import utils


class BackgroundFilter:
    """
        Filter object class containing parameters for background creation.
    """
    def __init__(self, type_str='1', value_str='0'):
        self.type = utils.to_int(type_str)
        self.value = value_str


class TranslateFilter:
    """
        Filter object class containing parameters for translation manipulation.
    """
    def __init__(self, x_range_str='0:0', y_range_str='0:0'):
        self.x_min, self.x_max = utils.extract_range(x_range_str)
        self.y_min, self.y_max = utils.extract_range(y_range_str)


class ScaleFilter:
    """
        Filter object class containing parameters for scale manipulation.
    """
    def __init__(self, scale_range_str='0:0'):
        self.min, self.max = utils.extract_range(scale_range_str)


class RotateFilter:
    """
        Filter object class containing parameters for rotate manipulation.
    """
    def __init__(self, angle_range_str='0:0'):
        self.min, self.max = utils.extract_range(angle_range_str)


class ClipFilter:
    """
        Filter object class containing parameters for clipping manipulation.
    """
    def __init__(self, x_range_str='0:0', y_range_str='0:0'):
        self.x_min, self.x_max = utils.extract_range(x_range_str)
        self.y_min, self.y_max = utils.extract_range(y_range_str)


class NoiseFilter:
    """
        Filter object class containing parameters for noise insertion.
    """
    def __init__(self, target='0', noise_range_str='0:0'):
        self.target = utils.to_int(target)
        self.min, self.max = utils.extract_range(noise_range_str)


class HueFilter:
    """
        Filter object class containing parameters for hue manipulation.
    """
    def __init__(self, target='0', hue_range_str='0:0'):
        self.target = utils.to_int(target)
        self.min, self.max = utils.extract_range(hue_range_str)


class SaturationFilter:
    """
        Filter object class containing parameters for saturation manipulation.
    """
    def __init__(self, target='0', sat_range_str='0:0'):
        self.target = utils.to_int(target)
        self.min, self.max = utils.extract_range(sat_range_str)


class ValueFilter:
    """
        Filter object class containing parameters for value manipulation.
    """
    def __init__(self, target='0', val_range_str='0:0'):
        self.target = utils.to_int(target)
        self.min, self.max = utils.extract_range(val_range_str)


class BrightnessFilter:#
    """
        Filter object class containing parameters for brightness manipulation.
    """
    def __init__(self, target='0', bright_range_str='0:0'):
        self.target = utils.to_int(target)
        self.min, self.max = utils.extract_range(bright_range_str)


class ContrastFilter:
    """
        Filter object class containing parameters for contrast manipulation.
    """
    def __init__(self, target='0', contr_range_str='0:0'):
        self.target = utils.to_int(target)
        self.min, self.max = utils.extract_range(contr_range_str)


class GammaFilter:
    """
        Filter object class containing parameters for gamma manipulation.
    """
    def __init__(self, target='0', gamma_range_str='0:0'):
        self.target = utils.to_int(target)
        self.min, self.max = utils.extract_range(gamma_range_str)


class BlurFilter:
    """
        Filter object class containing parameters for blur insertion.
    """
    def __init__(self, target='0', blur_range_str='0:0'):
        self.target = utils.to_int(target)
        self.min, self.max = utils.extract_range(blur_range_str)


class OverlayFilter:
    """
        Filter object class containing parameters for overlay insertion.
    """
    def __init__(self, filter='0', target='0', intensity='0', path_type='0', overlay_img_path=''):
        self.filter = utils.to_int(filter)
        self.target = utils.to_int(target)
        self.intensity = utils.to_int(intensity)
        self.path_type = utils.to_int(path_type)
        self.overlay_img_path = overlay_img_path


class OverlapFilter:
    """
        Filter object class containing parameters for overlap insertion.
    """
    def __init__(self, type='0', path_type='0', overlap_img_path='', x_pct_range='0:0', y_pct_range='0:0', scale=None, rotate=None, noise=None, hue=None, saturation=None, value=None, brightness=None, contrast=None, gamma=None, blur=None, overlay=None):
        self.type = utils.to_int(type)
        self.path_type = utils.to_int(path_type)
        self.overlap_img_path = overlap_img_path
        self.x_pct_min, self.x_pct_max = utils.extract_range(x_pct_range)
        self.y_pct_min, self.y_pct_max = utils.extract_range(y_pct_range)
        self.scale = scale
        self.rotate = rotate
        self.noise = noise
        self.hue = hue
        self.saturation = saturation
        self.value = value
        self.brightness = brightness
        self.contrast = contrast
        self.gamma = gamma
        self.blur = blur
        self.overlay = overlay


class AddObjectFilter:
    """
        Filter object class containing parameters for adding objects.
    """
    def __init__(self, num_obj_range_str='0:0', path_type='0', add_obj_path='', translate=None, scale=None, rotate=None, clip=None, noise=None, hue=None, saturation=None, value=None, brightness=None, contrast=None, gamma=None, blur=None, overlay=None):
        self.obj_min, self.obj_max = utils.extract_range(num_obj_range_str)
        self.path_type = utils.to_int(path_type)
        self.add_obj_path = add_obj_path
        self.translate = translate
        self.scale = scale
        self.rotate = rotate
        self.clip = clip
        self.noise = noise
        self.hue = hue
        self.saturation = saturation
        self.value = value
        self.brightness = brightness
        self.contrast = contrast
        self.gamma = gamma
        self.blur = blur
        self.overlay = overlay


class ResolutionFilter:
    """
        Filter object class containing parameters for output image resolution.
    """
    def __init__(self, x_range, y_range):
        self.x_min, self.x_max = utils.extract_range(x_range)
        self.y_min, self.y_max = utils.extract_range(y_range)


class GeneralSettings:
    """
        Object class containing general settings for image generation.
    """
    def __init__(self, object_path="", background_path="", output_path="", output_height=0, output_width=0):
        self.object_path = object_path
        self.background_path = background_path
        self.output_path = output_path
        self.output_height = output_height
        self.output_width = output_width


class FilterChainSettings:
    """
        Filter chain object class containing all kinds of different filters.
    """
    def __init__(self, num_obj_per_img='1:1', background=BackgroundFilter(type_str='1'),
                 translate=TranslateFilter('50:50', '50:50'), scale=None, rotate=None, clip=None, noise=None, hue=None,
                 saturation=None, value=None, brightness=None, contrast=None, gamma=None, blur=None, overlay=None,
                 overlap=None, add_obj=None, resolution=None):
        self.num_obj_per_img = num_obj_per_img
        self.background = background
        self.translate = translate
        self.scale = scale
        self.rotate = rotate
        self.clip = clip
        self.noise = noise
        self.hue = hue
        self.saturation = saturation
        self.value = value
        self.brightness = brightness
        self.contrast = contrast
        self.gamma = gamma
        self.blur = blur
        self.overlay = overlay
        self.overlap = overlap
        self.add_obj = add_obj
        self.resolution = resolution


class Object:
    """
        Object object containing the image of the object with its filename and bounding box coordinates
    """
    def __init__(self, filename=None, image=None, bbox=None, category=None, x_offset=None, y_offset=None):
        self.filename = filename
        self.image = image
        self.bbox = bbox
        self.category = category
        self.x_offset = x_offset
        self.y_offset = y_offset


class BoundingBox:
    """
        Bounding box object class containing x and y coordinates.
    """
    def __init__(self, x_min, x_max, y_min, y_max):
        self.x_min = x_min
        self.x_max = x_max
        self.y_min = y_min
        self.y_max = y_max

    def __str__(self):
        return "x_min: %s, x_max: %s, y_min: %s, y_max: %s" % (self.x_min, self.x_max, self.y_min, self.y_max)


class HSV:
    """
        HSV object class containing minimum and maximum values for hue, saturation and value.
    """
    def __init__(self, h_min=31, h_max=90, s_min=60, s_max=255, v_min=36, v_max=255):
        self.h_min = h_min
        self.h_max = h_max
        self.s_min = s_min
        self.s_max = s_max
        self.v_min = v_min
        self.v_max = v_max

    def is_equal_to(self, hsv_obj):
        """
            Check, whether two HSV objects contain the same information.
            :param hsv_obj: HSV object to compare
            :return: boolean, whether HSV information is the same
        """
        if self.h_min == hsv_obj.h_min and self.h_max == hsv_obj.h_max \
                and self.s_min == hsv_obj.s_min and self.s_max == hsv_obj.s_max \
                and self.v_min == hsv_obj.v_min and self.v_max == hsv_obj.v_max:
            return True
        else:
            return False

    def copy(self):
        """
            Copy information of an existing HSV object into another HSV object.
            :return: hsv_obj: copy of HSV object
        """
        hsv_obj = HSV()
        hsv_obj.h_min = self.h_min
        hsv_obj.h_max = self.h_max
        hsv_obj.s_min = self.s_min
        hsv_obj.s_max = self.s_max
        hsv_obj.v_min = self.v_min
        hsv_obj.v_max = self.v_max
        return hsv_obj

    def __str__(self):
        return "h_min: %s, h_max: %s, s_min: %s, s_max: %s, v_min: %s, v_max: %s" % (self.h_min, self.h_max, self.s_min,
                                                                                     self.s_max, self.v_min, self.v_max)
