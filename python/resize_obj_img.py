import os
import sys
import utils
import logging
import img_processing
from tkinter import filedialog
from tkinter import simpledialog


def create_folder(folder_path):
    """
        Create a new folder given by folder_path.

        :param folder_path: path to new folder
        :return:
    """
    if not os.path.exists(folder_path):
        os.makedirs(folder_path)
        logging.info("Created folder \'%s\'" % folder_path)
    else:
        logging.info("Folder \'%s\' already exists" % folder_path)


def main():
    """
        Resize all images inside the selected directory to the desired new width retaining original aspect ratio.

        :return:
    """
    # setup logging
    utils.setup_logging()

    # select path to object images
    dirname = os.path.dirname(__file__)
    initial_dir = os.path.join(dirname, os.path.realpath('../images/'))
    obj_img_path = filedialog.askdirectory(initialdir=initial_dir, title="Select object image directory")

    # ask desired resolution
    img_width_str = simpledialog.askstring("Resolution input", "Enter desired image width")
    if not img_width_str:
        sys.exit()
    img_width = utils.to_int(img_width_str)

    # create folder for resized images
    foldername = "resized_width_%s" % img_width_str
    folder_path = os.path.join(obj_img_path, foldername)
    create_folder(folder_path)

    # get object images
    obj_img_filename_list = utils.list_directory(obj_img_path, ['.jpg', '.png'])

    # resize object images
    counter = 1
    total = len(obj_img_filename_list)
    for obj_img_filename in obj_img_filename_list:
        obj_img = img_processing.import_image(os.path.join(obj_img_path, obj_img_filename))
        obj_img_resized = img_processing.resize_image_aspect_ratio(obj_img, new_width=img_width)
        img_processing.save_image(obj_img_resized, folder_path, obj_img_filename)
        logging.info(   "=> %s%% done" % str(int((counter/total)*100)))
        counter += 1

    logging.info("\n\nSuccessfully resized all images inside \'%s\' to width %s" % (obj_img_path, img_width))


if __name__ == '__main__':
    main()
