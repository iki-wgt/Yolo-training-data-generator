import os
import sys
import copy
import utils
import random
import logging
import img_processing
from objects import Object, TranslateFilter, ScaleFilter
from yolo_generator import YoloGenerator


# constants for filter targets
TARGET_FORE_AND_BACKGROUND = 0
TARGET_FOREGROUND = 1
TARGET_BACKGROUND = 2


def apply_scale_filter(obj_img, scale_filter):
    """
        Apply scale filter with given scale_filter object parameters.

        :param obj_img: image containing cropped object
        :param scale_filter: object containing scale parameters
        :return: obj_img: image with scaled object
    """
    if scale_filter.min == scale_filter.max:
        factor = utils.to_float(scale_filter.min) / 100
    else:
        factor = utils.to_float(random.randint(int(scale_filter.min), int(scale_filter.max))) / 100
    logging.info("   => scale object image with factor %3.2f" % factor)
    obj_img = img_processing.scale_img(obj_img, factor)
    return obj_img


def apply_rotate_filter(obj_img, rotate_filter):
    """
        Apply rotate filter with given rotate_filter object parameters.

        :param obj_img: image containing cropped object
        :param rotate_filter: object containing rotate parameters
        :return: image with rotated object
    """
    if rotate_filter.min == rotate_filter.max:
        degrees = rotate_filter.min
    else:
        degrees = random.randint(rotate_filter.min, rotate_filter.max)
    logging.info("   => rotate object image by %d degrees" % degrees)
    obj_img = img_processing.rotate_img(obj_img, degrees)
    return obj_img


def apply_translate_filter(translate_filter):
    """
        Apply translate filter with given translate_filter object parameters.

        :param translate_filter: object containing translate parameters
        :return: x_offset_factor, y_offset_factor: x and y translation values
    """
    if translate_filter.x_min == translate_filter.x_max:
        x_offset_factor = utils.to_float(translate_filter.x_min) / 100
    else:
        x_offset_factor = utils.to_float(random.randint(translate_filter.x_min, translate_filter.x_max)) / 100
    if translate_filter.y_min == translate_filter.y_max:
        y_offset_factor = utils.to_float(translate_filter.y_min) / 100
    else:
        y_offset_factor = utils.to_float(random.randint(translate_filter.y_min, translate_filter.y_max)) / 100
    logging.info("   => translate by x: %s and y: %s" % (x_offset_factor, y_offset_factor))
    return x_offset_factor, y_offset_factor


def apply_clip_filter(obj_img, clip_filter):
    """
        Apply clip filter with given clip_filter object parameters.

        :param obj_img: image containing cropped object
        :param clip_filter: object containing clip parameters
        :return:
            obj_img: image with clipped object
    """
    if clip_filter.x_min == clip_filter.x_max:
        x_factor = utils.to_float(clip_filter.x_min / 100)
    else:
        x_factor = utils.to_float((random.randint(clip_filter.x_min, clip_filter.x_max)) / 100)
    if clip_filter.y_min == clip_filter.y_max:
        y_factor = utils.to_float(clip_filter.y_min / 100)
    else:
        y_factor = utils.to_float((random.randint(clip_filter.y_min, clip_filter.y_max)) / 100)

    obj_img = img_processing.clip_img(obj_img, x_factor, y_factor)

    return obj_img


def apply_hsv_filter(hue_filter, sat_filter, val_filter, img):
    standard_value_list = [0, 50, 50]
    current_value_list = standard_value_list.copy()

    # hue
    if hue_filter:
        if hue_filter.min == hue_filter.max:
            current_value_list[0] = int(hue_filter.min)
        else:
            current_value_list[0] = random.randint(hue_filter.min, hue_filter.max)

    # saturation
    if sat_filter:
        if sat_filter.min == sat_filter.max:
            current_value_list[1] = int(sat_filter.min)
        else:
            current_value_list[1] = random.randint(sat_filter.min, sat_filter.max)

    # value
    if val_filter:
        if val_filter.min == val_filter.max:
            current_value_list[2] = int(val_filter.min)
        else:
            current_value_list[2] = random.randint(val_filter.min, val_filter.max)

    # process image
    if current_value_list != standard_value_list:  # filter on back- and foreground
        img = img_processing.change_hsv(img, current_value_list[0], current_value_list[1], current_value_list[2])

    return img

def apply_hue_filter(hue_filter, img):
    if hue_filter.min == hue_filter.max:
        hue = int(hue_filter.min)
    else:
        hue = random.randint(hue_filter.min, hue_filter.max)

    img = img_processing.change_hue(img, hue)
    return img


def apply_saturation_filter(sat_filter, img):
    if sat_filter.min == sat_filter.max:
        sat = int(sat_filter.min)
    else:
        sat = random.randint(sat_filter.min, sat_filter.max)

    img = img_processing.change_saturation(img, sat)
    return img


def apply_value_filter(val_filter, img):
    if val_filter.min == val_filter.max:
        val = int(val_filter.min)
    else:
        val = random.randint(val_filter.min, val_filter.max)

    img = img_processing.change_value(img, val)
    return img


def apply_bcg_filter(brightness_filter, contrast_filter, gamma_filter, obj_img, bg_img):
    """
        Apply BCG filter  with given brightness_filter, contrast_filter and gamma_filter parameters.

        :param brightness_filter: object containing brightness parameters
        :param contrast_filter: object containing contrast parameters
        :param gamma_filter: object containing gamma parameters
        :param obj_img: image containing cropped object
        :param bg_img: background image
        :return:
            bg_img, obj_img: dependent on target either background or object image or both get processed and returned
    """
    standard_value_list = [0, 0, 0.0]
    bcg_per_target_list = [standard_value_list.copy(), standard_value_list.copy(), standard_value_list.copy()]

    # brightness
    if brightness_filter:
        if brightness_filter.min == brightness_filter.max:
            bcg_per_target_list[brightness_filter.target][0] = int(brightness_filter.min)
        else:
            bcg_per_target_list[brightness_filter.target][0] = random.randint(brightness_filter.min, brightness_filter.max)

    # contrast
    if contrast_filter:
        if contrast_filter.min == contrast_filter.max:
            bcg_per_target_list[contrast_filter.target][1] = int(contrast_filter.min)
        else:
            bcg_per_target_list[contrast_filter.target][1] = random.randint(contrast_filter.min, contrast_filter.max)

    # gamma
    if gamma_filter:
        if gamma_filter.min == gamma_filter.max:
            bcg_per_target_list[gamma_filter.target][2] = float(gamma_filter.min)
        else:
            bcg_per_target_list[gamma_filter.target][2] = random.uniform(gamma_filter.min, gamma_filter.max)

    # choose target and process image(s)
    if bcg_per_target_list[0] != standard_value_list:  # filter on back- and foreground
        logging.info("   => apply BCG filter on back- and foreground with b=%s, c=%s, g=%s" % (bcg_per_target_list[0][0], bcg_per_target_list[0][1], bcg_per_target_list[0][2]))
        bg_img = img_processing.change_bcg(bg_img, bcg_per_target_list[0][0], bcg_per_target_list[0][1], bcg_per_target_list[0][2])
        obj_img = img_processing.change_bcg(obj_img, bcg_per_target_list[0][0], bcg_per_target_list[0][1], bcg_per_target_list[0][2])
    if bcg_per_target_list[1] != standard_value_list:  # only on foreground
        logging.info("   => apply BCG filter only on foreground with b=%s, c=%s, g=%s" % (bcg_per_target_list[1][0], bcg_per_target_list[1][1], bcg_per_target_list[1][2]))
        obj_img = img_processing.change_bcg(obj_img, bcg_per_target_list[1][0], bcg_per_target_list[1][1], bcg_per_target_list[1][2])
    if bcg_per_target_list[2] != standard_value_list:  # only on background
        logging.info("   => apply BCG filter only on background with b=%s, c=%s, g=%s" % (bcg_per_target_list[2][0], bcg_per_target_list[2][1], bcg_per_target_list[2][2]))
        bg_img = img_processing.change_bcg(bg_img, bcg_per_target_list[2][0], bcg_per_target_list[2][1], bcg_per_target_list[2][2])
    return bg_img, obj_img


def apply_brightness_filter(brightness_filter, img):
    if brightness_filter.min == brightness_filter.max:
        brightness = int(brightness_filter.min)
    else:
        brightness = random.randint(brightness_filter.min, brightness_filter.max)

    img = img_processing.change_brightness(img, brightness)
    return img


def apply_contrast_filter(contrast_filter, img):
    if contrast_filter.min == contrast_filter.max:
        contrast = int(contrast_filter.min)
    else:
        contrast = random.randint(contrast_filter.min, contrast_filter.max)

    img = img_processing.change_contrast(img, contrast)
    return img


def apply_gamma_filter(gamma_filter, img):
    if gamma_filter.min == gamma_filter.max:
        gamma = float(gamma_filter.min)
    else:
        gamma = random.uniform(gamma_filter.min, gamma_filter.max)

    img = img_processing.change_gamma(img, gamma)
    return img


def apply_noise_filter(noise_filter, img):
    """
        Apply noise filter with given noise_filter parameters.

        :param noise_filter: object containing noise parameters
        :param img: passed image on which noise filter should be applied
        :return: img: processed image overlayed by noise
    """
    # get noise factor
    if noise_filter.min == noise_filter.max:
        noise_factor_int = int(noise_filter.min)
    else:
        noise_factor_int = random.randint(noise_filter.min, noise_filter.max)
    noise_factor = float(noise_factor_int) / 1000
    logging.info("   => noise filter with factor %s" % noise_factor)
    img = img_processing.salt_and_pepper_noise(img, noise_factor)
    return img


def apply_blur_filter(blur_filter, img):
    """
        Apply blur filter with given blur_filter parameters.

        :param blur_filter: object containing blur parameters
        :param img: passed image on which blur filter should be applied
        :return: img: processed blurred image
    """
    # get blur factor
    if blur_filter.min == blur_filter.max:
        blur_factor = int(blur_filter.min)
    else:
        blur_factor = random.randint(blur_filter.min, blur_filter.max)

    # return if blur factor is 0
    if blur_factor == 0:
        return img

    # calc blur factor considering image resolution
    kernel_size = utils.to_int(min(img[:, :, 0].shape) * blur_factor/3333)

    logging.info("   => apply blur filter with factor %s" % kernel_size)
    img = img_processing.blur_img(img, kernel_size)
    return img


def apply_background_filter(background_filter, res_x, res_y, background_path):
    """
        Apply background filter with given background_filter parameters, desired x and y resolution and the path to
        background directory. Dependent on filter type, the background consists of one given single color, a random
        color, a desired specified image, a random image out of set standard background directory or a random image out
        of another given directory.

        :param background_filter: object containing background parameters
        :param res_x: x resolution of output background image
        :param res_y: y resolution of output background image
        :param background_path: path to set standard background directory
        :return: bg_img: created background image with desired resolution
    """
    if background_filter.type == 0:  # fixed hex color
        # background_filter.value has format "0x00000000"
        r_int = utils.hex_to_int(background_filter.value[2:4])
        g_int = utils.hex_to_int(background_filter.value[4:6])
        b_int = utils.hex_to_int(background_filter.value[6:8])
        alpha_int = utils.hex_to_int(background_filter.value[8:10])
        bg_img = img_processing.create_colored_background_img(res_x, res_y, r_int, g_int, b_int)
        logging.info("   => set background to: r: %s, g: %s, b: %s, alpha: %s" % (r_int, g_int, b_int, alpha_int))
    elif background_filter.type == 1:  # random hex color
        r_rand = random.randint(0, 255)
        g_rand = random.randint(0, 255)
        b_rand = random.randint(0, 255)
        bg_img = img_processing.create_colored_background_img(res_x, res_y, r_rand, g_rand, b_rand)
        logging.info("   => set background to random color \'(%s, %s, %s)\'" % (r_rand, g_rand, b_rand))
    elif background_filter.type == 2:  # background image path specified in background_filter.value
        bg_img_path = background_filter.value
        bg_img = img_processing.import_image(bg_img_path)
        bg_img = img_processing.resize_aspect_ratio_crop_image(bg_img, res_x, res_y)  # resize image to the desired resolution
        logging.info("   => set background to \'%s\'" % bg_img_path)
    elif background_filter.type == 3:  # random background out of backgrounds path
        bg_img_path_list = utils.list_directory(background_path, file_ext_list=['.jpg', '.png'])
        random_bg_idx = random.randint(0, len(bg_img_path_list) - 1)
        bg_img_filename = bg_img_path_list[random_bg_idx]
        bg_img_path = os.path.abspath(background_path + bg_img_filename)
        bg_img = img_processing.import_image(bg_img_path)
        bg_img = img_processing.resize_aspect_ratio_crop_image(bg_img, res_x, res_y)  # resize image to the desired resolution
        logging.info("   => get random background from background path: \'%s\'" % bg_img_path)
    elif background_filter.type == 4:  # random background image from another folder
        bg_img_path_list = utils.list_directory(background_filter.value, file_ext_list=['.jpg', '.png'])
        random_bg_idx = random.randint(0, len(bg_img_path_list) - 1)
        bg_img_filename = bg_img_path_list[random_bg_idx]
        bg_img_path = os.path.abspath(background_path + bg_img_filename)
        bg_img = img_processing.import_image(bg_img_path)
        bg_img = img_processing.resize_aspect_ratio_crop_image(bg_img, res_x, res_y)  # resize image to the desired resolution
        logging.info("   => get random background from another path: \'%s\'" % bg_img_path)
    else:
        logging.error("ERROR: Unknown type (%s) of background filter" % background_filter.type)
        sys.exit(-1)

    return bg_img


def apply_add_obj_filter(add_obj_filter, bg_img, dest_res_x, dest_res_y):
    """
            Apply 'Add object' filter with given add_obj_filter parameters, a given background image and the desired output
            resolution. The add_obj object can contain almost all other filters, which get applied on each added object.

            :param add_obj_filter: object containing add_obj parameters
            :param bg_img: background image
            :param dest_res_x: x resolution of output image
            :param dest_res_y: y resolution of output image
            :return: bg_img: background image containing all inserted objects
    """
    # get number of wanted additional objects
    if add_obj_filter.obj_min == add_obj_filter.obj_max:
        num_obj = int(add_obj_filter.obj_min)
    else:
        num_obj = random.randint(add_obj_filter.obj_min, add_obj_filter.obj_max)
    logging.info("   => add %d additional objects" % num_obj)

    # create list of objects containing 'add_obj' object images
    obj_list = []

    # add objects to the background
    for obj_counter in range(num_obj):
        # get object image
        if add_obj_filter.path_type == 0:
            add_obj_path_list = utils.list_directory(add_obj_filter.add_obj_path, file_ext_list=['.jpg', '.png'])
            random_add_obj_idx = random.randint(0, len(add_obj_path_list) - 1)
            add_obj_img_path = os.path.abspath(add_obj_filter.add_obj_path + add_obj_path_list[random_add_obj_idx])
        else:
            add_obj_img_path = add_obj_filter.add_obj_path
        add_obj_img = img_processing.import_image(add_obj_img_path)
        obj = Object(filename='', image=add_obj_img, bbox=None, category=None)
        obj_list.append(obj)

        # add overlap and add_obj filter so method 'apply_filters_on_objects()' can handle this
        add_obj_filter.overlap = None
        add_obj_filter.add_obj = None

    # add chosen object images to background image
    merged_img, obj_list = apply_filters_on_objects(obj_list, add_obj_filter, bg_img, dest_res_x, dest_res_y)

    return merged_img


def apply_overlay_filter(overlay_filter, img):
    """
        Apply overlay filter with given overlay_filter parameters.

        :param overlay_filter: object containing overlay parameters
        :param img: passed image on which overlay filter should be applied
        :return: img: processed image overlayed with another image
    """
    # get overlay image
    if overlay_filter.path_type == 0:  # random image out of overlay_img_path
        overlay_img_path_list = utils.list_directory(overlay_filter.overlay_img_path, file_ext_list=['.jpg', '.png'])
        random_overlay_idx = random.randint(0, len(overlay_img_path_list) - 1)
        overlay_img_filename = overlay_img_path_list[random_overlay_idx]
        overlay_img_path = os.path.abspath(overlay_filter.overlay_img_path + overlay_img_filename)
    elif overlay_filter.path_type == 1:  # overlay image path specified in overlay_filter.overlay_img_path
        overlay_img_path = overlay_filter.overlay_img_path
    else:
        logging.error("ERROR: Unknown path type (%s) of overlay filter" % overlay_filter.path_type)
        sys.exit(-1)

    # prepare intensity
    intensity = utils.to_float(overlay_filter.intensity/100)

    # read in overlapping image
    overlay_img = img_processing.import_image(overlay_img_path)
    overlay_img = img_processing.resize_aspect_ratio_crop_image(overlay_img, img.shape[1], img.shape[0])

    # filter
    if overlay_filter.filter == 0:  # multiply
        img = img_processing.multiply_images(img, overlay_img, intensity)
    elif overlay_filter.filter == 1:  # color
        img = img_processing.color_filter(img, overlay_img, intensity)
    elif overlay_filter.filter == 2:  # brighten
        img = img_processing.brighten_filter(img, overlay_img, intensity)
    elif overlay_filter.filter == 3:  # darken
        img = img_processing.darken_filter(img, overlay_img, intensity)
    else:
        logging.error("ERROR: Unknown filter (%s) of overlay filter" % overlay_filter.filter)
        sys.exit(-1)

    target_list = ['fore- and background', 'foreground', 'background']
    filter_list = ['multiply', 'color', 'brighten', 'darken']
    logging.info("   => apply overlay filter \'%s\' on %s with overlay image \'%s\'" %
                 (filter_list[overlay_filter.filter], target_list[overlay_filter.target],
                  overlay_img_path))

    return img


def apply_overlap_filter(overlap_filter_original, obj, bg_img):
    """
        Apply overlap filter with given overlap_filter parameters.

        :param overlap_filter: object containing overlap parameters
        :param obj_img: image containing cropped object
        :param bg_img: passed image on which overlap filter should be applied
        :param x_offset_factor: x translation value
        :param y_offset_factor: y translation value
        :return: img: processed image with overlapping object
    """

    # only operate on copy of overlap_filter, so the original gets untouched
    overlap_filter = copy.deepcopy(overlap_filter_original)

    # extract important information out of object
    obj_img = obj.image
    x_offset_factor = obj.x_offset
    y_offset_factor = obj.y_offset

    # get overlap image
    if overlap_filter.path_type == 0:  # random image out of overlap_img_path
        overlap_img_path_list = utils.list_directory(overlap_filter.overlap_img_path, file_ext_list=['.jpg', '.png'])
        random_overlap_idx = random.randint(0, len(overlap_img_path_list) - 1)
        overlap_img_filename = overlap_img_path_list[random_overlap_idx]
        overlap_img_path = os.path.abspath(overlap_filter.overlap_img_path + overlap_img_filename)
    elif overlap_filter.path_type == 1:  # overlap image path specified in overlap_filter.overlap_img_path
        overlap_img_path = overlap_filter.overlap_img_path
    else:
        logging.error("ERROR: Unknown path type (%s) of overlap filter" % overlap_filter.path_type)
        sys.exit(-1)

    # get overlapping percentage values
    # x:
    if overlap_filter.x_pct_min == overlap_filter.x_pct_max:
        x_pct = overlap_filter.x_pct_min
    else:
        x_pct = random.randint(overlap_filter.x_pct_min, overlap_filter.x_pct_max)
    # y:
    if overlap_filter.y_pct_min == overlap_filter.y_pct_max:
        y_pct = overlap_filter.y_pct_min
    else:
        y_pct = random.randint(overlap_filter.y_pct_min, overlap_filter.y_pct_max)
    y_pct *= -1  # invert y_pct, so positive values overlap upper part and negative values overlap lower part

    # read in overlapping image
    overlap_img = img_processing.import_image(overlap_img_path)

    # calculate scale in relation to object image
    if obj_img.shape[0] > obj_img.shape[1]:  # get the larger shape length for scale calculation, so overlap object is big enough
        if overlap_img.shape[0] > overlap_img.shape[1]:
            scale_factor = obj_img.shape[0] / overlap_img.shape[0]
        else:
            scale_factor = obj_img.shape[0] / overlap_img.shape[1]
    else:
        if overlap_img.shape[0] > overlap_img.shape[1]:
            scale_factor = obj_img.shape[1] / overlap_img.shape[0]
        else:
            scale_factor = obj_img.shape[1] / overlap_img.shape[1]

    # calculate translation to attain overlapping percentage
    x_len_pct = obj_img.shape[1] * (float(x_pct) / 100)
    y_len_pct = obj_img.shape[0] * (float(y_pct) / 100)
    x_rel_len = x_len_pct / (bg_img.shape[1] - overlap_img.shape[1])
    y_rel_len = y_len_pct / (bg_img.shape[0] - overlap_img.shape[1])
    x_offset_factor = min(max(0, x_offset_factor + x_rel_len), 1)  # minimal value must be 0 and maximum must be 1
    y_offset_factor = min(max(0, y_offset_factor + y_rel_len), 1)  # minimal value must be 0 and maximum must be 1

    # update overlap filter with calculated standard values
    # add translate filter
    x_range_str = '%s:%s' % (x_offset_factor*100, x_offset_factor*100)
    y_range_str = '%s:%s' % (y_offset_factor*100, y_offset_factor*100)
    overlap_filter.translate = TranslateFilter(x_range_str=x_range_str, y_range_str=y_range_str)
    # update scale filter with default size
    if overlap_filter.scale:
        overlap_filter.scale.min = overlap_filter.scale.min * scale_factor
        overlap_filter.scale.max = overlap_filter.scale.max * scale_factor
    else:
        scale_range_str = '%s:%s' % (scale_factor*100, scale_factor*100)
        overlap_filter.scale = ScaleFilter(scale_range_str=scale_range_str)
    # add clip and overlap filter so method 'apply_filters_on_objects()' can handle this
    overlap_filter.clip = None
    overlap_filter.overlap = None

    # create object
    obj = Object(filename='', image=overlap_img, bbox=None, category=None)

    # add object image to background image
    merged_img, obj_list = apply_filters_on_objects([obj], overlap_filter, bg_img, 3*bg_img.shape[1], 3*bg_img.shape[0])

    target_str = 'behind' if overlap_filter.type == 0 else 'in front of'
    logging.info("   => apply overlap filter %s object with x=%s%%, y=%s%% overlapping and object \'%s\'" % (target_str, x_pct, y_pct*(-1), overlap_img_path))
    return merged_img


def apply_resolution_filter(resolution_filter):
    """
        Apply resolution filter with given resolution_filter object parameters.

        :param resolution_filter: object containing resolution parameters
        :return: res_x, res_y: x and y resolution in pixel
    """
    if resolution_filter.x_min == resolution_filter.x_max:
        res_x = utils.to_int(resolution_filter.x_min)
    else:
        res_x = random.randint(resolution_filter.x_min, resolution_filter.x_max)
    if resolution_filter.y_min == resolution_filter.y_max:
        res_y = utils.to_int(resolution_filter.y_min)
    else:
        res_y = random.randint(resolution_filter.y_min, resolution_filter.y_max)
    logging.info("   => resolution x: %s and y: %s" % (res_x, res_y))
    return res_x, res_y


def apply_filters_on_background(background_path, filter_chain, dest_res_x, dest_res_y):
    # background filter
    bg_img = apply_background_filter(filter_chain.background, dest_res_x, dest_res_y, background_path)

    # add object filter
    if filter_chain.add_obj:
        bg_img = apply_add_obj_filter(filter_chain.add_obj, bg_img, dest_res_x, dest_res_y)

    # overlay filter
    if filter_chain.overlay:
        if filter_chain.overlay.target == TARGET_FORE_AND_BACKGROUND:
            pass
        elif filter_chain.overlay.target == TARGET_FOREGROUND:
            pass
        elif filter_chain.overlay.target == TARGET_BACKGROUND:
            bg_img = apply_overlay_filter(filter_chain.overlay, bg_img)
        else:
            logging.error("ERROR: Unknown overlay filter target (%s)" % filter_chain.overlay.target)
            sys.exit(-1)

    # hue filter
    if filter_chain.hue:
        if filter_chain.hue.target == TARGET_FORE_AND_BACKGROUND:  # fore- and background
            pass
        elif filter_chain.hue.target == TARGET_FOREGROUND:
            pass
        elif filter_chain.hue.target == TARGET_BACKGROUND:
            bg_img = apply_hue_filter(filter_chain.hue, bg_img)
        else:
            logging.error("ERROR: Unknown hue filter target (%s)" % filter_chain.hue.target)
            sys.exit(-1)

    # saturation filter
    if filter_chain.saturation:
        if filter_chain.saturation.target == TARGET_FORE_AND_BACKGROUND:  # fore- and background
            pass
        elif filter_chain.saturation.target == TARGET_FOREGROUND:
            pass
        elif filter_chain.saturation.target == TARGET_BACKGROUND:
            bg_img = apply_saturation_filter(filter_chain.saturation, bg_img)
        else:
            logging.error("ERROR: Unknown saturation filter target (%s)" % filter_chain.saturation.target)
            sys.exit(-1)

    # value filter
    if filter_chain.value:
        if filter_chain.value.target == TARGET_FORE_AND_BACKGROUND:  # fore- and background
            pass
        elif filter_chain.value.target == TARGET_FOREGROUND:
            pass
        elif filter_chain.value.target == TARGET_BACKGROUND:
            bg_img = apply_value_filter(filter_chain.value, bg_img)
        else:
            logging.error("ERROR: Unknown value filter target (%s)" % filter_chain.value.target)
            sys.exit(-1)

    # brightness filter
    if filter_chain.brightness:
        if filter_chain.brightness.target == TARGET_FORE_AND_BACKGROUND:  # fore- and background
            pass
        elif filter_chain.brightness.target == TARGET_FOREGROUND:
            pass
        elif filter_chain.brightness.target == TARGET_BACKGROUND:
            bg_img = apply_brightness_filter(filter_chain.brightness, bg_img)
        else:
            logging.error("ERROR: Unknown brightness filter target (%s)" % filter_chain.brightness.target)
            sys.exit(-1)

    # contrast filter
    if filter_chain.contrast:
        if filter_chain.contrast.target == TARGET_FORE_AND_BACKGROUND:  # fore- and background
            pass
        elif filter_chain.contrast.target == TARGET_FOREGROUND:
            pass
        elif filter_chain.contrast.target == TARGET_BACKGROUND:
            bg_img = apply_contrast_filter(filter_chain.contrast, bg_img)
        else:
            logging.error("ERROR: Unknown contrast filter target (%s)" % filter_chain.contrast.target)
            sys.exit(-1)

    # gamma filter
    if filter_chain.gamma:
        if filter_chain.gamma.target == TARGET_FORE_AND_BACKGROUND:  # fore- and background
            pass
        elif filter_chain.gamma.target == TARGET_FOREGROUND:
            pass
        elif filter_chain.gamma.target == TARGET_BACKGROUND:
            bg_img = apply_gamma_filter(filter_chain.gamma, bg_img)
        else:
            logging.error("ERROR: Unknown gamma filter target (%s)" % filter_chain.gamma.target)
            sys.exit(-1)

    # blur filter
    if filter_chain.blur:
        if filter_chain.blur.target == TARGET_FORE_AND_BACKGROUND:  # fore- and background
            pass
        elif filter_chain.blur.target == TARGET_FOREGROUND:
            pass
        elif filter_chain.blur.target == TARGET_BACKGROUND:
            bg_img = apply_blur_filter(filter_chain.blur, bg_img)
        else:
            logging.error("ERROR: Unknown blur filter target (%s)" % filter_chain.blur.target)
            sys.exit(-1)

    # noise filter
    if filter_chain.noise:
        if filter_chain.noise.target == TARGET_FORE_AND_BACKGROUND:  # fore- and background
            pass
        elif filter_chain.noise.target == TARGET_FOREGROUND:
            pass
        elif filter_chain.noise.target == TARGET_BACKGROUND:
            bg_img = apply_noise_filter(filter_chain.noise, bg_img)
        else:
            logging.error("ERROR: Unknown noise filter target (%s)" % filter_chain.noise.target)
            sys.exit(-1)

    return bg_img


def apply_filters_on_objects(obj_list, filter_chain, bg_img, dest_res_x, dest_res_y):
    merged_img = None

    # object image operations
    for obj_idx in range(len(obj_list)):
        # clip object by removing green pixels if object image has no alpha channel
        if obj_list[obj_idx].image.shape[2] > 3:
            logging.info("   => Object image is already clipped")
        else:
            obj_list[obj_idx].image = img_processing.rmv_green(obj_list[obj_idx].image)

        # rotate filter
        if filter_chain.rotate:
            obj_list[obj_idx].image = apply_rotate_filter(obj_list[obj_idx].image, filter_chain.rotate)

        # crop transparent pixels out of object image
        obj_list[obj_idx].image = img_processing.crop_transparent_pixels(obj_list[obj_idx].image)

        # scale object image after rotate filter, so it takes 30% of the backgrounds images space
        obj_list[obj_idx].image = img_processing.adjust_obj_to_bg_size(obj_list[obj_idx].image, dest_res_x, dest_res_y)

        # translate filter
        curr_x_offset, curr_y_offset = apply_translate_filter(filter_chain.translate)
        obj_list[obj_idx].x_offset = curr_x_offset
        obj_list[obj_idx].y_offset = curr_y_offset

        # scale_filter
        if filter_chain.scale:
            obj_list[obj_idx].image = apply_scale_filter(obj_list[obj_idx].image, filter_chain.scale)

        # clip filter
        if filter_chain.clip:
            obj_list[obj_idx].image = apply_clip_filter(obj_list[obj_idx].image, filter_chain.clip)

        # overlay filter
        if filter_chain.overlay:
            if filter_chain.overlay.target == TARGET_FOREGROUND:
                obj_list[obj_idx].image = apply_overlay_filter(filter_chain.overlay, obj_list[obj_idx].image)

        # hue filter
        if filter_chain.hue:
            if filter_chain.hue.target == TARGET_FOREGROUND:
                obj_list[obj_idx].image = apply_hue_filter(filter_chain.hue, obj_list[obj_idx].image)

        # saturation filter
        if filter_chain.saturation:
            if filter_chain.saturation.target == TARGET_FOREGROUND:
                obj_list[obj_idx].image = apply_saturation_filter(filter_chain.saturation, obj_list[obj_idx].image)

        # value filter
        if filter_chain.value:
            if filter_chain.value.target == TARGET_FOREGROUND:
                obj_list[obj_idx].image = apply_value_filter(filter_chain.value, obj_list[obj_idx].image)

        # brightness filter
        if filter_chain.brightness:
            if filter_chain.brightness.target == TARGET_FOREGROUND:
                obj_list[obj_idx].image = apply_brightness_filter(filter_chain.brightness, obj_list[obj_idx].image)

        # contrast filter
        if filter_chain.contrast:
            if filter_chain.contrast.target == TARGET_FOREGROUND:
                obj_list[obj_idx].image = apply_contrast_filter(filter_chain.contrast, obj_list[obj_idx].image)

        # gamma filter
        if filter_chain.gamma:
            if filter_chain.gamma.target == TARGET_FOREGROUND:
                obj_list[obj_idx].image = apply_gamma_filter(filter_chain.gamma, obj_list[obj_idx].image)

        # blur filter
        if filter_chain.blur:
            if filter_chain.blur.target == TARGET_FOREGROUND:
                obj_list[obj_idx].image = apply_blur_filter(filter_chain.blur, obj_list[obj_idx].image)

        # noise filter
        if filter_chain.noise:
            if filter_chain.noise.target == TARGET_FOREGROUND:
                obj_list[obj_idx].image = apply_noise_filter(filter_chain.noise, obj_list[obj_idx].image)

        # overlap filter
        if filter_chain.overlap:
            if filter_chain.overlap.type == 0:  # type 0: behind objects
                bg_img = apply_overlap_filter(filter_chain.overlap, obj_list[obj_idx], bg_img)
            elif filter_chain.overlap.type != 1:
                logging.error("ERROR: Unknown overlap filter type (%s)" % filter_chain.overlap.type)
                sys.exit(-1)

        # merge object and background
        if merged_img is None:  # if it's the first time merging
            background_img = bg_img
        else:
            background_img = merged_img
        merged_img, obj_bbox = img_processing.paste_foreground_into_background(obj_list[obj_idx].image, background_img,
                                                                               curr_x_offset, curr_y_offset)
        obj_list[obj_idx].bbox = obj_bbox

    return merged_img, obj_list


def apply_filters_on_objects_and_background(merged_img, filter_chain):
    # noise filter
    # noise applied to fore- and background
    if filter_chain.noise and filter_chain.noise.target == TARGET_FORE_AND_BACKGROUND:
        merged_img = apply_noise_filter(filter_chain.noise, merged_img)

    # blur filter
    # blur applied to fore- and background
    if filter_chain.blur and filter_chain.blur.target == TARGET_FORE_AND_BACKGROUND:
        merged_img = apply_blur_filter(filter_chain.blur, merged_img)

    # overlay filter
    if filter_chain.overlay:
        if filter_chain.overlay.target == TARGET_FORE_AND_BACKGROUND:
            merged_img = apply_overlay_filter(filter_chain.overlay, merged_img)

    # hue filter
    if filter_chain.hue:
        if filter_chain.hue.target == TARGET_FORE_AND_BACKGROUND:
            merged_img = apply_hue_filter(filter_chain.hue, merged_img)

    # saturation filter
    if filter_chain.saturation:
        if filter_chain.saturation.target == TARGET_FORE_AND_BACKGROUND:
            merged_img = apply_saturation_filter(filter_chain.saturation, merged_img)

    # value filter
    if filter_chain.value:
        if filter_chain.value.target == TARGET_FORE_AND_BACKGROUND:
            merged_img = apply_value_filter(filter_chain.value, merged_img)

    # brightness filter
    if filter_chain.brightness:
        if filter_chain.brightness.target == TARGET_FORE_AND_BACKGROUND:
            merged_img = apply_brightness_filter(filter_chain.brightness, merged_img)

    # contrast filter
    if filter_chain.contrast:
        if filter_chain.contrast.target == TARGET_FORE_AND_BACKGROUND:
            merged_img = apply_contrast_filter(filter_chain.contrast, merged_img)

    # gamma filter
    if filter_chain.gamma:
        if filter_chain.gamma.target == TARGET_FORE_AND_BACKGROUND:
            merged_img = apply_gamma_filter(filter_chain.gamma, merged_img)

    return merged_img


def generate(general_struct, filter_chain_list, class_id_to_name_dict, img_name_to_class_id_dict, yolo_version, draw_bounding_boxes=False, no_yolo_output=False):
    """
        Generate images with given settings in general struct and filter chain list. With the assignment of each object
        image to an object class this method also creates output files for YOLO.

        :param general_struct: object containing general settings
        :param filter_chain_list: list of filter_chain objects containing all settings for every set filter
        :param class_id_to_name_dict: dict assigning object class IDs to its corresponding class name
        :param img_name_to_class_id_dict: dict assigning each image to its object class ID
        :param yolo_version: number of YOLO version for which the YOLO output files should be created
        :param draw_bounding_boxes: boolean value specifying whether bounding boxes should be drawn around object
        :param no_yolo_output: boolean value specifying whether no YOLO output files should be created
        :return:
    """
    # get destination resolution
    dest_res_x = general_struct.output_width
    dest_res_y = general_struct.output_height

    # collect input images
    objects_path = general_struct.object_path
    obj_img_filename_list = utils.list_directory(objects_path, [".jpg", ".png"])

    # setup yolo object
    yolo_generator = YoloGenerator(yolo_version=yolo_version)

    # set counter for filename of stored images
    img_counter = 1

    # list of all objects
    obj_list = []

    # do operations for every object
    for filter_chain in filter_chain_list:
        for obj_img_filename in obj_img_filename_list:
            # TODO: following outprint should be in the next upper loop and here should be "Generate image x" or smth
            logging.info("Process filter chain number %d:" % img_counter)
            output_filename = str(img_counter)
            obj_list.clear()  # list of all objects

            # get number of objects which should be placed onto each generated image
            num_obj_per_img_min, num_obj_per_img_max = utils.extract_range(filter_chain.num_obj_per_img)
            if num_obj_per_img_min == num_obj_per_img_max:
                num_obj_per_img = int(num_obj_per_img_min)
            else:
                num_obj_per_img = random.randint(num_obj_per_img_min, num_obj_per_img_max)
            logging.info("   => %s objects get placed onto each generated image" % num_obj_per_img)

            #############################################################
            # BACKGROUND FILTERS
            # get resolution out of resolution filter
            if filter_chain.resolution:
                res_x, res_y = apply_resolution_filter(filter_chain.resolution)
            else:
                res_x = dest_res_x
                res_y = dest_res_y

            # apply all filters aiming on background
            bg_img = apply_filters_on_background(general_struct.background_path, filter_chain, res_x, res_y)

            #############################################################
            # OBJECTS FILTERS

            # read in main object image
            obj_img_path = os.path.abspath(os.path.join(general_struct.object_path, obj_img_filename))
            category_number = img_name_to_class_id_dict[obj_img_filename]
            main_obj = Object(filename=obj_img_filename, image=img_processing.import_image(obj_img_path), category=category_number)
            obj_list.append(main_obj)
            logging.info("   => object image path \'%s\'" % obj_img_filename)

            # read in other randomly selected object images including their labels
            for x in range(num_obj_per_img-1):
                random_idx = random.randint(0, len(obj_img_filename_list)-1)
                obj_img_path = os.path.abspath(os.path.join(general_struct.object_path, obj_img_filename_list[random_idx]))
                category_number = img_name_to_class_id_dict[obj_img_filename_list[random_idx]]
                obj = Object(filename=obj_img_filename_list[random_idx], image=img_processing.import_image(obj_img_path), category=category_number)
                logging.info("   => random object image path \'%s\'" % obj_img_path)
                obj_list.append(obj)

            # apply all object filters on the generated objects
            merged_img, obj_list = apply_filters_on_objects(obj_list, filter_chain, bg_img, res_x, res_y)

            #############################################################
            # OBJECT AND BACKGROUND FILTERS

            # before applying all other filters, insert additional objects in front of objects
            for obj in obj_list:
                # overlap filter applied onto fore- and background
                if filter_chain.overlap:
                    if filter_chain.overlap.type == 1:  # type 1: in front of objects
                        merged_img = apply_overlap_filter(filter_chain.overlap, obj, merged_img)
                    elif filter_chain.overlap.type != 0:
                        logging.error("ERROR: Unknown overlap filter type (%s)" % filter_chain.overlap.type)
                        sys.exit(-1)

            # apply all filters aiming for object and background
            merged_img = apply_filters_on_objects_and_background(merged_img, filter_chain)

            #############################################################

            # draw bounding box
            if draw_bounding_boxes:
                for obj in obj_list:
                    img_processing.draw_bounding_box(merged_img, obj.bbox)

            # save result image
            img_processing.save_image(merged_img, general_struct.output_path, output_filename + '.jpg')

            # create YOLO information for every generated image with category numbers and bounding boxes of objects
            yolo_generator.generate_yolo_output(obj_list, merged_img.shape[:2], general_struct.output_path, output_filename)

            logging.info("... done processing filter chain number %s\n" % img_counter)
            img_counter += 1

    # create text files 'train.txt', 'obj.names' and 'obj.data' for YOLO inside the output directory
    if not no_yolo_output:
        yolo_generator.generate_train_data_file(general_struct.output_path)
        yolo_generator.generate_obj_names_file(general_struct.output_path, class_id_to_name_dict)
        yolo_generator.generate_obj_data_file(general_struct.output_path, len(class_id_to_name_dict.keys()))
        yolo_generator.generate_yolo_obj_cfg(general_struct.output_path, len(class_id_to_name_dict.keys()))

    return
