// Wait till the browser is ready to render the game (avoids glitches)
var gameManager;
window.requestAnimationFrame(function () {
  gameManager = new GameManager(4, KeyboardInputManager, HTMLActuator, LocalStorageManager);
});
