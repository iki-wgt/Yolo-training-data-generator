FROM ubuntu:18.04

WORKDIR /root

RUN apt-get update && \
    apt-get -y install \
    git \
    vim \
    software-properties-common

# INSTALL PIP
RUN apt -y install python3-pip
RUN pip3 install --upgrade pip

# INSTALL PYTHON DEPENDENCIES
RUN pip3 install opencv-python
RUN pip3 install pyqt5

# INSTALL JAVA
RUN add-apt-repository ppa:webupd8team/java -y && \
    apt update && \
    apt-get install -y openjfx

# INSTALL X-WINDOWS
RUN apt-get install -qqy x11-apps
ENV DISPLAY :0

# CLONE PROJECT REPOSITORY
RUN git clone https://github.com/yknapp/deep_learning_data_generator.git

# SET ENVIRONMENT VARIABLES
RUN echo 'alias python=python3' >> ~/.bashrc
RUN echo 'alias pip=pip3' >> ~/.bashrc
