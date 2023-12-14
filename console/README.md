# Javet Shell Console Usage

Javet Shell Console is a console application that provides a Node.js flavored console interactions.

## Node.js Mode

Run `java -jar javet-shell-${version}.jar -r node` to activate the Node.js mode.

```js
N > 1 + 1
2
N > let a = 1; let b = 2; a + b
3
N > setTimeout(() => console.log('\nMessage from setTimeout'), 1000)
6
N >
Message from setTimeout

undefined
N > import * as fs from 'node:fs'
>>> console.log(fs.existsSync('/abc'));
>>>
false
[object Promise]
N >
```

## V8 Mode

Run `java -jar javet-shell-${version}.jar -r v8` to activate the V8 mode.

```js
V > 1 + 1
2
V > let a = 1; let b = 2; a + b
3
V > setTimeout(() => console.log('\nMessage from setTimeout'), 1000)
1
V >
Message from setTimeout
```

## Access to JVM

JavetShell exposes the whole JVM to the console and gives you a real interactive Java REPL.

```js
V > let java = javet.getPackage('java')
undefined
V > let sb = new java.lang.StringBuilder()
undefined
V > sb.append(123)
123
V > sb.append('abc')
123abc
V > sb
123abc
V > java.util.Locale.US.toString()
en_US
V > java['.getPackages']().map(p => p['.name']).join(', ')
java.math, java.nio, java.lang, java.text, java.io, java.util, java.time, java.security, java.net
```

## API

- Node.js Mode
  - [All Node.js API](https://nodejs.org/dist/latest-v20.x/docs/api/)
- V8 Mode
  - [console](https://www.caoccao.com/Javenode/reference/modules/console.html)
  - [timers](https://www.caoccao.com/Javenode/reference/modules/times.html)
