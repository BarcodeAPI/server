//
// BarcodeAPI.org
// multi.js
//

function init() {

	var bartab = document.getElementById("bartab");

	var root = window.location.href;
	var params = root.substring(root.indexOf("?") + 1);

	var requests = params.split("&");
	for ( var request in requests) {

		var row;
		if ((request % 3) == 0) {
			row = document.createElement("tr");
			bartab.appendChild(row);
		}

		var col = document.createElement("td");
		col.setAttribute("align", "center");
		row.appendChild(col);
		col.appendChild(generateImage(requests[request]));
	}
}

function generateImage(request) {

	var url = location.origin + "/api/" + request;

	var img = document.createElement("img");
	img.classList.add("multis-barcode");
	img.src = url;

	return img;
}

function print() {

	var data = document.getElementById("wrapper").innerHTML;

	setTimeout(function() {

		var w = window.open();
		w.document.write(data);
		w.document.close();
		w.focus();
		w.print();
		w.close();
	}, 250);
}
