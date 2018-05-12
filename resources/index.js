window.onhashchange = genCode;

function genCode() {

	var url = location.origin + "/api";

	var type = location.hash.substring(1);
	if (type == "") {
		type = "auto";
	}

	var text = document.getElementById("search_text").value;
	if (text == "") {

		url = "";
	} else {

		if (type != "auto") {

			url = url + "/" + type;
		}

		url += "/" + encodeURIComponent(text);
		console.log("Generating: " + url);
	}

	document.getElementById("barcode_output").src = url;
}
