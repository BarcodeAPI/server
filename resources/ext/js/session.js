//
// BarcodeAPI.org, 2017-2025
// session.js // session.html
//

function init() {

	// Load the type details
	fetch("/session/")
		.then(response => {
			return (response.status == 200) ? response.json() : false;
		}).then(onLoadSession);
	fetch("/limiter/")
		.then(response => {
			return (response.status == 200) ? response.json() : false;
		}).then(onLoadLimiter);

	// Log tracking event
	var setupMillis = ((new Date()) - timeStart);
	trackingEvent("AppEvents", "AppLoad", "Session", setupMillis);
}

function onLoadSession(data) {

	document.getElementById("session-key").innerHTML = data.key;
	document.getElementById("session-created").innerHTML = (new Date(data.time.created)).toJSON();
	document.getElementById("session-expires").innerHTML = (new Date(data.time.expires)).toJSON();
	document.getElementById("session-count").innerHTML = data.count;

	var addresses = "";
	for (var a in data.addresses) {
		var d = data.addresses[a];

		addresses += //
			"<tr><td>" + d.ip + "</td><td>" + d.hits + "</td></tr>";
	}
	document.getElementById("session-addresses").innerHTML = addresses;

	for (var r in data.requests) {
		var d = data.requests[r];

		if (d.text.match(/^\/api\/.*/)) {
			addEntryAPI(d.text.substr(4), d.hits);
			continue;
		}

		addEntryOther(d.text, d.hits);
		continue;
	}
}

function onLoadLimiter(data) {
	document.getElementById("limiter-caller").innerHTML = data.caller;
	document.getElementById("limiter-created").innerHTML = (new Date(data.time.created)).toJSON();
	document.getElementById("limiter-expires").innerHTML = (new Date(data.time.expires)).toJSON();
	document.getElementById("limiter-enforce").innerHTML = (data.enforce ? "Yes" : "No");
	document.getElementById("limiter-reputation").innerHTML = Number(data.reputation).toFixed(2);
	document.getElementById("limiter-tokenSpend").innerHTML = data.tokens.spend;
	document.getElementById("limiter-tokenLimit").innerHTML = data.tokens.limit;
	document.getElementById("limiter-tokenCount").innerHTML = Number(data.tokens.count).toFixed(2);
}

function makeEntryRow(text, hits) {

	return ("<tr><td>" + text + "</td><td>" + hits + "</td></tr>");
}

function addEntryAPI(text, hits) {

	document.getElementById("session-requests-api").innerHTML += makeEntryRow(text, hits);
}

function addEntryOther(text, hits) {

	document.getElementById("session-requests-other").innerHTML += makeEntryRow(text, hits);
}

function sessionDelete() {
	if (!confirm("Forget this session?")) {
		return;
	}

	console.log("Requesting session to be deleted.");
	fetch('/session/', {
		method: 'DELETE'
	}).then(response => {

		return response.ok;
	}).then(okay => {
		if (!okay) {
			alert("Failed deleting session!");
			return;
		}

		alert("Session deleted.");
		window.location.reload();
	});
}

function limiterReset() {
	if (!confirm("Request limiter balance to be reset?")) {
		return;
	}

	console.log("Requesting limiter balance to be reset.");
	fetch('/limiter/', {
		method: 'DELETE'
	}).then(response => {

		return response.ok;
	}).then(okay => {
		if (!okay) {
			alert("Could not reset limiter balance!");
			return;
		}

		alert("Limiter balancer reset.");
		window.location.reload();
	});
}
