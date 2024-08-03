//
// BarcodeAPI.org
// multi.js
//

var barcodes = [];

function init() {

	var root = window.location.href;
	var start = root.indexOf("?");
	if (start >= 0) {

		// parse page arguments as barcodes to render
		var params = root.substring(start + 1);
		var requests = params.split("&");
		for (var request in requests) {

			addFromText(requests[request]);
		}
	}

	document.getElementById("input").focus();
}

function goHome() {
	window.location = "/index.html";
}

function addFromInput() {
	var input = document.getElementById("input");
	var inStr = input.value.split("\n");

	for (var x in inStr) {
		addFromText(inStr[x]);
	}

	input.value = "";
	input.focus();
}

function addFromText(text) {
	if (text.length) {
		var img = generateImage(text);
		document.getElementById("barcodes").appendChild(img);
		barcodes.push(text);
	}
}

function onKeyDown(e) {
	if (e.keyCode === 13) {
		addFromInput();
		e.preventDefault();
		return;
	}
}

function onKeyUp(e) {
	if (e.target.value.endsWith("\n")) {
		addFromInput();
	}
}

function generateImage(request) {

	var url = location.origin + "/api/" + request;

	var img = document.createElement("img");
	img.classList.add("multis-barcode");
	img.src = url;

	return img;
}

function multiShare() {
	var url = "/multi.html?";
	for (var x in barcodes) {
		url += barcodes[x] + "&";
	}

	window.location = url;
}

function printPage() {
	window.print();
}