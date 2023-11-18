//
// BarcodeAPI.org
// multi.js
//

var barcodes = [];

function init() {

	var root = window.location.href;
	var start = root.indexOf("?");
	if (start <= 0) {
		return;
	}

	var params = root.substring(start + 1);
	var requests = params.split("&");
	for (var request in requests) {

		addFromText(requests[request]);
	}
}

function addFromInput() {
	var input = document.getElementById("input");
	addFromText(input.value);
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

function onTextChanged(event) {
	if (event.keyCode === 13) {
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
	for ( var x in barcodes) {
		url += barcodes[x] + "&";
	}
	
	window.location = url;
}
