function loadLicenses() {

	var url = "/static/license.txt";
	loadFile(url, onloadLicenses);
}

function onloadLicenses(data) {

	document.getElementById("licenses").innerHTML = data;
}

function loadFile(path, callback) {

	var xobj = new XMLHttpRequest();
	xobj.overrideMimeType("application/json");
	xobj.open('GET', path, true);
	xobj.onreadystatechange = function() {
		if (xobj.readyState == 4 && xobj.status == "200") {
			callback(xobj.responseText);
		}
	};
	xobj.send(null);
}
