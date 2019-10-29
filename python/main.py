import os
import sys
import utils
import logging
import argparse
import xml_parser as xml
import img_generator as img_gen

YOLO_VERSIONS = (2, 3)


def main():
    """
        Main method of Image-Generator.

        :return:
    """
    # setup logging
    utils.setup_logging()

    # setup argument parsing
    parser = argparse.ArgumentParser()
    parser.add_argument('data_xml', default='data.xml', help="relative path to 'data.xml'")
    parser.add_argument('-yv', '--yolo_version', help='set desired version of YOLO', type=int, default=2)
    parser.add_argument('-b', '--bounding_boxes', help='draw bounding boxes into output images', action='store_true')
    parser.add_argument('-ny', '--no_yolo_output', help='don\'t generate YOLO output files', action='store_true')

    args = parser.parse_args()

    if not args.data_xml.endswith('.xml'):  # given file must be XML
        logging.error("Error parsing program argument: name of \'data.xml\' must end with \'.xml\'")
        sys.exit()
    if len(args.data_xml) < 5:  # XML file must contain a file name
        logging.error("Error parsing program argument: XML file doesn\'t contain a file name")
        sys.exit()
    if args.yolo_version not in YOLO_VERSIONS:  # check valid YOLO version
        logging.error("Error parsing program argument: YOLO version can only be %s" % list(YOLO_VERSIONS))

    # set xml file names
    class_xml_name = 'class.xml'

    # read data.xml
    data_xml_name = os.path.join(utils.get_project_path(), args.data_xml)
    general_struct, filter_list = xml.read_data_xml(data_xml_name)

    # read class.xml inside object directory
    class_xml_path = os.path.join(general_struct.object_path, class_xml_name)
    class_id_to_name_dict, img_name_to_class_id_dict = xml.read_class_xml(class_xml_path)

    # generate images
    img_gen.generate(general_struct, filter_list, class_id_to_name_dict, img_name_to_class_id_dict, args.yolo_version, draw_bounding_boxes=args.bounding_boxes, no_yolo_output=args.no_yolo_output)


if __name__ == '__main__':
    main()
