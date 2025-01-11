//
// BarcodeAPI.org
// session.js
//

function init() {

	// Load the type details
	fetch("/session/")
		.then(response => {
			return (response.status == 200) ? response.json() : false;
		}).then(onLoadSession);
}

function onLoadSession(data) {

	document.getElementById("limiter-caller").innerHTML = data.limiter.caller;
	document.getElementById("limiter-created").innerHTML = (new Date(data.limiter.created)).toJSON();
	document.getElementById("limiter-expires").innerHTML = (new Date(data.limiter.expires)).toJSON();
	document.getElementById("limiter-enforce").innerHTML = data.limiter.enforce;
	document.getElementById("limiter-tokenSpend").innerHTML = data.limiter.tokenSpend;
	document.getElementById("limiter-tokenLimit").innerHTML = data.limiter.tokenLimit;
	document.getElementById("limiter-tokenCount").innerHTML = Number(data.limiter.tokenCount).toFixed(2);

	document.getElementById("session-key").innerHTML = data.session.key;
	document.getElementById("session-created").innerHTML = (new Date(data.session.created)).toJSON();
	document.getElementById("session-expires").innerHTML = (new Date(data.session.expires)).toJSON();
	document.getElementById("session-count").innerHTML = data.session.count;

	var addresses = "";
	for (var a in data.session.addresses) {
		var d = data.session.addresses[a];

		addresses += //
			"<tr><td>" + d.ip + "</td><td>" + d.hits + "</td></tr>";
	}
	document.getElementById("session-addresses").innerHTML = addresses;

	for (var r in data.session.requests) {
		var d = data.session.requests[r];

		if (d.text.match(/^\/api\/.*/)) {
			addEntryAPI(d.text.substr(4), d.hits);
			continue;
		}

		addEntryOther(d.text, d.hits);
		continue;
	}

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
