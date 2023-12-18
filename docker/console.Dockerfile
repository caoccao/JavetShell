# Copyright (c) 2021-2023 caoccao.com Sam Cao
# All rights reserved.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# Usage:
#   docker build -t sjtucaocao/javet-shell:latest --platform linux/amd64 -f docker/console.Dockerfile .
#   docker build -t sjtucaocao/javet-shell:latest --platform linux/arm64 -f docker/console.Dockerfile .

FROM ubuntu:22.04

RUN apt-get update -y
RUN apt-get install -y openjdk-17-jdk
RUN apt-get install -y unzip zip wget

# Install Gradle
WORKDIR /
RUN wget https://services.gradle.org/distributions/gradle-8.2-bin.zip
RUN mkdir /opt/gradle
RUN unzip -d /opt/gradle gradle-8.2-bin.zip
ENV GRADLE_HOME="/opt/gradle/gradle-8.2"
ENV PATH=$GRADLE_HOME/bin:$PATH

# Build JavetShell
WORKDIR /
RUN mkdir console
WORKDIR /console
COPY ./console/ .
RUN gradle build

# Build Cache
ENV VERSION=0.1.0
WORKDIR /
RUN mv /console/build/libs/javet-shell-${VERSION}.jar ./
RUN unzip ./javet-shell-${VERSION}.jar '*.so'
RUN zip -d ./javet-shell-${VERSION}.jar *.dll *.dylib *.so

# Build Shell Scripts
WORKDIR /
RUN echo "java -Djavet.lib.loading.path=/ -Djavet.lib.loading.type=custom -jar javet-shell-${VERSION}.jar -r node" > javet-shell-node.sh
RUN echo "java -Djavet.lib.loading.path=/ -Djavet.lib.loading.type=custom -jar javet-shell-${VERSION}.jar -r v8" > javet-shell-v8.sh
RUN chmod +x *.sh

# Cleanup
WORKDIR /
RUN apt-get clean -y
RUN rm -rf /var/lib/apt/lists/*
RUN rm gradle-8.2-bin.zip
RUN rm -rf /opt/gradle
RUN rm -rf /console
