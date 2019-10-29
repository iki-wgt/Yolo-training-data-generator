import os
import sys
import utils
import objects

try:
    import xml.etree.cElementTree as ET
except ImportError:
    import xml.etree.ElementTree as ET


def read_data_xml(xml_path):
    """
        Extract all information for image generation out of an XML file at the given path. This concerns general
        information and filter chain information, where each of them get stored into a seperate object.

        :param xml_path: path to XML file
        :return:
            general_struct: object containing general information
            filter_chain_list: object containing filter chains
    """
    try:
        tree = ET.ElementTree(file=xml_path)
    except FileNotFoundError:
        utils.logging.error("Error: Die Datei '%s' existiert nicht" % xml_path)
        sys.exit()
    data = tree.getroot()
    general_struct = objects.GeneralSettings()
    filter_chain_list = []

    for child in data:
        # get the general information
        if child.tag == "general":
            for child2 in child:
                if child2.tag == "obj_path":
                    general_struct.object_path = utils.to_str(child2.text)
                elif child2.tag == "bg_path":
                    general_struct.background_path = utils.to_str(child2.text)
                elif child2.tag == "output_path":
                    general_struct.output_path = utils.to_str(child2.text)
                elif child2.tag == "output_height":
                    general_struct.output_height = utils.to_int(child2.text)
                elif child2.tag == "output_width":
                    general_struct.output_width = utils.to_int(child2.text)
                elif child2.tag == "similar_path":
                    pass
                elif child2.tag == "overlap_path":
                    pass
                else:
                    unknown_element(child2.tag, child.tag)

        # find all filters
        elif child.tag == "filter":
            # iterate through all settings by the given number
            for x in range(utils.to_int(child.get("num"))):
                # create a new filter
                filter_chain_struct = objects.FilterChainSettings()
                # extract range containing number of objects which should be placed onto each generated image
                if child.get("num_obj_per_img") is not None:
                    filter_chain_struct.num_obj_per_img = child.get("num_obj_per_img")
                for child2 in child:
                    if child2.tag == "background":
                        filter_chain_struct.background = objects.BackgroundFilter(utils.to_str(child2.get("type")),
                                                                                  utils.to_str(child2.text))
                    elif child2.tag == "translate":
                        filter_chain_struct.translate = objects.TranslateFilter(utils.to_str(child2.find('x').text),
                                                                                utils.to_str(child2.find('y').text))
                    elif child2.tag == "rotate":
                        filter_chain_struct.rotate = objects.RotateFilter(utils.to_str(child2.text))
                    elif child2.tag == "scale":
                        filter_chain_struct.scale = objects.ScaleFilter(utils.to_str(child2.text))
                    elif child2.tag == "clip":
                        x_clip = utils.to_str(child2.find('x').text) if child2.find('x') is not None else '0:0'
                        y_clip = utils.to_str(child2.find('y').text) if child2.find('y') is not None else '0:0'
                        filter_chain_struct.clip = objects.ClipFilter(x_clip, y_clip)

                    elif child2.tag == "noise":
                        filter_chain_struct.noise = objects.NoiseFilter(utils.to_str(child2.get("target")),
                                                                        utils.to_str(child2.text))
                    elif child2.tag == "hue":
                        filter_chain_struct.hue = objects.HueFilter(utils.to_str(child2.get("target")),
                                                                    utils.to_str(child2.text))
                    elif child2.tag == "saturation":
                        filter_chain_struct.saturation = objects.SaturationFilter(utils.to_str(child2.get("target")),
                                                                                  utils.to_str(child2.text))
                    elif child2.tag == "value":
                        filter_chain_struct.value = objects.ValueFilter(utils.to_str(child2.get("target")),
                                                                        utils.to_str(child2.text))
                    elif child2.tag == "brightness":
                        filter_chain_struct.brightness = objects.BrightnessFilter(utils.to_str(child2.get("target")),
                                                                                  utils.to_str(child2.text))
                    elif child2.tag == "contrast":
                        filter_chain_struct.contrast = objects.ContrastFilter(utils.to_str(child2.get("target")),
                                                                              utils.to_str(child2.text))
                    elif child2.tag == "gamma":
                        filter_chain_struct.gamma = objects.GammaFilter(utils.to_str(child2.get("target")),
                                                                        utils.to_str(child2.text))
                    elif child2.tag == "blur":
                        filter_chain_struct.blur = objects.BlurFilter(utils.to_str(child2.get("target")),
                                                                      utils.to_str(child2.text))
                    elif child2.tag == "add_obj":
                        # collect filters for add_obj
                        translate = objects.TranslateFilter(utils.to_str(child2.find("translate").find('x').text),
                                                            utils.to_str(child2.find("translate").find('y').text)) if child2.find("translate") is not None else objects.TranslateFilter('50:50', '50:50')
                        scale = objects.ScaleFilter(utils.to_str(child2.find("scale").text)) if child2.find("scale") is not None else None
                        rotate = objects.RotateFilter(utils.to_str(child2.find("rotate").text)) if child2.find("rotate") is not None else None
                        if child2.find("clip") is not None:
                            clip_x_range = utils.to_str(child2.find("clip").find('x').text) if child2.find("clip").find('x') is not None else '0:0'
                            clip_y_range = utils.to_str(child2.find("clip").find('y').text) if child2.find("clip").find('y') is not None else '0:0'
                            clip = objects.ClipFilter(clip_x_range, clip_y_range)
                        else:
                            clip = None
                        noise = objects.NoiseFilter("1", utils.to_str(child2.find("noise").text)) if child2.find("noise") is not None else None
                        hue = objects.HueFilter("1", utils.to_str(child2.find("hue").text)) if child2.find("hue") is not None else None
                        saturation = objects.SaturationFilter("1", utils.to_str(child2.find("saturation").text)) if child2.find("saturation") is not None else None
                        value = objects.ValueFilter("1", utils.to_str(child2.find("value").text)) if child2.find("value") is not None else None
                        brightness = objects.BrightnessFilter("1", utils.to_str(child2.find("brightness").text)) if child2.find("brightness") is not None else None
                        contrast = objects.ContrastFilter("1", utils.to_str(child2.find("contrast").text)) if child2.find("contrast") is not None else None
                        gamma = objects.GammaFilter("1", utils.to_str(child2.find("gamma").text)) if child2.find("gamma") is not None else None
                        blur = objects.BlurFilter("1", utils.to_str(child2.find("blur").text)) if child2.find("blur") is not None else None
                        overlay = objects.OverlayFilter(utils.to_str(child2.find("overlay").get("filter")), "1",
                                                        utils.to_float(child2.find("overlay").get("intensity")),
                                                        utils.to_str(child2.find("overlay").find("overlay_img_path").get("path_type")),
                                                        utils.to_str(child2.find("overlay").find("overlay_img_path").text)) if child2.find("overlay") is not None else None
                        filter_chain_struct.add_obj = objects.AddObjectFilter(num_obj_range_str=utils.to_str(child2.get("num")),
                                                                              path_type=utils.to_str(child2.find("add_obj_img_path").get("path_type")),
                                                                              add_obj_path=utils.to_str(child2.find("add_obj_img_path").text),
                                                                              translate=translate,
                                                                              scale=scale,
                                                                              rotate=rotate,
                                                                              clip=clip,
                                                                              noise=noise,
                                                                              hue=hue,
                                                                              saturation=saturation,
                                                                              value=value,
                                                                              brightness=brightness,
                                                                              contrast=contrast,
                                                                              gamma=gamma,
                                                                              blur=blur,
                                                                              overlay=overlay)
                    elif child2.tag == "overlap":
                        scale = objects.ScaleFilter(utils.to_str(child2.find("scale").text)) if child2.find("scale") is not None else None
                        rotate = objects.RotateFilter(utils.to_str(child2.find("rotate").text)) if child2.find("rotate") is not None else None
                        noise = objects.NoiseFilter("1", utils.to_str(child2.find("noise").text)) if child2.find("noise") is not None else None
                        hue = objects.HueFilter("1", utils.to_str(child2.find("hue").text)) if child2.find("hue") is not None else None
                        saturation = objects.SaturationFilter("1", utils.to_str(child2.find("saturation").text)) if child2.find("saturation") is not None else None
                        value = objects.ValueFilter("1", utils.to_str(child2.find("value").text)) if child2.find("value") is not None else None
                        brightness = objects.BrightnessFilter("1", utils.to_str(child2.find("brightness").text)) if child2.find("brightness") is not None else None
                        contrast = objects.ContrastFilter("1", utils.to_str(child2.find("contrast").text)) if child2.find("contrast") is not None else None
                        gamma = objects.GammaFilter("1", utils.to_str(child2.find("gamma").text)) if child2.find("gamma") is not None else None
                        blur = objects.BlurFilter("1", utils.to_str(child2.find("blur").text)) if child2.find("blur") is not None else None
                        overlay = objects.OverlayFilter(utils.to_str(child2.find("overlay").get("filter")), "1",
                                                                            utils.to_float(child2.find("overlay").get("intensity")),
                                                                            utils.to_str(child2.find("overlay").find("overlay_img_path").get("path_type")),
                                                                            utils.to_str(child2.find("overlay").find("overlay_img_path").text)) if child2.find("overlay") is not None else None
                        filter_chain_struct.overlap = objects.OverlapFilter(type=utils.to_str(child2.get("type")),
                                                                            path_type=utils.to_str(child2.find("overlap_img_path").get("path_type")),
                                                                            overlap_img_path=utils.to_str(child2.find("overlap_img_path").text),
                                                                            x_pct_range=utils.to_str(child2.find("x_percentage").text),
                                                                            y_pct_range=utils.to_str(child2.find("y_percentage").text),
                                                                            scale=scale,
                                                                            rotate=rotate,
                                                                            noise=noise,
                                                                            hue=hue,
                                                                            saturation=saturation,
                                                                            value=value,
                                                                            brightness=brightness,
                                                                            contrast=contrast,
                                                                            gamma=gamma,
                                                                            blur=blur,
                                                                            overlay=overlay)
                    elif child2.tag == "overlay":
                        filter_chain_struct.overlay = objects.OverlayFilter(utils.to_str(child2.get("filter")),
                                                                            utils.to_str(child2.get("target")),
                                                                            utils.to_float(child2.get("intensity")),
                                                                            utils.to_str(child2.find("overlay_img_path").get("path_type")),
                                                                            utils.to_str(child2.find("overlay_img_path").text))
                    elif child2.tag == "resolution":
                        filter_chain_struct.resolution = objects.ResolutionFilter(utils.to_str(child2.find('x').text),
                                                                                  utils.to_str(child2.find('y').text))
                    else:
                        unknown_element(child2.tag, child.tag)

                # append filter to list
                filter_chain_list.append(filter_chain_struct)

        else:
            unknown_element(child.tag, data)

    utils.logging.info("XML-Parser: object path: %s", os.path.abspath(general_struct.object_path))
    utils.logging.info("XML-Parser: background path: %s", os.path.abspath(general_struct.background_path))
    utils.logging.info("XML-Parser: output path: %s", os.path.abspath(general_struct.output_path))
    utils.logging.info("XML-Parser: found %d filter chain(s)\n", len(filter_chain_list))
    return general_struct, filter_chain_list


def read_class_xml(xml_path):
    """
        Extract all information out of the class XML file at the given path. This file contains all object class names
        and object class IDs mapped to each input object image. This method returns a dict containing the mapping of
        class IDs to class name and the mapping of file names with their corresponding class ID.

        :param xml_path: path to the class XML file
        :return:
            class_id_to_name_dict: dict containing mapping of class IDs to class name
            img_name_to_class_id_dict: dict containing mapping of file names with their corresponding class ID
    """
    try:
        tree = ET.ElementTree(file=xml_path)
    except FileNotFoundError:
        utils.logging.error("Error: Die Datei '%s' existiert nicht" % xml_path)
        sys.exit()
    data = tree.getroot()

    class_id_to_name_dict = dict()
    img_name_to_class_id_dict = dict()
    for child in data:
        # get the general information
        if child.tag == "class":
            class_id_to_name_dict[child.get("class")] = child.get("name")
        if child.tag == "picture":
            img_name_to_class_id_dict[child.text] = child.get("class")

    return class_id_to_name_dict, img_name_to_class_id_dict


def unknown_element(child, parent):
    """
        Throw an error when an XML file contains an unknown element.

        :param child: name of unknown element
        :param parent: name of parent of unknown element
        :return:
    """
    utils.logging.error("ERROR: unknown child-element '%s' in '%s'", child, parent)
    sys.exit(-1)
