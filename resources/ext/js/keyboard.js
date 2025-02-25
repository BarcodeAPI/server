//
// BarcodeAPI.org
// keyboard.js
//

function init() {
	var btns = document.getElementsByClassName("button");
	for (var x in btns) {
		if (btns[x].addEventListener) {
			btns[x].addEventListener('click', addChar);
		}
	}
}

function addChar(event) {

	var val = event.target.value;
	window.opener.addCharacter(val)
}

function _help() {
	window.opener.actionNonprintingHelp();
}

function _close() {
	window.opener.actionCloseKeyboard();
}
