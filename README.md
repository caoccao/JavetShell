# Javet Shell

[![Android Build](https://github.com/caoccao/JavetShell/actions/workflows/android_build.yml/badge.svg)](https://github.com/caoccao/JavetShell/actions/workflows/android_build.yml) [![Console Build](https://github.com/caoccao/JavetShell/actions/workflows/console_build.yml/badge.svg)](https://github.com/caoccao/JavetShell/actions/workflows/console_build.yml)

Javet Shell is a console or Android application that provides Node.js flavored console interactions. It is a sample application of [Javet](https://github.com/caoccao/Javet).

## Features

- [Android](android) (Implementing)
- [Linux + MacOS + Windows](console) (Implementing)
- Complete access to JVM
- Debug with Chrome Dev Tools

## Quick Start

### Android

- Download the apk file from the latest [action](https://github.com/caoccao/JavetShell/actions/workflows/android_build.yml).
- Install the apk file.

Refer to this [page](android) for details.

### Console

```shell
Usage: Javet Shell options_list
Options:
    --runtimeType, -r [V8] -> JS runtime type { Value should be one of [node, v8] }
    --scriptName, -s [main.js] -> Script name { String }
    --help, -h -> Usage info
```

#### Manual

- Download the jar file from the latest [action](https://github.com/caoccao/JavetShell/actions/workflows/console_build.yml) on Linux or Windows.
- Run `java -jar javet-shell-0.1.0.jar`

#### Docker

- Run `docker run --rm -it --entrypoint bash sjtucaocao/javet-shell:latest`
- Run `./javet-shell-node.sh` or `./javet-shell-v8.sh`

Refer to this [page](console) for details.

## License

[APACHE LICENSE, VERSION 2.0](LICENSE)
