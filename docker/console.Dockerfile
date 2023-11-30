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

# Usage: docker build -t sjtucaocao/javet-shell:latest -f docker/console.Dockerfile .

FROM ubuntu:22.04

RUN apt-get update -y
RUN apt-get install -y openjdk-17-jdk
RUN apt-get install -y zip

WORKDIR /
COPY ./console/build/libs/javet-shell-0.1.0.jar .
RUN zip -d ./javet-shell-0.1.0.jar *.dll *.dylib
