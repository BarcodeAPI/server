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
	document.getElementById("limiter-created").innerHTML = data.limiter.created;
	document.getElementById("limiter-expires").innerHTML = data.limiter.expires;
	document.getElementById("limiter-enforce").innerHTML = data.limiter.enforce;
	document.getElementById("limiter-tokenSpend").innerHTML = data.limiter.tokenSpend;
	document.getElementById("limiter-tokenLimit").innerHTML = data.limiter.tokenLimit;
	document.getElementById("limiter-tokenCount").innerHTML = data.limiter.tokenCount;

	document.getElementById("session-key").innerHTML = data.session.key;
	document.getElementById("session-created").innerHTML = data.session.created;
	document.getElementById("session-expires").innerHTML = data.session.expires;
	document.getElementById("session-count").innerHTML = data.session.count;
	document.getElementById("session-requests").innerHTML = JSON.stringify(data.session.requests, null, '\t');
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
