# 2048

Fork of the popular game "2048"

## Differences from the original

### assets/Game/index.html

After `<div class="game-container">...</div>`, remove the three `<p>...</p>` nodes.

Remove the tag `<h1 class="title">2048</h1>`.

### assets/Game/js/application.js

Above `window.requestAnimationFrame(...)`, initialize a new variable with `var gameManager;`.

Replace `new GameManager(...)` with `gameManager = new GameManager(...)`.

### assets/Game/style/main.css

Before `.above-game:after { ... }`, add:

```
.above-game {
  display: none;
}
```

### assets/Game/js/keyboard_input_manager.js

Inside of `gameContainer.addEventListener(this.eventTouchstart, function (event) {`, remove `event.preventDefault()` at the end.

Remove the definition `var gameContainer = document.getElementsByClassName("game-container")[0];`.

Replace all occurrences of `gameContainer` with `document`.

## Contributing

All contributions are welcome! If you wish to contribute, please create an issue first so that your feature, problem or question can be discussed.

## Dependencies

 * [AppRater](https://github.com/delight-im/AppRater) — [delight.im](https://github.com/delight-im) — [Apache License 2.0](https://github.com/delight-im/AppRater/blob/master/LICENSE)
 * [Android-BaseLib](https://github.com/delight-im/Android-BaseLib) — [delight.im](https://github.com/delight-im) — [Apache License 2.0](https://github.com/delight-im/Android-BaseLib/blob/master/LICENSE)
 * [Android-Progress](https://github.com/delight-im/Android-Progress) — [delight.im](https://github.com/delight-im) — [Apache License 2.0](https://github.com/delight-im/Android-Progress/blob/master/LICENSE)
 * [Android-AdvancedWebView](https://github.com/delight-im/Android-AdvancedWebView) — [delight.im](https://github.com/delight-im) — [Apache License 2.0](https://github.com/delight-im/Android-AdvancedWebView/blob/master/LICENSE)
 * [2048](https://github.com/gabrielecirulli/2048) — [Gabriele Cirulli](https://github.com/gabrielecirulli) — [MIT License](https://github.com/gabrielecirulli/2048/blob/master/LICENSE.txt)
 * Android 2.2+

## Disclaimer

This project is neither affiliated with nor endorsed by Gabriele Cirulli.

## License

```
Copyright 2014 www.delight.im <info@delight.im>

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
