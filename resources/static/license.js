function loadLicense() {

	loadFile("/static/license.txt", function(data) {
		document.getElementById("license").innerHTML = data;
	});

	loadFile("/static/thanks.txt", function(data) {
		document.getElementById("thanks").innerHTML = data;
	});
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
