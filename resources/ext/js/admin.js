//
// BarcodeAPI.org, 2023
// admin.js // admin.html
//

function fetch(path, callback) {

	var xobj = new XMLHttpRequest();
	xobj.overrideMimeType("application/json");

	xobj.open('GET', path, true);
	xobj.onreadystatechange = function() {
		if (xobj.readyState === 4 && xobj.status === 200) {
			callback(JSON.parse(xobj.responseText));
		}
	};

	xobj.send(null);
}

function zero(x) {
	return ((x < 10) ? ("0" + x) : x);
}

function loadInfo() {
	fetch("/info/", function(info) {

		// display hostname and version
		document.getElementById("appHost").innerHTML = info.hostname;
		document.getElementById("appVersion").innerHTML = info.version;

		// calculate uptime	in HH:MM
		var sinceH = Math.floor((info.uptime / 1000 / 60 / 60)).toFixed(0);
		var sinceM = Math.floor((info.uptime / 1000 / 60 % 60)).toFixed(0);

		// display uptime 
		var uptime = ("+" + zero(sinceH) + ":" + zero(sinceM));
		document.getElementById("appUptime").innerHTML = uptime;
	});
}
