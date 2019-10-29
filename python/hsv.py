import os
import utils
import logging
from objects import HSV

# pickle path settings
PICKLE_FILES_FOLDER = "pickle_files"
HSV_PICKLE_FILENAME = 'hsv'
HSV_PICKLE_PATH = os.path.join(utils.get_python_project_path(), PICKLE_FILES_FOLDER, HSV_PICKLE_FILENAME)


def get_hsv_std_values():
    """
        Import and return stored HSV value object from '/pickle_files/hsv.pickle'. If no such file exists, return HSV
        object with default values.

        :return:
    """
    hsv_std_values = utils.read_from_pickle(HSV_PICKLE_PATH)
    if not hsv_std_values:
        return HSV()
    else:
        return hsv_std_values


def set_hsv_std_values(hsv_object):
    """
        Save given HSV object into '/pickle_files/hsv.pickle'.

        :param hsv_object: contains min and max values for hue, saturation and value.
        :return:
    """
    utils.save_to_pickle(HSV_PICKLE_PATH, _object=hsv_object)
    logging.info("Data saved to '%s.pickle'" % HSV_PICKLE_PATH)
