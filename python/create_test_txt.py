import os
import utils
import logging
from tkinter import filedialog


OUTPUT_FILENAME = 'test'
DARKNET_IMG_DIRECTORY = '/data/img/'


def main():
    """
        This method creates the YOLO file 'test.txt' containing all file names inside the given directory. YOLO needs
        this file to know all images, which are necessary to test detection performance.

        :return:
    """
    # setup logging
    utils.setup_logging()

    # setup file dialog
    dirname = os.path.dirname(__file__)
    initial_dir = os.path.join(dirname, os.path.realpath('../'))

    # select path to object images
    test_images_path = filedialog.askdirectory(initialdir=initial_dir, title="Select image directory")
    test_img_path = os.path.join(test_images_path).replace('\\', '/')

    # select path to output directory
    output_path = os.path.join(test_images_path)

    # collect all file names inside given directory
    content_str = ''
    filename_list = utils.list_directory(test_img_path, [".jpg"])

    # merge file names with darknet folder structure
    for curr_filename in filename_list[:-1]:
        content_str += "%s\n" % os.path.join(DARKNET_IMG_DIRECTORY, curr_filename)
    content_str += "%s" % os.path.join(DARKNET_IMG_DIRECTORY, filename_list[-1])
    utils.create_txt_file(output_path, OUTPUT_FILENAME, content_str)

    logging.info("Successfully created \'test.txt\' in \'%s\'" % output_path)


if __name__ == '__main__':
    main()
