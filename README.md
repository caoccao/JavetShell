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

- Download the jar file from the latest [action](https://github.com/caoccao/JavetShell/actions/workflows/console_build.yml).
- Run `java -jar javet-shell-x.x.x.jar`

```shell
Usage: Javet Shell options_list
Options:
    --runtimeType, -r [V8] -> JS runtime type { Value should be one of [node, v8] }
    --scriptName, -s [main.js] -> Script name { String }
    --help, -h -> Usage info
```

Refer to this [page](console) for details.

## License

[APACHE LICENSE, VERSION 2.0](https://github.com/caoccao/Javet/blob/main/LICENSE)
