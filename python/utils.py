import os
import sys
import pickle
import logging
import datetime


def get_project_path():
    """
        Get the path of the project directory of ImgGenerator.

        :return: path to project directory
    """
    return os.path.realpath(os.path.join(os.path.abspath(os.path.dirname(__file__)), '../'))


def get_python_project_path():
    """
        Get current directory, in which program got executed.

        :return: path to execution directory
    """
    return os.path.abspath(os.path.dirname(__file__))


def create_folder(folder_path):
    """
        Create a new folder in the given path.

        :param folder_path: path of the new folder
        :return:
    """
    try:
        os.mkdir(folder_path)
        logging.info("Created folder \'%s\'" % folder_path)
    except FileExistsError:
        logging.info("Folder \'%s\' already exists" % folder_path)


def open_file(file_path):
    """
        Open a file at the given path and return its content.

        :param file_path: path to file
        :return: content of file
    """
    try:
        return open(file_path)
    except FileNotFoundError:
        logging.error("Error: Die Datei '%s' existiert nicht" % file_path)
        sys.exit()


def read_file(file_path):
    """
        Read the content of a file at the given path.

        :param file_path: path to file
        :return: content of file
    """
    try:
        with open(file_path, 'r') as file:
            return file.read()
    except FileNotFoundError:
        logging.error("Error: Die Datei '%s' existiert nicht" % file_path)
        sys.exit()


def write_file(file_path, file_content):
    """
        Write the given content in a file at the given path.

        :param file_path: path to file
        :param file_content: desired file content
        :return:
    """
    with open(file_path, 'w') as file:
        file.write(file_content)


def create_txt_file(output_path, filename, content_str, file_ext='txt'):
    """
        Create a new file at the given path with the given filename, its desired content and its file extension.

        :param output_path: path, where file should be stored
        :param filename: name of the created file
        :param content_str: content of the created file
        :param file_ext: file extension of the created file
        :return:
    """
    file = open(os.path.abspath(os.path.join(output_path, filename + '.' + file_ext)), 'w')
    file.write(content_str)
    file.close()


def save_to_pickle(filename="test", _object=None):
    """
        Save the given object into a pickle with the given name.

        :param filename: name of the pickle file
        :param _object: object, which should be stored inside the pickle file
        :return:
    """
    with open((filename+".pickle"), 'wb') as handle:
        pickle.dump(_object, handle, protocol=pickle.HIGHEST_PROTOCOL)


def read_from_pickle(filename):
    """
        Extract the object out of a desired pickle file.

        :param filename: name of the pickle file
        :return: object stored inside pickle file
    """
    try:
        with open((filename+".pickle"), 'rb') as handle:
            _object = pickle.load(handle)
        return _object
    except FileNotFoundError as error:
        logging.error(error)
        return None


def setup_logging():
    """
        Setup logging, so program output gets printed on console and stored into a log file.

        :return:
    """
    filename = str(datetime.datetime.now()).replace(' ', '_').replace(':', '-')
    logs_path = os.path.join(get_python_project_path(), 'logs/')
    # create 'logs' directory
    try:
        os.mkdir(logs_path)
    except FileExistsError:
        pass
    logging.basicConfig(filename=os.path.join(logs_path, filename+'.log'), level=logging.INFO, format='')
    logging.getLogger().addHandler(logging.StreamHandler())  # add handler so the output also gets printed to console


def list_directory(path, file_ext_list=None, error_quit=True):
    """
        List all file names with the given file extensions inside the directory of the given path.

        :param path: path of file directory
        :param file_ext_list: list of extensions, which should be concerned
        :param error_quit: if bool true, then quit program when error occurs
        :return: list of all file names inside the given directory of the given file extensions
    """
    try:
        if file_ext_list:
            file_path_list = []
            for file_path in os.listdir(path):
                if file_path.lower().endswith(tuple(file_ext_list)):
                    file_path_list.append(file_path)
        else:
            file_path_list = os.listdir(path)
    except FileNotFoundError as error:
        logging.error(error)
        if error_quit:
            sys.exit()
        else:
            file_path_list = []
    return file_path_list


def extract_range(range_str):
    """
        Split a range string in the form of 'x:x' into two float values.

        :param range_str: string containing two values seperated by a colon
        :return: min_value, max_value: float values extracted out of range string
    """
    try:
        min_value_str, max_value_str = range_str.split(':')
        min_value = to_float(min_value_str)
        max_value = to_float(max_value_str)
    except ValueError as v_err:
        logging.error(str(v_err))
        sys.exit()
    except Exception as exc:
        logging.error(str(exc))
        sys.exit()
    return min_value, max_value


def to_int(value):
    """
        Cast given value into an integer.

        :param value: value, which should be casted
        :return: integer value
    """
    try:
        i = int(value)
    except ValueError as verr:
        logging.error(str(verr))
        sys.exit(-1)
    except Exception as ex:
        logging.error(str(ex))
        sys.exit(-1)

    return i


def to_float(value):
    """
        Cast given value into a float.

        :param value: value, which should be casted
        :return: float value
    """
    try:
        f = float(value)
    except ValueError as verr:
        logging.error(str(verr))
        sys.exit(-1)
    except Exception as ex:
        logging.error(str(ex))
        sys.exit(-1)

    return f


def to_str(value):
    """
        Cast given value into a string.

        :param value: value, which should be casted
        :return: string value
    """
    try:
        string = str(value)
    except ValueError as verr:
        logging.error(str(verr))
        sys.exit(-1)
    except Exception as ex:
        logging.error(str(ex))
        sys.exit(-1)

    return string


def hex_to_int(hex_str):
    """
        Cast given hexadecimal string into an integer.

        :param hex_str: string containing hexadecimal value
        :return: integer value
    """
    try:
        val_int = int("0x" + hex_str, 16)
    except ValueError as v_err:
        logging.error(str(v_err))
        sys.exit()
    except Exception as exc:
        logging.error(str(exc))
        sys.exit()

    return val_int
