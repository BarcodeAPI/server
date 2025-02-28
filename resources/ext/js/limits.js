//
// BarcodeAPI.org, 2017-2025
// limits.js // limits.html
//

window.addEventListener("load", function() {

	fetch('/limiter/')
		.then(response => {
			return response.json();
		})
		.then(data => {
			if (!data.enforce) {
				document.getElementsByClassName("notice-enforced")[0].style.display = 'block';
			}

			document.getElementById("token_count").innerHTML = //
				((data.tokenCount == -1) ? "Unlimited" : data.tokenCount);

			document.getElementById("token_limit").innerHTML = //
				((data.tokenLimit == -1) ? "Unlimited" : data.tokenLimit);

			// Log tracking event
			var setupMillis = ((new Date()) - timeStart);
			trackingEvent("AppEvents", "AppLoad", "Limits", setupMillis);
		});
});
