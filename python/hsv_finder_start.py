import os
import utils
import argparse
import hsv_finder_qt


def main():
    """
        Setup output logging, process program arguments and start HSV-Finder application.

        :return:
    """
    # setup logging
    utils.setup_logging()

    # setup argument parsing
    parser = argparse.ArgumentParser()
    parser.add_argument('-p', '--path', help='set path in which greenscreen object images reside', type=str, default=None)
    args = parser.parse_args()

    if args.path:
        obj_img_path = os.path.abspath(args.path)
    else:
        obj_img_path = None
    hsv_finder_qt.start_hsv_finder(obj_img_path)


if __name__ == '__main__':
    main()
