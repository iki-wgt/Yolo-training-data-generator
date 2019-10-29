import os
import sys
import hsv
import cv2
import copy
import utils
import logging
import numpy as np
import img_processing
from objects import HSV
from PyQt5.QtWidgets import QWidget, QSlider, QLabel, QPushButton, QApplication, QDesktopWidget, QGridLayout, QFileDialog, QMessageBox
from PyQt5.QtGui import QPixmap, QImage
from PyQt5.QtCore import Qt

# window resolution
WINDOW_WIDTH = 800
WINDOW_HEIGHT = 500

# upper bounds of HSV values
H_MIN = 0
H_MAX = 360
S_MIN = 0
S_MAX = 255
V_MIN = 0
V_MAX = 255

# standard image directory
STD_IMG_DIRECTORY = os.path.join(os.path.dirname(__file__), os.path.realpath('../images/'))


class Window(QWidget):
    def __init__(self, obj_img_path=None):
        super().__init__()
        self.window_title = "HSV Finder"
        self.width = WINDOW_WIDTH
        self.height = WINDOW_HEIGHT
        self.img = None
        self.img_trans = None
        self.img_directory = obj_img_path
        self.img_index = 0
        self.img_num = 0
        self.img_label = None
        self.label_h_min = None
        self.label_h_max = None
        self.label_s_min = None
        self.label_s_max = None
        self.label_v_min = None
        self.label_v_max = None
        self.sl_h_min = None
        self.sl_h_max = None
        self.sl_s_min = None
        self.sl_s_max = None
        self.sl_v_min = None
        self.sl_v_max = None
        self.std_hsv = HSV()
        self.curr_hsv = HSV()

        self.initUI()

    def center(self):
        """
            Move the GUI window to the center of the screen.

            :return:
        """
        qr = self.frameGeometry()
        cp = QDesktopWidget().availableGeometry().center()
        qr.moveCenter(cp)
        self.move(qr.topLeft())

    def reset_img_index(self):
        """
            Set the image index of the directory to the beginning.

            :return:
        """
        self.img_index = 0

    @staticmethod
    def convert_np_to_q_img(cv_img):
        """
            Convert numpy nd array image to QImage.

            :param cv_img: nd array image, which needs to be converted
            :return: converted QImage
        """
        image = QImage(cv_img, cv_img.shape[1], cv_img.shape[0], cv_img.shape[1] * 3, QImage.Format_RGB888).rgbSwapped()
        return QPixmap(image)

    def process_img(self):
        """
            Remove green color out of self.image and merge it with checkerboard image.

            :return: Image containing cropped object on checkerboard background
        """
        # add alpha channel to object image
        b_channel, g_channel, r_channel = cv2.split(self.img)
        alpha_channel = np.ones(b_channel.shape, dtype=self.img.dtype)
        img_obj = cv2.merge((b_channel, g_channel, r_channel, alpha_channel))

        # remove green color and crop transparent pixels
        img_obj_greenless = img_processing.rmv_green(img_obj, hsv_ranges=self.curr_hsv)
        img_obj_cropped = img_processing.crop_transparent_pixels(img_obj_greenless)

        # place object onto a black background
        merged_img, obj_bbox = img_processing.paste_foreground_into_background(img_obj_cropped, self.img_trans)

        # draw bounding box around object
        img_processing.draw_bounding_box(merged_img, obj_bbox)

        # remove alpha channel (QT doesn't like that)
        b, g, r, alpha = cv2.split(merged_img)
        result_img = cv2.merge((b, g, r))

        return result_img

    def set_img(self):
        """
            Load current image by image index, resize it and create checkerboard image with the same shape.

            :return:
        """
        img_path_list = utils.list_directory(self.img_directory, ['.jpg'])
        img_path = img_path_list[self.img_index]
        img = cv2.imread(os.path.join(self.img_directory, img_path), cv2.IMREAD_UNCHANGED)
        self.img = img_processing.resize_image_aspect_ratio(img, new_width=WINDOW_WIDTH)
        self.img_trans = img_processing.create_checkerboard_pattern(self.img.shape)
        self.update_img()

    def update_img(self):
        """
            Convert current image to QPixmap and display it on image label.

            :return:
        """
        processed_img = self.process_img()
        q_img = self.convert_np_to_q_img(processed_img)
        self.img_label.setPixmap(q_img)

    def set_img_directory(self):
        """
            Ask user to pick valid image folder and then load images in chosen directory.

            :return:
        """
        path_valid_bool = False
        while not path_valid_bool:
            # if img_path is invalid, let user choose another one
            directory_file_num = len(utils.list_directory(self.img_directory, ['.jpg'], error_quit=False))
            if directory_file_num > 0:
                self.img_num = directory_file_num
                self.reset_img_index()
                self.set_img()
                path_valid_bool = True
            else:
                self.ask_for_img_path()

    def ask_for_img_path(self):
        """
            Ask user to pick directory, check its validity and load its containing images.

            :return:
        """
        while True:
            img_path = str(QFileDialog.getExistingDirectory(self, "Select image directory", STD_IMG_DIRECTORY))
            if not img_path:  # stop program, if user clicks 'abort' and self.img_path is not initialized
                sys.exit()
            if not img_path:  # ignore, if user clicks 'abort'
                break
            if len(utils.list_directory(img_path, ['.jpg'])) > 0:  # if chosen directory contains JPEG files, break loop
                self.img_directory = img_path
                self.img_num = len(utils.list_directory(img_path, ['.jpg']))
                self.reset_img_index()
                self.set_img()
                break
            else:
                logging.info("The selected directory doesn\'t contain JPEG files. Please choose another one.")

    def handle_prev_btn(self):
        """
            Click listener for 'previous' button, which reduces the image index by one.

            :return:
        """
        if self.img_index > 0:
            self.img_index -= 1
            self.set_img()

    def handle_next_btn(self):
        """
            Click listener for 'next' button, which increments the image index by one.

            :return:
        """
        if self.img_index < (self.img_num-1):
            self.img_index += 1
            self.set_img()

    def handle_ch_dir_btn(self):
        """
            Click listener for 'change directory' button, which lets user pick new image directory.

            :return:
        """
        self.ask_for_img_path()

    def handle_reset_btn(self):
        """
            Click listener for 'reset' button, which resets all HSV values back to the stored ones.

            :return:
        """
        self.curr_hsv = copy.copy(self.std_hsv)
        self.sl_h_min.setValue(self.std_hsv.h_min)
        self.sl_h_max.setValue(self.std_hsv.h_max)
        self.sl_s_min.setValue(self.std_hsv.s_min)
        self.sl_s_max.setValue(self.std_hsv.s_max)
        self.sl_v_min.setValue(self.std_hsv.v_min)
        self.sl_v_max.setValue(self.std_hsv.v_max)
        self.update_img()

    def handle_save_btn(self):
        """
            Click listener for 'save' button, which saves the current HSV values into a pickle file.

            :return:
        """
        if not self.curr_hsv.is_equal_to(self.std_hsv):
            ret = QMessageBox.question(self, '', "Do you really want to save current HSV values?", QMessageBox.Yes | QMessageBox.No)
            if ret == QMessageBox.Yes:
                hsv.set_hsv_std_values(self.curr_hsv)
                self.std_hsv = self.curr_hsv.copy()

    def handle_cancel_btn(self):
        """
            Click listener for 'cancel' button, which terminates the program.

            :return:
        """
        if not self.curr_hsv.is_equal_to(self.std_hsv):
            ret = QMessageBox.question(self, '', "Do you really want to discard changes?", QMessageBox.Yes | QMessageBox.No)
            if ret == QMessageBox.Yes:
                sys.exit()
        else:
            sys.exit()

    def keyPressEvent(self, event):
        """
            Key listener for Escape, Space, A and D keys.

            :param event:
            :return:
        """
        if event.key() == Qt.Key_Escape:
            self.handle_cancel_btn()
        elif event.key() == Qt.Key_Space:
            self.handle_reset_btn()
        elif event.key() == Qt.Key_A:
            self.handle_prev_btn()
        elif event.key() == Qt.Key_D:
            self.handle_next_btn()

    def update_h_min(self):
        """
            Update displayed H_min value and trigger image processing with new value.

            :return:
        """
        self.label_h_min.setText(str(self.sl_h_min.value()))
        self.curr_hsv.h_min = self.sl_h_min.value()
        self.update_img()

    def update_h_max(self):
        """
            Update displayed H_max value and trigger image processing with new value.

            :return:
        """
        self.label_h_max.setText(str(self.sl_h_max.value()))
        self.curr_hsv.h_max = self.sl_h_max.value()
        self.update_img()

    def update_s_min(self):
        """
            Update displayed S_min value and trigger image processing with new value.

            :return:
        """
        self.label_s_min.setText(str(self.sl_s_min.value()))
        self.curr_hsv.s_min = self.sl_s_min.value()
        self.update_img()

    def update_s_max(self):
        """
            Update displayed S_max value and trigger image processing with new value.

            :return:
        """
        self.label_s_max.setText(str(self.sl_s_max.value()))
        self.curr_hsv.s_max = self.sl_s_max.value()
        self.update_img()

    def update_v_min(self):
        """
            Update displayed V_min value and trigger image processing with new value.

            :return:
        """
        self.label_v_min.setText(str(self.sl_v_min.value()))
        self.curr_hsv.v_min = self.sl_v_min.value()
        self.update_img()

    def update_v_max(self):
        """
            Update displayed V_max value and trigger image processing with new value.

            :return:
        """
        self.label_v_max.setText(str(self.sl_v_max.value()))
        self.curr_hsv.v_max = self.sl_v_max.value()
        self.update_img()

    def create_slider_with_label(self, name, min, max, default):
        """
            Create slider with info and value label.

            :param name: name of slider
            :param min: min value of slider
            :param max: max value of slider
            :param default: default value of slider
            :return: info label, value label and slider object
        """
        # info_label
        info_label = QLabel(name)
        info_label.setAlignment(Qt.AlignCenter)
        # slider
        slider = QSlider(Qt.Horizontal, self)
        slider.setMinimum(min)
        slider.setMaximum(max)
        slider.setValue(default)
        slider.setTickPosition(QSlider.TicksBelow)
        slider.setTickInterval(25)
        # info_label
        value_label = QLabel(self)
        value_label.setText(str(default))
        value_label.setAlignment(Qt.AlignCenter)
        return info_label, slider, value_label

    def initUI(self):
        """
            Setup GUI by selecting stored HSV values, desired image directory and creating all GUI elements.

            :return:
        """
        # get HSV values out of pickle file
        # import HSV values from 'hsv.pickle'
        self.std_hsv = hsv.get_hsv_std_values()
        self.curr_hsv = self.std_hsv.copy()

        grid = QGridLayout()
        grid.setSpacing(10)

        # add image to label
        self.img_label = QLabel(self)
        #self.img_label.setFixedSize(self.width, self.height)
        self.img_label.setScaledContents(True)
        grid.addWidget(self.img_label, 0, 0, 3, 8)

        # ask user for image directory and put selected image into img_label
        if not self.img_directory:
            self.ask_for_img_path()
        else:
            self.set_img_directory()

        # Previous button
        prev_btn = QPushButton('Prev', self)
        prev_btn.setToolTip("Go to previous image")
        prev_btn.clicked.connect(self.handle_prev_btn)
        prev_btn.resize(prev_btn.sizeHint())
        grid.addWidget(prev_btn, 3, 0, 1, 2)

        # Next button
        next_btn = QPushButton('Next', self)
        next_btn.setToolTip("Go to next image")
        next_btn.clicked.connect(self.handle_next_btn)
        next_btn.resize(next_btn.sizeHint())
        grid.addWidget(next_btn, 3, 2, 1, 2)

        # Directory button
        ch_dir_btn = QPushButton('Change directory', self)
        ch_dir_btn.setToolTip("Change image directory")
        ch_dir_btn.clicked.connect(self.handle_ch_dir_btn)
        ch_dir_btn.resize(ch_dir_btn.sizeHint())
        grid.addWidget(ch_dir_btn, 3, 4, 1, 2)

        # Reset button
        reset_btn = QPushButton('Reset values', self)
        reset_btn.setToolTip("Reset HSV to its standard values")
        reset_btn.clicked.connect(self.handle_reset_btn)
        reset_btn.resize(reset_btn.sizeHint())
        grid.addWidget(reset_btn, 3, 6, 1, 2)

        #########################################################################
        # add slider with labels

        # h_min
        h_min_info_label, self.sl_h_min, self.label_h_min = self.create_slider_with_label("H min", H_MIN, H_MAX, self.std_hsv.h_min)
        grid.addWidget(h_min_info_label, 4, 0)
        self.sl_h_min.valueChanged.connect(self.update_h_min)
        grid.addWidget(self.sl_h_min, 4, 1, 1, 6)
        grid.addWidget(self.label_h_min, 4, 7)

        # h_max
        h_max_info_label, self.sl_h_max, self.label_h_max = self.create_slider_with_label("H max", H_MIN, H_MAX, self.std_hsv.h_max)
        grid.addWidget(h_max_info_label, 5, 0)
        self.sl_h_max.valueChanged.connect(self.update_h_max)
        grid.addWidget(self.sl_h_max, 5, 1, 1, 6)
        grid.addWidget(self.label_h_max, 5, 7)

        # s_min
        s_min_info_label, self.sl_s_min, self.label_s_min = self.create_slider_with_label("S min", S_MIN, S_MAX, self.std_hsv.s_min)
        grid.addWidget(s_min_info_label, 6, 0)
        self.sl_s_min.valueChanged.connect(self.update_s_min)
        grid.addWidget(self.sl_s_min, 6, 1, 1, 6)
        grid.addWidget(self.label_s_min, 6, 7)

        # s_max
        s_max_info_label, self.sl_s_max, self.label_s_max = self.create_slider_with_label("S max", S_MIN, S_MAX, self.std_hsv.s_max)
        grid.addWidget(s_max_info_label, 7, 0)
        self.sl_s_max.valueChanged.connect(self.update_s_max)
        grid.addWidget(self.sl_s_max, 7, 1, 1, 6)
        grid.addWidget(self.label_s_max, 7, 7)

        # v_min
        v_min_info_label, self.sl_v_min, self.label_v_min = self.create_slider_with_label("V min", V_MIN, V_MAX, self.std_hsv.v_min)
        grid.addWidget(v_min_info_label, 8, 0)
        self.sl_v_min.valueChanged.connect(self.update_v_min)
        grid.addWidget(self.sl_v_min, 8, 1, 1, 6)
        grid.addWidget(self.label_v_min, 8, 7)

        # v_max
        v_max_info_label, self.sl_v_max, self.label_v_max = self.create_slider_with_label("V max", V_MIN, V_MAX, self.std_hsv.v_max)
        grid.addWidget(v_max_info_label, 9, 0)
        self.sl_v_max.valueChanged.connect(self.update_v_max)
        grid.addWidget(self.sl_v_max, 9, 1, 1, 6)
        grid.addWidget(self.label_v_max, 9, 7)

        # save button
        save_btn = QPushButton('Save', self)
        save_btn.setToolTip("Save HSV changes")
        save_btn.clicked.connect(self.handle_save_btn)
        grid.addWidget(save_btn, 10, 4, 1, 2)

        # cancel button
        cancel_btn = QPushButton('Cancel', self)
        cancel_btn.setToolTip("Discard HSV changes")
        cancel_btn.clicked.connect(self.handle_cancel_btn)
        grid.addWidget(cancel_btn, 10, 6, 1, 2)

        # window setup
        self.setLayout(grid)
        self.resize(self.width, self.height)
        self.center()
        self.setWindowTitle(self.window_title)


def start_hsv_finder(obj_img_path=None):
    """
        Start HSV-Finder application.

        :param obj_img_path: path of directory containing desired object images
        :return:
    """
    logging.info("Starting HSV-Finder\n")
    app = QApplication(sys.argv)
    ex = Window(obj_img_path)
    ex.show()
    sys.exit(app.exec_())


if __name__ == '__main__':
    start_hsv_finder()
