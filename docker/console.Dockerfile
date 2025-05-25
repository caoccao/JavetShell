# Copyright (c) 2021-2025. caoccao.com Sam Cao
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
#   docker build -t sjtucaocao/javet-shell:amd64-latest --platform linux/amd64 -f docker/console.Dockerfile .
#   docker build -t sjtucaocao/javet-shell:arm64-latest --platform linux/arm64 -f docker/console.Dockerfile .

FROM gradle:8.14-jdk17-jammy AS build

ENV VERSION=0.1.0

# Preparation
RUN apt-get update -y
RUN apt-get install -y unzip zip wget libatomic1

# Build JavetShell
WORKDIR /
RUN mkdir console
WORKDIR /console
COPY ./console/ .
RUN gradle build

# Build Cache
WORKDIR /console/build/libs
RUN unzip ./javet-shell-${VERSION}.jar 'libjavet-*.so'
RUN zip -d ./javet-shell-${VERSION}.jar libjavet-*.so

# Build Shell Scripts
WORKDIR /console/build/libs
RUN echo "java -Djavet.lib.loading.path=/ -Djavet.lib.loading.type=custom -jar javet-shell-${VERSION}.jar -r node" > javet-shell-node.sh
RUN echo "java -Djavet.lib.loading.path=/ -Djavet.lib.loading.type=custom -jar javet-shell-${VERSION}.jar -r v8" > javet-shell-v8.sh
RUN chmod +x *.sh

# Deploy
FROM eclipse-temurin:17-jre-jammy AS main

RUN apt-get update -y
RUN apt-get install -y libatomic1

WORKDIR /
COPY --from=build /console/build/libs/javet* .
COPY --from=build /console/build/libs/libjavet* .
