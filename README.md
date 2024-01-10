# Javet Shell

[![Android Build](https://github.com/caoccao/JavetShell/actions/workflows/android_build.yml/badge.svg)](https://github.com/caoccao/JavetShell/actions/workflows/android_build.yml) [![Console Build](https://github.com/caoccao/JavetShell/actions/workflows/console_build.yml/badge.svg)](https://github.com/caoccao/JavetShell/actions/workflows/console_build.yml)

Javet Shell is a console or Android application that provides Node.js flavored console interactions. It is a sample application of [Javet](https://github.com/caoccao/Javet).

## Features

- [Android](android)
- [Linux + MacOS + Windows](console)
- Complete access to JVM
- Debug with Chrome Dev Tools

## Quick Start

### Android

- Download the apk file from the latest [action](https://github.com/caoccao/JavetShell/actions/workflows/android_build.yml).
- Install the apk file.

Please refer to this [page](android) for details.

### Console

```shell
Usage: Javet Shell options_list
Options:
    --debugPort, -p [9229] -> Debug port { Int }
    --runtimeType, -r [V8] -> JS runtime type { Value should be one of [node, v8] }
    --scriptName, -s [main.js] -> Script name { String }
    --verbose, -v [false] -> Verbose
    --help, -h -> Usage info
```

#### Manual

- Download the jar file from the latest [action](https://github.com/caoccao/JavetShell/actions/workflows/console_build.yml).
- Run `java -jar javet-shell-0.1.0.jar`

#### Docker

- Run the following command to run the Docker container.

```sh
# amd64
docker run --rm -it -p 9229:9229 --entrypoint bash sjtucaocao/javet-shell:amd64-latest
# arm64
docker run --rm -it -p 9229:9229 --entrypoint bash sjtucaocao/javet-shell:arm64-latest
```

- Run the following command to start the JavetShell console.

```sh
# amd64
./javet-shell-node.sh
# arm64
./javet-shell-v8.sh
```

Please refer to this [page](console) for details.

## License

[APACHE LICENSE, VERSION 2.0](LICENSE)
