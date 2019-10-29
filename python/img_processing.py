import os
import sys
import cv2
import hsv
import utils
import logging
import numpy as np
from objects import BoundingBox

# HSV ranges for clipping green
HSV_VALUES = hsv.get_hsv_std_values()


def import_image(img_path):
    """
        Import an image stored at the given path.

        :param img_path: path to image
        :return: img: imported image as numpy nd array
    """
    img = cv2.imread(img_path, cv2.IMREAD_UNCHANGED)
    if img is None:
        # if 'img_path' contains unicode chars, then the following works
        stream = open(img_path, "rb")
        bytes_array = bytearray(stream.read())
        numpy_array = np.asarray(bytes_array, dtype=np.uint8)
        img = cv2.imdecode(numpy_array, cv2.IMREAD_UNCHANGED)

        # if no image is found at the given path
        if img is None:
            logging.error("Error: Could\'t find image file \'%s\'" % img_path)
            sys.exit()
    return img


def rmv_green(img, hsv_ranges=None):
    """
        Remove green color out of given image by removing parts of HSV image space. If no HSV ranges are passed, HSV
        values stored in /pickle_files/hsv.pickle are used.

        :param img: image containing object in front of green screen
        :param hsv_ranges: HSV object containing HSV ranges, which need to be removed out of object image
        :return: clipped_img: object image with transparency instead of green color
    """
    logging.info("   => Clipping object image by removing green color")
    hsv_values = hsv_ranges if hsv_ranges is not None else HSV_VALUES
    hsv_img = cv2.cvtColor(img.copy(), cv2.COLOR_BGR2HSV)
    h, s, v = cv2.split(hsv_img)
    alpha_channel = np.where(np.logical_and(np.logical_and(np.logical_and(np.logical_and(np.logical_and(np.greater_equal(h, hsv_values.h_min),
                                            np.less_equal(h, hsv_values.h_max)),
                                            np.greater_equal(s, hsv_values.s_min)),
                                            np.less_equal(s, hsv_values.s_max)),
                                            np.greater_equal(v, hsv_values.v_min)),
                                            np.less_equal(v, hsv_values.v_max)),
                             0, 255).astype(np.uint8)
    b_channel, g_channel, r_channel = cv2.split(img[:, :, :3])
    clipped_img = cv2.merge((b_channel, g_channel, r_channel, alpha_channel))
    return clipped_img


def draw_bounding_box(img, obj_bbox, color=(255, 0, 0)):
    """
        Draw bounding box onto the given image in the given color.

        :param img: image in which bounding box should be drawn
        :param obj_bbox: object containing parameters of bounding box
        :param color: BGR color of bounding box
        :return:
    """
    cv2.rectangle(img, (obj_bbox.x_min, obj_bbox.y_min), (obj_bbox.x_max, obj_bbox.y_max), color, 2)


def create_colored_background_img(res_x, res_y, r_int, g_int, b_int):
    """
        Create an image with the given resolution and color it with the given RGB values.

        :param res_x: desired x resolution of output image
        :param res_y: desired y resolution of output image
        :param r_int: 8 bit value specifying red color
        :param g_int: 8 bit value specifying green color
        :param b_int: 8 bit value specifying blue color
        :return: img: image with the desired color and resolution
    """
    img = np.ones((res_y, res_x, 3), np.uint8) * 255  # create blank image
    img[:] = (b_int, g_int, r_int)  # color has format GBR
    return img


def create_checkerboard_pattern(shape):
    """
        Creates an image with a checkerboard pattern and the given shape.

        :param shape: desired shape of checkerboard image
        :return: pattern_3_channel: checkerboard image with desired shape
    """
    h, w = shape[:2]
    square_len = 8
    square_num_x = int(w / (2 * square_len)) + 1
    square_num_y = int(h / (2 * square_len)) + 1
    pattern_float = np.kron([[1, 0] * square_num_x, [0, 1] * square_num_x] * square_num_y,
                            np.ones((square_len, square_len)))
    pattern = np.multiply(pattern_float.astype(dtype=np.uint8), 255)
    pattern[pattern == 0] = 200
    pattern_cropped = pattern[:h, :w]
    pattern_3_channel = cv2.merge((pattern_cropped, pattern_cropped, pattern_cropped, np.ones(shape=pattern_cropped.shape, dtype=np.uint8)))
    return pattern_3_channel


def scale_img(img, factor):
    """
        Resize image by the given factor.

        :param img: image which needs to be resized
        :param factor: resize factor (greater than zero)
        :return: scaled image
    """
    return cv2.resize(img, None, fx=float(factor), fy=float(factor))


def rotate_img(img, angle):
    """
        Rotate image by the given angle.

        :param img: image which needs to be rotated
        :param angle: rotation angle
        :return: rotated image
    """
    # grab the dimensions of the image and then determine the
    # center
    (height, width) = img.shape[:2]
    (x_center, y_center) = (width // 2, height // 2)

    # grab the rotation matrix (applying the negative of the
    # angle to rotate clockwise), then grab the sine and cosine
    # (i.e., the rotation components of the matrix)
    rot_mat = cv2.getRotationMatrix2D((x_center, y_center), -angle, 1.0)
    cos = np.abs(rot_mat[0, 0])
    sin = np.abs(rot_mat[0, 1])

    # compute the new bounding dimensions of the image
    new_width = int((height * sin) + (width * cos))
    new_height = int((height * cos) + (width * sin))

    # adjust the rotation matrix to take the translation into account
    rot_mat[0, 2] += (new_width / 2) - x_center
    rot_mat[1, 2] += (new_height / 2) - y_center

    # perform the actual rotation and return the image
    return cv2.warpAffine(img, rot_mat, (new_width, new_height))


def clip_img(img, x_factor, y_factor):
    """
        Clip image by the given x and y factors.

        :param img: image which needs to be clipped
        :param x_factor: x value defining the degree of horizontal clipping
        :param y_factor: y value defining the degree of vertical clipping
        :return: img: clipped image
    """
    img_height, img_width = img.shape[:2]

    # set image slice indices
    x_thresh_left, y_thresh_top = 0, 0
    x_thresh_right, y_thresh_bottom = img_width, img_height

    # process x clipping
    if x_factor < 0.0:  # image gets clipped on the left side
        logging.info("   => Clip image on left half by %s%%" % (abs(x_factor)*100))
        x_thresh_left = utils.to_int(img_width * abs(x_factor / 2))  # divide by 2 because x_factor only gets applied on the right or left half of the image

    elif x_factor > 0.0:  # image gets clipped on the right side
        logging.info("   => Clip image on right half by %s%%" % (x_factor*100))
        x_thresh_right -= utils.to_int(img_width * abs(x_factor / 2))

    # process y clipping
    if y_factor < 0.0:  # image gets clipped on the upper side
        logging.info("   => Clip image on upper half by %s%%" % (abs(y_factor)*100))
        y_thresh_top = utils.to_int(img_width * abs(y_factor / 2))  # divide by 2 because y_factor only gets applied on the upper or lower half of the image

    elif y_factor > 0.0:  # image gets clipped on the lower side
        logging.info("   => Clip image on lower half %s%%" % (y_factor*100))
        y_thresh_bottom -= utils.to_int(img_width * abs(y_factor / 2))

    return img[y_thresh_top:y_thresh_bottom, x_thresh_left:x_thresh_right, :]


def blur_img(img, kernel_size):
    """
        Blur image with the given kernel size.

        :param img: image which needs to be blurred
        :param kernel_size: size of kernel
        :return: blurred image
    """
    # kernel size needs to be odd
    if kernel_size % 2 == 0:
        kernel_size += 1

    # kernel size needs to be at least 1
    max(kernel_size, 1)

    kernel_size = utils.to_int(kernel_size)

    return cv2.blur(img, (kernel_size, kernel_size))


def salt_and_pepper_noise(img, amount=0.005):
    """
        Add salt and pepper noise to the given image in the given amount.

        :param img: image which needs to be overlayed by noise
        :param amount: intensity of noise
        :return: image overlayed by noise
    """
    amount = utils.to_float(amount)

    # Ratio of salt and pepper
    s_vs_p = 0.5

    # Salt
    num_salt = np.ceil(amount * img.size * s_vs_p)
    coords = [np.random.randint(0, i - 1, utils.to_int(num_salt)) for i in img.shape]
    img[coords[0], coords[1], :3] = 255  # only write on channels 0-2, not on alpha channel

    # Pepper
    num_pepper = np.ceil(amount * img.size * (1. - s_vs_p))
    coords = [np.random.randint(0, i - 1, utils.to_int(num_pepper)) for i in img.shape]
    img[coords[0], coords[1], :3] = 0   # only write on channels 0-2, not on alpha channel

    return img


def change_hsv(img, hue=0, saturation=0, value=0):
    """
        Change hue, saturation and value of the given image.

        :param img: image, which HSV values should be changed
        :param hue: new hue value
        :param saturation: new saturation value
        :param value: new value value
        :return: img_converted: result image with changed HSV values
    """
    hsv = cv2.cvtColor(img, cv2.COLOR_BGR2HSV)
    h, s, v = cv2.split(hsv)
    hue = utils.to_int(hue)
    saturation = utils.to_int(saturation)
    value = utils.to_int(value)

    # Hue
    if hue == 0:
        pass
    elif hue > 0:
        # increase h
        lim = 360 - hue
        h[h > lim] = 360
        h[h <= lim] += hue
    else:
        logging.error("The HUE value (%s) must be greater than 0" % hue)

    # Saturation
    saturation = utils.to_int((saturation - 50) * 5.1)
    if saturation == 0:
        pass
    if saturation > 0:
        # increase saturation
        lim = 255 - saturation
        s[s > lim] = 255
        s[s <= lim] += saturation
    else:
        # decrease saturation
        lim = abs(saturation)
        s[s > lim] -= abs(saturation)
        s[s <= lim] = 0

    # Value
    value = utils.to_int((value - 50) * 2)
    if value == 0:
        pass
    if value > 0:
        # increase value
        lim = 255 - value
        v[v > lim] = 255
        v[v <= lim] += value
    else:
        # decrease value
        lim = abs(value)
        v[v > lim] -= abs(value)
        v[v <= lim] = 0

    final_hsv = cv2.merge((h, s, v))
    img_converted = cv2.cvtColor(final_hsv, cv2.COLOR_HSV2BGR)

    # add alpha channel if existent
    if img.shape[2] > 3:
        img_converted = cv2.merge((img_converted, img[:, :, 3]))
    return img_converted


def change_hue(img, hue=0):
    img = np.array(img, dtype=np.uint8)  # convert image to uint8 numpy array to avoid cv2 depth errors
    hsv = cv2.cvtColor(img, cv2.COLOR_BGR2HSV)
    h, s, v = cv2.split(hsv)
    hue = utils.to_int(hue)

    if hue == 0:
        pass
    elif hue > 0:
        # increase h
        lim = 360 - hue
        h[h > lim] = 360
        h[h <= lim] += hue
    else:
        logging.error("The HUE value (%s) must be greater than 0" % hue)

    final_hsv = cv2.merge((h, s, v))
    img_converted = cv2.cvtColor(final_hsv, cv2.COLOR_HSV2BGR)

    # add alpha channel if existent
    if img.shape[2] > 3:
        img_converted = cv2.merge((img_converted, img[:, :, 3]))
    return img_converted


def change_saturation(img, saturation=0):
    img = np.array(img, dtype=np.uint8)  # convert image to uint8 numpy array to avoid cv2 depth errors
    hsv = cv2.cvtColor(img, cv2.COLOR_BGR2HSV)
    h, s, v = cv2.split(hsv)
    saturation = utils.to_int(saturation)

    saturation = utils.to_int((saturation - 50) * 5.1)
    if saturation == 0:
        pass
    if saturation > 0:
        # increase saturation
        lim = 255 - saturation
        s[s > lim] = 255
        s[s <= lim] += saturation
    else:
        # decrease saturation
        lim = abs(saturation)
        s[s > lim] -= abs(saturation)
        s[s <= lim] = 0

    final_hsv = cv2.merge((h, s, v))
    img_converted = cv2.cvtColor(final_hsv, cv2.COLOR_HSV2BGR)

    # add alpha channel if existent
    if img.shape[2] > 3:
        img_converted = cv2.merge((img_converted, img[:, :, 3]))
    return img_converted


def change_value(img, value=0):
    img = np.array(img, dtype=np.uint8)  # convert image to uint8 numpy array to avoid cv2 depth errors
    hsv = cv2.cvtColor(img, cv2.COLOR_BGR2HSV)
    h, s, v = cv2.split(hsv)
    value = utils.to_int(value)

    value = utils.to_int((value - 50) * 2)
    if value == 0:
        pass
    if value > 0:
        # increase value
        lim = 255 - value
        v[v > lim] = 255
        v[v <= lim] += value
    else:
        # decrease value
        lim = abs(value)
        v[v > lim] -= abs(value)
        v[v <= lim] = 0

    final_hsv = cv2.merge((h, s, v))
    img_converted = cv2.cvtColor(final_hsv, cv2.COLOR_HSV2BGR)

    # add alpha channel if existent
    if img.shape[2] > 3:
        img_converted = cv2.merge((img_converted, img[:, :, 3]))
    return img_converted

def brightness_conversion_lut(offset=0):
    """
        Create a lookup table for brightness manipulation.

        :param offset: value defining how brightness should be changed
        :return: bright_lut: lookup table containing manipulated brightness
    """
    bright_lut = np.array([(i + offset) for i in np.arange(0, 256)])
    # important first clip and then uint8 cast
    bright_lut = np.clip(bright_lut, 0, 255).astype("uint8")
    return bright_lut


def contrast_conversion_lut(a, c):
    """
        Create a lookup table for contrast manipulation.

        :param a: constant for linear contrast manipulation
        :param c: constant for shifting contrast
        :return: contrast_lut: lookup table containing manipulated contrast
    """
    contrast_lut = np.array([a * (i - c) + c for i in np.arange(0, 256)])
    # important first clip and then uint8 cast
    contrast_lut = np.clip(contrast_lut, 0, 255).astype("uint8")
    return contrast_lut


def gamma_conversion_lut(gamma=1.0):
    """
        Create a lookup table for gamma manipulation.

        :param gamma: value for gamma manipulation
        :return: gamma_lut: lookup table containing manipulated gamma
    """
    # avoid divide by 0
    if gamma < 0.1:
        gamma = 0.1
    inv_gamma = 1.0 / gamma
    gamma_lut = np.array([255.0 * ((i / 255.0) ** inv_gamma) for i in np.arange(0, 256)]).astype("uint8")
    return gamma_lut


def merge_bgr_channels(blue_img, green_img, red_img):
    """
        Merge BGR channels to an image.

        :param blue_img: blue channel
        :param green_img: green channel
        :param red_img: red channel
        :return: image consisting of merged BGR channels
    """
    color_img = np.zeros((blue_img.shape[0], blue_img.shape[1], 3), 'uint8')
    color_img[..., 0] = blue_img
    color_img[..., 1] = green_img
    color_img[..., 2] = red_img
    return color_img


def change_brightness(img, brightness=0):
    brightness = utils.to_int(brightness)

    # split BGR image to separate arrays
    if img.shape[2] > 3:
        b_img, g_img, r_img, alpha = cv2.split(img)
    else:
        b_img, g_img, r_img = cv2.split(img)

    # manipulate each channel
    if brightness != 0:
        b_img = cv2.LUT(b_img, brightness_conversion_lut(brightness))
        g_img = cv2.LUT(g_img, brightness_conversion_lut(brightness))
        r_img = cv2.LUT(r_img, brightness_conversion_lut(brightness))

    # merge channels to colored picture
    img_converted = merge_bgr_channels(b_img, g_img, r_img)

    # add alpha channel if existent
    if img.shape[2] > 3:
        img_converted = cv2.merge((img_converted, img[:, :, 3]))

    return img_converted


def change_contrast(img, contrast=0):
    contrast = utils.to_int(contrast)

    # split BGR image to separate arrays
    if img.shape[2] > 3:
        b_img, g_img, r_img, alpha = cv2.split(img)
    else:
        b_img, g_img, r_img = cv2.split(img)

    if contrast != 0:
        b_img = cv2.LUT(b_img, contrast_conversion_lut(utils.to_float(contrast+100)/100, 100))
        g_img = cv2.LUT(g_img, contrast_conversion_lut(utils.to_float(contrast+100)/100, 100))
        r_img = cv2.LUT(r_img, contrast_conversion_lut(utils.to_float(contrast+100)/100, 100))

    # merge channels to colored picture
    img_converted = merge_bgr_channels(b_img, g_img, r_img)

    # add alpha channel if existent
    if img.shape[2] > 3:
        img_converted = cv2.merge((img_converted, img[:, :, 3]))

    return img_converted


def change_gamma(img, gamma=0.0):
    gamma = utils.to_float(gamma)

    # split BGR image to separate arrays
    if img.shape[2] > 3:
        b_img, g_img, r_img, alpha = cv2.split(img)
    else:
        b_img, g_img, r_img = cv2.split(img)

    if gamma != 0.0:
        b_img = cv2.LUT(b_img, gamma_conversion_lut(gamma*2))
        g_img = cv2.LUT(g_img, gamma_conversion_lut(gamma*2))
        r_img = cv2.LUT(r_img, gamma_conversion_lut(gamma*2))

    # merge channels to colored picture
    img_converted = merge_bgr_channels(b_img, g_img, r_img)

    # add alpha channel if existent
    if img.shape[2] > 3:
        img_converted = cv2.merge((img_converted, img[:, :, 3]))

    return img_converted


def change_bcg(img, brightness=0, contrast=0, gamma=0.0):
    """
        Change brightness, contrast and gamma of the given image.

        :param img: image, which BCG values should be changed
        :param brightness: new value for brightness
        :param contrast: new value for contrast
        :param gamma: new value for gamma
        :return: img_converted: result image with changed BCG values
    """
    brightness = utils.to_int(brightness)
    contrast = utils.to_int(contrast)
    gamma = utils.to_float(gamma)

    # split BGR image to separate arrays
    if img.shape[2] > 3:
        b_img, g_img, r_img, alpha = cv2.split(img)
    else:
        b_img, g_img, r_img = cv2.split(img)

    # manipulate each channel
    if brightness != 0:
        b_img = cv2.LUT(b_img, brightness_conversion_lut(brightness))
        g_img = cv2.LUT(g_img, brightness_conversion_lut(brightness))
        r_img = cv2.LUT(r_img, brightness_conversion_lut(brightness))

    if contrast != 0:
        b_img = cv2.LUT(b_img, contrast_conversion_lut(utils.to_float(contrast+100)/100, 100))
        g_img = cv2.LUT(g_img, contrast_conversion_lut(utils.to_float(contrast+100)/100, 100))
        r_img = cv2.LUT(r_img, contrast_conversion_lut(utils.to_float(contrast+100)/100, 100))

    if gamma != 0.0:
        b_img = cv2.LUT(b_img, gamma_conversion_lut(gamma*2))
        g_img = cv2.LUT(g_img, gamma_conversion_lut(gamma*2))
        r_img = cv2.LUT(r_img, gamma_conversion_lut(gamma*2))

    # merge channels to colored picture
    img_converted = merge_bgr_channels(b_img, g_img, r_img)

    # add alpha channel if existent
    if img.shape[2] > 3:
        img_converted = cv2.merge((img_converted, img[:, :, 3]))

    return img_converted


def multiply_images(img, overlay_img, intensity):
    """
        Multiply the two given images and merge it with the first image with the given intensity.

        :param img: first image which needs combined with the other one
        :param overlay_img: second image which needs combined with the other one
        :param intensity: degree of overlay intensity
        :return: result_img: original image overlayed by multiplied image
    """
    multiplied_img = np.copy(img).astype(np.float64)
    if (img.shape[0] != overlay_img.shape[0]) or (img.shape[1] != overlay_img.shape[1]):
        logging.error("      => Error: The shapes of images differ: [%s, %s], [%s, %s]" % (img.shape[1], img.shape[0],
                                                                                           overlay_img.shape[1], overlay_img.shape[0]))
    else:
        multiplied_img[:, :, :3] = np.true_divide(multiplied_img[:, :, :3], 255)
        img_divided = np.true_divide(img.astype(np.float64), 255)
        overlay_img_divided = np.true_divide(overlay_img.astype(np.float64), 255)
        # if overlay image has an alpha channel
        if overlay_img.shape[2] > 3:
            transparency_mask = cv2.merge((overlay_img_divided[:, :, 3], overlay_img_divided[:, :, 3], overlay_img_divided[:, :, 3]))
            np.multiply(img_divided[:, :, :3], overlay_img_divided[:, :, :3], out=multiplied_img[:, :, :3], where=transparency_mask>0.9)
        else:
            np.multiply(img_divided[:, :, :3], overlay_img_divided[:, :, :3], out=multiplied_img[:, :, :3])
        np.multiply(multiplied_img[:, :, :3], 255, out=multiplied_img[:, :, :3]).astype(int)

    if intensity < 1.0:
        # mix original image with multiplied image with the given intensity
        result_img = alpha_blending(img, multiplied_img, intensity)
    else:
        result_img = multiplied_img

    return result_img


def darken_filter(img, overlay_img, intensity):
    """
        Take the minimum of each pixel of the given two images and merge it with the first image with the given
        intensity.

        :param img: first image which gets combined with the other one
        :param overlay_img: second image which gets combined with the other one
        :param intensity: degree of darken intensity
        :return: original image overlayed by darkened image
    """
    minimum_img = np.copy(img)
    if (img.shape[0] != overlay_img.shape[0]) or (img.shape[1] != overlay_img.shape[1]):
        logging.error("      => Error: The shapes of images differ: [%s, %s], [%s, %s]" % (img.shape[1], img.shape[0],
                                                                                           overlay_img.shape[1], overlay_img.shape[0]))
    else:
        if overlay_img.shape[2] > 3:
            transparency_mask = cv2.merge((overlay_img[:, :, 3], overlay_img[:, :, 3], overlay_img[:, :, 3]))
            np.minimum(img[:, :, :3], overlay_img[:, :, :3], out=minimum_img[:, :, :3], where=transparency_mask>0.9)
        else:
            np.minimum(img[:, :, :3], overlay_img[:, :, :3], out=minimum_img[:, :, :3])

    if intensity < 1.0:
        # mix original image with minimum image with the given intensity
        result_img = alpha_blending(img, minimum_img, intensity)
    else:
        result_img = minimum_img

    return result_img


def brighten_filter(img, overlay_img, intensity):
    """
        Take the maximum of each pixel of the given two images and merge it with the first image with the given
        intensity.

        :param img: first image which gets combined with the other one
        :param overlay_img: second image which gets combined with the other one
        :param intensity: degree of brighten intensity
        :return: original image overlayed by brightened image
    """
    maximum_img = np.copy(img)
    if (img.shape[0] != overlay_img.shape[0]) or (img.shape[1] != overlay_img.shape[1]):
        logging.error("      => Error: The shapes of images differ: [%s, %s], [%s, %s]" % (img.shape[1], img.shape[0],
                                                                                           overlay_img.shape[1],
                                                                                           overlay_img.shape[0]))
    else:
        if overlay_img.shape[2] > 3:
            transparency_mask = cv2.merge((overlay_img[:, :, 3], overlay_img[:, :, 3], overlay_img[:, :, 3]))
            np.maximum(img[:, :, :3], overlay_img[:, :, :3], out=maximum_img[:, :, :3], where=transparency_mask > 0.9)
        else:
            np.maximum(img[:, :, :3], overlay_img[:, :, :3], out=maximum_img[:, :, :3])

    if intensity < 1.0:
        # mix original image with minimum image with the given intensity
        result_img = alpha_blending(img, maximum_img, intensity)
    else:
        result_img = maximum_img

    return result_img


def color_filter(img, overlay_img, intensity):
    """
        Combine the two given images with the color filter and merge them with the first image with the given intensity.

        :param img: first image which gets combined with the other one
        :param overlay_img: second image which gets combined with the other one
        :param intensity: degree of color intensity
        :return: original image overlayed by colored image
    """
    color_img = np.copy(img)
    if (img.shape[0] != overlay_img.shape[0]) or (img.shape[1] != overlay_img.shape[1]):
        logging.error("      => Error: The shapes of images differ: [%s, %s], [%s, %s]" % (img.shape[1], img.shape[0],
                                                                                           overlay_img.shape[1],
                                                                                           overlay_img.shape[0]))
        result_img = color_img
    else:
        # if overlay image has an alpha channel
        if overlay_img.shape[2] > 3:
            # mix image with overlay image
            mixed_img = np.copy(img)
            alpha_s = overlay_img[:, :, 3] / 255.0
            alpha_l = 1.0 - alpha_s
            for c in range(0, 3):
                mixed_img[:, :, c] = (alpha_s * overlay_img[:, :, c] + alpha_l * img[:, :, c])
        else:
            mixed_img = cv2.addWeighted(img[:, :, :3], 0.5, overlay_img, 0.5, 0)

        # get hue and saturation out of mixed image
        hsv_mixed_img = cv2.cvtColor(mixed_img, cv2.COLOR_BGR2HSV)
        hue_mixed_img, sat_mixed_img, val_mixed_img = cv2.split(hsv_mixed_img)

        # get hue and saturation out of image
        hsv_img = cv2.cvtColor(img, cv2.COLOR_BGR2HSV)
        hue_img, sat_img, val_img = cv2.split(hsv_img)

        # get luminance and chrominance out of image
        yuv_img = cv2.cvtColor(img, cv2.COLOR_BGR2YUV)
        luminance_img, u_img, v_img = cv2.split(yuv_img)

        # merge necessary parts for the color filter
        hsv_result_img = cv2.merge((hue_mixed_img, sat_mixed_img, val_img))
        result_img_1 = cv2.cvtColor(hsv_result_img, cv2.COLOR_HSV2BGR)

        # extract luminance and chrominance out of result image
        yuv_result_img_1 = cv2.cvtColor(result_img_1, cv2.COLOR_BGR2YUV)
        luminance_result_img, u_result_img, v_result_img = cv2.split(yuv_result_img_1)

        # merge image's luminance with the chrominance of result image
        yuv_result_img = cv2.merge((luminance_img, u_result_img, v_result_img))
        color_img = cv2.cvtColor(yuv_result_img, cv2.COLOR_YUV2BGR)

        if img.shape[2] > 3:
            # add alpha channel of original image
            b_result_img, g_result_img, r_result_img = cv2.split(color_img)
            color_img = cv2.merge((b_result_img, g_result_img, r_result_img, img[:, :, 3]))

        if intensity < 1.0:
            # mix original image with minimum image with the given intensity
            result_img = alpha_blending(img, color_img, intensity)
        else:
            result_img = color_img

    return result_img


def get_nontransparent_edges(img):
    """
        Find image coordinates of minimum and maximum nontransparent pixels.

        :param img: image for which nontransparent pixels should be found
        :return:
            min_x, max_x: minium and maximum x coordinates of nontransparent pixels
            min_y, max_y: minium and maximum y coordinates of nontransparent pixels
    """
    alpha_nonzero = np.nonzero(img[:, :, 3])
    try:
        min_y = alpha_nonzero[0][0]
        max_y = alpha_nonzero[0][-1]
        min_x = min(alpha_nonzero[1])
        max_x = max(alpha_nonzero[1])
    except Exception:  # if there are no nontransparent edges anymore
        min_y = 0
        max_y = 0
        min_x = 0
        max_x = 0
    return min_x, min_y, max_x, max_y


def crop_transparent_pixels(img):
    """
        Crop all edges of nontransparent pixels of the given image.

        :param img: image which needs to be cropped
        :return: cropped image
    """
    min_x, min_y, max_x, max_y = get_nontransparent_edges(img)
    return img[min_y:max_y, min_x:max_x, :]


def adjust_obj_to_bg_size(foreground_img, bg_img_width, bg_img_height):
    """
        Resize the given foreground/object image to a specific relation to the size of the background image.

        :param foreground_img: image containing object
        :param bg_img_width: width of background image
        :param bg_img_height: height of background image
        :return: foreground_img: resized foreground/object image
    """
    logging.info("   => Adjusting object size relative to background size")
    background_scale = 0.3

    # collect info of foreground image
    fg_img_height, fg_img_width = foreground_img.shape[:2]

    # desired width of nontransparent object size
    fg_pix_width_desired = int(bg_img_width * background_scale)
    fg_pix_height_desired = int(bg_img_height * background_scale)

    if fg_img_height > fg_img_width:  # vertical foreground image
        foreground_img = resize_image_aspect_ratio(foreground_img, new_height=fg_pix_height_desired)
        min_x, min_y, max_x, max_y = get_nontransparent_edges(foreground_img)
        fg_pix_width_2 = max_x - min_x

        # check if new width of object exceeds background with
        if fg_pix_width_2 > fg_pix_width_desired:
            foreground_img = resize_image_aspect_ratio(foreground_img, new_width=fg_pix_width_desired)

    else:  # horizontal and quadratic foreground image
        foreground_img = resize_image_aspect_ratio(foreground_img, new_width=fg_pix_width_desired)
        min_x, min_y, max_x, max_y = get_nontransparent_edges(foreground_img)
        fg_pix_height_2 = max_y - min_y

        # check if new height of object exceeds background height
        if fg_pix_height_2 > fg_pix_height_desired:
            foreground_img = resize_image_aspect_ratio(foreground_img, new_height=fg_pix_height_desired)

    return foreground_img


def alpha_blending(img1, img2, intensity):
    """
        Merge two images by alpha blending with the given intensity.

        :param img1: first image which needs to be combined with the other one
        :param img2: second image which needs to be combined with the other one
        :param intensity: degree of alpha blending intensity
        :return: merged image
    """
    # calc alpha value (opacity)
    merged_img = np.copy(img1).astype(np.float64)
    alpha_s = np.ones(merged_img[:, :, 0].shape, dtype=merged_img.dtype) * intensity
    alpha_l = 1.0 - alpha_s

    # do actual merge of fore- in background image
    for c in range(0, 3):
        merged_img[:, :, c] = (alpha_s * img2[:, :, c] +
                               alpha_l * img1[:, :, c])

    return merged_img


def paste_foreground_into_background(foreground_img, background_img, x_factor=0.5, y_factor=0.5, bounding_box=True):
    """
        Paste a foreground/object image onto a background image, translated by the given x and y factors. If parameter
        'bounding_box' is true, then also the bounding box is returned.

        :param foreground_img: foreground/object image
        :param background_img: background image
        :param x_factor: value for x translation
        :param y_factor: value for y translation
        :param bounding_box: boolean, whether bounding box should be returned
        :return: merged_img, (bounding_box): merged image and optional the bounding box of the foreground object
    """
    # if foreground image is bigger than background image, then adjust foreground's resolution to background's
    if foreground_img.shape[0] > background_img.shape[0]:
        foreground_img = crop_image(foreground_img, new_height=background_img.shape[0])
    if foreground_img.shape[1] > background_img.shape[1]:
        foreground_img = crop_image(foreground_img, new_width=background_img.shape[1])

    merged_img = background_img.copy()
    fg_img_height, fg_img_width = foreground_img.shape[:2]
    bg_img_height, bg_img_width = background_img.shape[:2]

    x_offset = int((bg_img_width - fg_img_width) * x_factor)
    y_offset = int((bg_img_height - fg_img_height) * y_factor)

    # calc corners of foreground image
    y1, y2 = y_offset, y_offset + foreground_img.shape[0]
    x1, x2 = x_offset, x_offset + foreground_img.shape[1]

    # calc alpha value (opacity)
    alpha_s = foreground_img[:, :, 3] / 255.0
    alpha_l = 1.0 - alpha_s

    # do actual merge of fore- in background image
    for c in range(0, 3):
        merged_img[y1:y2, x1:x2, c] = (alpha_s * foreground_img[:, :, c] +
                                    alpha_l * background_img[y1:y2, x1:x2, c])

    logging.info("   => merge object and background")

    if bounding_box:
        return merged_img, BoundingBox(x_min=x1, x_max=x2, y_min=y1, y_max=y2)
    else:
        return merged_img


def crop_image(img, new_width=None, new_height=None):
    """
        Crop image either to given width or given height.

        :param img: image, which needs to be cropped
        :param new_width: desired new width of image
        :param new_height: desired new height of image
        :return: cropped image with either the given width or height
    """
    height, width = img.shape[:2]
    if new_width and not new_height:
        # calculate difference between image's resolution and the given resolution parameters
        x_diff = width - new_width
        # calculating pixel area to crop the image
        width_start = int((x_diff + 1) / 2)
        width_end = width - (x_diff-width_start)
        cropped_img = img[:, width_start:width_end, :]
        logging.info("   => Crop image from resolution [%s, %s] to [%s, %s]" % (width, height, cropped_img.shape[1], cropped_img.shape[0]))
    elif not new_width and new_height:
        # calculate difference between image's resolution and the given resolution parameters
        y_diff = height - new_height
        # calculating pixel area to crop the image
        height_start = int((y_diff + 1) / 2)
        height_end = height - (y_diff-height_start)
        cropped_img = img[height_start:height_end:, :]
        logging.info("   => Crop image from resolution [%s, %s] to [%s, %s]" % (width, height, cropped_img.shape[1], cropped_img.shape[0]))
    else:
        cropped_img = img
        logging.error("    => Error cropping image: please only use either new width or new height, otherwise the image doesn\'t get cropped")

    return cropped_img


def resize_image(img, res_x, res_y):
    """
        Resize the given image to the given x and y resolution.

        :param img: image, which needs to be resized
        :param res_x: desired new x resolution
        :param res_y: desired new y resolution
        :return: resized image
    """
    logging.info("   => resize image to %sx%s" % (res_x, res_y))
    return cv2.resize(img, (res_x, res_y), interpolation=cv2.INTER_NEAREST)


def resize_image_aspect_ratio(img, new_width=None, new_height=None):
    """
        Resize the given image to either the given width or the given height by retaining the image's aspect ratio.

        :param img: image, which needs to be resized
        :param new_width: desired new x resolution
        :param new_height: desired new y resolution
        :return: resized image
    """
    height, width = img.shape[:2]
    if new_width and not new_height:
        r = new_width/width
        new_height = int(height*r)
    elif not new_width and new_height:
        r = new_height/height
        new_width = int(width*r)
    else:
        logging.error("    => Error at resizing image with aspect ratio: please only use either new width or new height, otherwise the aspect ratio changes")
    new_img = cv2.resize(img, (new_width, new_height))
    logging.info("   => Resize image (with aspect ratio) from [%s, %s] to [%s, %s]" % (img.shape[1], img.shape[0], new_img.shape[1], new_img.shape[0]))
    return new_img


def resize_aspect_ratio_crop_image(img, res_x, res_y):
    """
        Resize the given image to the given x and y resolution. To retain the image's aspect ratio, it gets cropped for
        the desired resolution.

        :param img: image, which needs to be resized
        :param res_x: desired new x resolution
        :param res_y: desired new y resolution
        :return: resized image
    """
    height, width = img.shape[:2]
    aspect_ratio_img = width / height
    aspect_ratio_desired = res_x / res_y
    if aspect_ratio_img > aspect_ratio_desired:  # horizontal images
        img_resized = resize_image_aspect_ratio(img, new_height=res_y)
        img_res_cropped = crop_image(img_resized, new_width=res_x)
    else:  # vertical and quadratic images
        img_resized = resize_image_aspect_ratio(img, new_width=res_x)
        img_res_cropped = crop_image(img_resized, new_height=res_y)
    logging.info("   => Resize (with aspect ratio) and crop image from [%s, %s] to [%s, %s]" % (img.shape[1], img.shape[0], img_res_cropped.shape[1], img_res_cropped.shape[0]))
    return img_res_cropped


def save_image(img, output_path, filename):
    """
        Save the given image the the given output directory with the given file name.

        :param img: image, which needs to be saved
        :param output_path: target path of image
        :param filename: file name of stored image
        :return:
    """
    output_file_path = os.path.join(output_path, filename)
    cv2.imwrite(output_file_path, img)
    logging.info("   => stored image to \'%s\'" % output_file_path)
