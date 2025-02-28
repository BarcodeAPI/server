//
// BarcodeAPI.org, 2017-2025
// admin.js // admin.html
//

function zero(x) {
	return ((x < 10) ? ("0" + x) : x);
}

function loadInfo() {
	fetch("/info/").then(response => {
		return response.json();
	}).then(onLoadInfo);
}

function onLoadInfo(info) {

	// display hostname and version
	document.getElementById("appHost").innerHTML = info.hostname;
	document.getElementById("appVersion").innerHTML = info.version;

	// calculate uptime	in HH:MM
	var sinceH = Math.floor((info.uptime / 1000 / 60 / 60)).toFixed(0);
	var sinceM = Math.floor((info.uptime / 1000 / 60 % 60)).toFixed(0);

	// display uptime 
	var uptime = ("+" + zero(sinceH) + ":" + zero(sinceM));
	document.getElementById("appUptime").innerHTML = uptime;

	// Log tracking event
	var setupMillis = ((new Date()) - timeStart);
	trackingEvent("AppEvents", "AppLoad", "Admin", setupMillis);
}
