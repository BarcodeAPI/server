//
// BarcodeAPI.org
// multi.js
//

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
	var img = generateImage(text);
	document.getElementById("barcodes").appendChild(img);
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
