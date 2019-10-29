import os
import sys
import time
import utils
import random
import logging
import numpy as np


class YoloGenerator:

    def __init__(self, yolo_version=2):
        """
            Setup all constants for the creation of YOLO output files.

            :param yolo_version: number of desired YOLO version
        """
        # YOLO file names
        self.TRAIN_TXT_FILENAME = 'train.txt'
        self.TEST_TXT_FILENAME = 'test.txt'
        self.OBJ_DATA_FILENAME = 'obj.data'
        self.OBJ_NAMES_FILENAME = 'obj.names'
        self.YOLO_CFG_FILENAME = 'yolo-obj.cfg'

        # Directories where different YOLO data will reside, relative to 'darknet.exe'
        self.TRAIN_TEST_TXT_PATH = 'own_dataset/cfg/'
        self.TRAIN_DATA_PATH = 'own_dataset/train_data/'
        self.CFG_PATH = 'own_dataset/cfg/'
        self.WEIGHTS_PATH = 'own_dataset/weights/'

        # Constants for anchor calculation
        self.WIDTH_IN_CFG_FILE = 416
        self.HEIGHT_IN_CFG_FILE = 416

        # File paths for templates
        self.TEMPLATE_PATH = os.path.join(utils.get_python_project_path(), "templates")

        # Timeout for searching anchors with KMeans
        self.KMEANS_TIMEOUT = 10

        # Set constants dependent on YOLO version
        if yolo_version == 2:
            self.NUM_CLUSTERS = 5
            self.YOLO_CFG_FILE_SOURCE = 'yolov2-obj.cfg'
            self.ANCHOR_DIVISION = 32.
            self.FILTER_CALCULATION_FORMULA = lambda x: (x+5)*5
        elif yolo_version == 3:
            self.NUM_CLUSTERS = 9
            self.YOLO_CFG_FILE_SOURCE = 'yolov3-obj.cfg'
            self.ANCHOR_DIVISION = 1.
            self.FILTER_CALCULATION_FORMULA = lambda x: (x+5)*3
        else:
            logging.error("ERROR: Unknown YOLO version \'%s\'" % yolo_version)
            sys.exit()

    @staticmethod
    def convert_bbox_to_yolo_format(bbox, img_shape):
        """
            Convert information of BoundingBox object into YOLO format: x_min, x_max, y_min, y_max coordinates to
            x, y coordinate, width, height relative to image shape.

            :param bbox: BoundingBox object
            :param img_shape: shape of the image containing an object with a bounding box
            :return:
                x: x coordinate of bounding box in image relative to image width
                y: y coordinate of bounding box in image relative to image height
                w: width of bounding box relative to image width
                h: height of bounding box relative to image height
        """
        img_width = 1. / img_shape[1]
        img_height = 1. / img_shape[0]
        x = (bbox.x_min + bbox.x_max) / 2.0
        y = (bbox.y_min + bbox.y_max) / 2.0
        w = bbox.x_max - bbox.x_min
        h = bbox.y_max - bbox.y_min
        x = x * img_width
        w = w * img_width
        y = y * img_height
        h = h * img_height
        return x, y, w, h

    @staticmethod
    def iou(x, centroids):
        """
            Calculate intersection over union.

            :param x: elements of annotation dimensions
            :param centroids: centroid objects
            :return: numpy array of all found similarities
        """
        similarities = []
        for centroid in centroids:
            c_w, c_h = centroid
            w, h = x
            if c_w >= w and c_h >= h:
                similarity = w*h/(c_w*c_h)
            elif c_w >= w and c_h <= h:
                similarity = w*c_h/(w*h + (c_w-w)*c_h)
            elif c_w <= w and c_h >= h:
                similarity = c_w*h/(w*h + c_w*(c_h-h))
            else:  # means both w,h are bigger than c_w and c_h respectively
                similarity = (c_w*c_h)/(w*h)
            similarities.append(similarity)  # will become (k,) shape
        return np.array(similarities)

    def calc_anchors(self, centroids):
        """
            Calculate anchors with the given centroids.

            :param centroids: centroid objects
            :return: string of all generated anchors rounded to 2 decimals
        """
        anchors = centroids.copy()

        for i in range(anchors.shape[0]):
            anchors[i][0] *= utils.to_float(self.WIDTH_IN_CFG_FILE) / self.ANCHOR_DIVISION
            anchors[i][1] *= utils.to_float(self.HEIGHT_IN_CFG_FILE) / self.ANCHOR_DIVISION

        # sort anchors
        widths = anchors[:, 0]
        sorted_indices = np.argsort(widths)

        # write all anchor chords into a string
        anchors_str = ''
        for i in sorted_indices[:-1]:
            anchors_str += ('%0.2f,%0.2f, ' % (anchors[i, 0], anchors[i, 1]))

        # add last coords without a comma at the end
        anchors_str += ('%0.2f,%0.2f' % (anchors[sorted_indices[-1:], 0], anchors[sorted_indices[-1:], 1]))

        return anchors_str

    def kmeans(self, X, centroids):
        """
            Find appropriate anchors with aid of KMeans algorithm.

            :param X: annotation dimensions
            :param centroids: centroid objects
            :return: string of all generated anchors rounded to 2 decimals
        """
        N = X.shape[0]
        k, dim = centroids.shape
        prev_assignments = np.ones(N)*(-1)
        iter = 0

        start = time.time()
        end = time.time()

        while True:
            # if finding anchors takes to long time, then return None
            if end-start > self.KMEANS_TIMEOUT:
                return None

            D = []
            iter += 1
            for i in range(N):
                d = 1 - self.iou(X[i], centroids)
                D.append(d)
            D = np.array(D)  # D.shape = (N,k)

            # assign samples to centroids
            assignments = np.argmin(D, axis=1)

            if (assignments == prev_assignments).all():
                anchors_str = self.calc_anchors(centroids)
                return anchors_str

            # calculate new centroids
            centroid_sums = np.zeros((k, dim), np.float)
            for i in range(N):
                centroid_sums[assignments[i]] += X[i]
            for j in range(k):
                centroids[j] = centroid_sums[j]/(np.sum(assignments == j))

            prev_assignments = assignments.copy()

            end = time.time()

    def gen_anchors(self, output_path, num_clusters):
        """
            Generate anchors, which tell YOLO in which scale objects are, which need to be trained and detected.

            :param output_path: path, where YOLO output files should be stored
            :param num_clusters: number of clusters need to be found
            :return: string of all generated anchors rounded to 2 decimals
        """
        np.seterr(divide='ignore', invalid='ignore')
        train_txt_path = os.path.join(output_path, self.TRAIN_TXT_FILENAME)
        f = utils.open_file(train_txt_path)

        lines = [line.rstrip('\n') for line in f.readlines()]

        annotation_dims = []

        for line in lines:
            line = line.replace('JPEGImages', 'labels')
            line = line.replace('.jpg', '.txt')
            line = line.replace('.png', '.txt')
            yolo_img_txt_filename = os.path.join(output_path, line.split('/')[-1])  # extract filenames out of 'train.txt'
            f2 = utils.open_file(yolo_img_txt_filename)
            for line in f2.readlines():
                line = line.rstrip('\n')
                w, h = line.split(' ')[3:]
                annotation_dims.append(tuple(map(float, (w, h))))
        annotation_dims = np.array(annotation_dims)

        if num_clusters == 0:
            for num_clusters_ in range(1, 11):  # we make 1 through 10 clusters
                anchors_str = None
                # kmeans returns None, if no anchors are found, then try it with new random indices
                while anchors_str is None:
                    indices = [random.randrange(annotation_dims.shape[0]) for i in range(num_clusters_)]
                    centroids = annotation_dims[indices]
                    anchors_str = self.kmeans(annotation_dims, centroids)
        else:
            anchors_str = None
            # kmeans returns None, if no anchors are found, then try it with new random indices
            while anchors_str is None:
                indices = [random.randrange(annotation_dims.shape[0]) for i in range(num_clusters)]
                centroids = annotation_dims[indices]
                anchors_str = self.kmeans(annotation_dims, centroids)

        return anchors_str

    def generate_yolo_output(self, obj_list, img_shape, output_path, filename):
        """
            Generate a text file for a given generated image, which contains its object class ID and the bounding box
            surrounding the object.

            :param obj_list: list of objects containing category numbers and bounding boxes surrounding object
            :param img_shape: shape of the generated image
            :param output_path: path, where YOLO output files should be stored
            :param filename: name of the generated YOlO file
            :return:
        """
        logging.info("   => Generating YOLO file \'%s\'" % (filename+'.txt'))
        content_str = ''
        for obj in obj_list:
            x, y, w, h = self.convert_bbox_to_yolo_format(obj.bbox, img_shape)
            content_str += ' '.join([str(obj.category), str(x), str(y), str(w), str(h)]) + '\n'
        utils.create_txt_file(output_path, filename, content_str)

    def generate_train_data_file(self, output_path):
        """
            Generate 'train.txt' file containing paths to all images, on which YOLO gets trained later.

            :param output_path: path, where YOLO output files should be stored
            :return:
        """
        logging.info("Generating YOLO text file \'%s\' to \'%s\'" % (self.TRAIN_TXT_FILENAME, output_path))
        content_str = ''
        filename_list = utils.list_directory(output_path, [".jpg"])
        for curr_filename in filename_list[:-1]:
            content_str += "%s%s\n" % (self.TRAIN_DATA_PATH, curr_filename)
        content_str += "%s%s" % (self.TRAIN_DATA_PATH, filename_list[-1])  # last element without newline
        filename = self.TRAIN_TXT_FILENAME.split('.')[0]
        utils.create_txt_file(output_path, filename, content_str)

    def generate_obj_names_file(self, output_path, class_id_to_name_dict):
        """
            Generate 'obj.names' file containing the mapping of all object class names to their corresponding object
            class ID.

            :param output_path: path, where YOLO output files should be stored
            :param class_id_to_name_dict: dict containing mapping of class IDs to class name
            :return:
        """
        logging.info("Generating YOLO text file \'%s\' to \'%s\'" % (self.OBJ_NAMES_FILENAME, output_path))
        # generate 'obj.names'
        content_str = ''
        key_list = list(class_id_to_name_dict.keys())
        for class_id in key_list[:-1]:
            content_str += '%s\n' % class_id_to_name_dict[class_id]
        content_str += '%s' % class_id_to_name_dict[key_list[-1]]  # last element without newline
        filename, file_extension = self.OBJ_NAMES_FILENAME.split('.')
        utils.create_txt_file(output_path, filename, content_str, file_ext=file_extension)

    def generate_obj_data_file(self, output_path, num_classes):
        """
            Generate 'obj.data' file containing information about number of object classes, location of 'train.txt',
            'test.txt', 'obj.names' and to the directory, where generated weights should be stored.

            :param output_path: path, where YOLO output files should be stored
            :param num_classes: number of object classes
            :return:
        """
        logging.info("Generating YOLO text file \'%s\' to \'%s\'" % (self.OBJ_DATA_FILENAME, output_path))
        # generate 'obj.data'
        content_str = "classes = %s\n" % num_classes
        content_str += "train = %s%s\n" % (self.TRAIN_TEST_TXT_PATH, self.TRAIN_TXT_FILENAME)
        content_str += "valid = %s%s\n" % (self.TRAIN_TEST_TXT_PATH, self.TEST_TXT_FILENAME)
        content_str += "names = %s%s\n" % (self.CFG_PATH, self.OBJ_NAMES_FILENAME)
        content_str += "backup = %s" % self.WEIGHTS_PATH
        filename, file_extension = self.OBJ_DATA_FILENAME.split('.')
        utils.create_txt_file(output_path, filename, content_str, file_ext=file_extension)

    def generate_yolo_obj_cfg(self, output_path, num_classes):
        """
            Generate 'yolo-obj.cfg' file, which contains all important information about the training and testing of
            the object, which should be trained and tested. For this a general 'yolo-obj.cfg' file is used as a
            foundation, in which specific information about the generated object images (anchors, number of object
            classes, number of filters, neural network resolution) is inserted.

            :param output_path: path, where YOLO output files should be stored
            :param num_classes: number of object classes
            :return:
        """
        logging.info("Generating \'%s\' to \'%s\'" % (self.YOLO_CFG_FILENAME, output_path))

        # prepare information
        anchors_str = self.gen_anchors(output_path, self.NUM_CLUSTERS)
        classes_str = str(num_classes)
        filters_str = str(self.FILTER_CALCULATION_FORMULA(num_classes))
        width_str = str(self.WIDTH_IN_CFG_FILE)
        height_str = str(self.HEIGHT_IN_CFG_FILE)

        # get template content of 'yolo-obj.cfg'
        cfg_file_content = utils.read_file(os.path.join(self.TEMPLATE_PATH, self.YOLO_CFG_FILE_SOURCE))

        # replace strings in cfg file
        cfg_file_content = cfg_file_content.replace("%anchors%", anchors_str)
        cfg_file_content = cfg_file_content.replace("%num_classes%", classes_str)
        cfg_file_content = cfg_file_content.replace("%num_filters%", filters_str)
        cfg_file_content = cfg_file_content.replace("%width%", width_str)
        cfg_file_content = cfg_file_content.replace("%height%", height_str)

        # save file into output directory
        utils.write_file(os.path.join(output_path, self.YOLO_CFG_FILENAME), cfg_file_content)
