//
// BarcodeAPI.org, 2017-2025
// session.js // session.html
//

function init() {

	// Load the type details
	fetch("/session/?history=true")
		.then(response => {
			return (response.status == 200) ? response.json() : false;
		}).then(onLoadSession);

	// Log tracking event
	var setupMillis = ((new Date()) - timeStart);
	trackingEvent("AppEvents", "AppLoad", "Session", setupMillis);
}

function onLoadSession(data) {

	// Load session data
	var _session = data.session;
	document.getElementById("session-key").innerHTML = _session.key;
	document.getElementById("session-created").innerHTML = (new Date(_session.time.created)).toJSON();
	document.getElementById("session-expires").innerHTML = (new Date(_session.time.expires)).toJSON();
	document.getElementById("session-count").innerHTML = _session.count;

	// Display history addresses
	var addresses = "";
	for (var a in _session.history.addresses) {
		var d = _session.history.addresses[a];

		addresses += //
			"<tr><td>" + d.ip + "</td><td>" + d.hits + "</td></tr>";
	}
	document.getElementById("session-addresses").innerHTML = addresses;

	// Display history requests
	for (var r in _session.history.requests) {
		var d = _session.history.requests[r];

		if (d.text.match(/^\/api\/.*/)) {
			addEntryAPI(d.text.substr(4), d.hits);
			continue;
		}

		addEntryOther(d.text, d.hits);
		continue;
	}

	// Load limiter data
	var _limiter = data.limiter;
	document.getElementById("limiter-caller").innerHTML = _limiter.caller;
	document.getElementById("limiter-created").innerHTML = (new Date(_limiter.time.created)).toJSON();
	document.getElementById("limiter-expires").innerHTML = (new Date(_limiter.time.expires)).toJSON();
	document.getElementById("limiter-count").innerHTML = _limiter.requests;
	document.getElementById("limiter-enforce").innerHTML = (_limiter.tokens.enforce ? "Yes" : "No");
	document.getElementById("limiter-reputation").innerHTML = Number(_limiter.reputation).toFixed(2);
	document.getElementById("limiter-tokenSpend").innerHTML = _limiter.tokens.spend;
	document.getElementById("limiter-tokenLimit").innerHTML = _limiter.tokens.limit;
	document.getElementById("limiter-tokenCount").innerHTML = Number(_limiter.tokens.count).toFixed(2);
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
