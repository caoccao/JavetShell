# Javet Shell Console Usage

Javet Shell Console is a console application that provides a Node.js flavored console interactions.

## Node.js Mode

Run `java -jar javet-shell-x.x.x.jar -r node` to activate the Node.js mode.

```js
N > // TODO
```

## V8 Mode

Run `java -jar javet-shell-x.x.x.jar -r v8` to activate the V8 mode.

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

## API

- Node.js Mode
  - [All Node.js API](https://nodejs.org/dist/latest-v20.x/docs/api/)
- V8 Mode
  - [console](https://www.caoccao.com/Javenode/reference/modules/console.html)
  - [timers](https://www.caoccao.com/Javenode/reference/modules/times.html)
