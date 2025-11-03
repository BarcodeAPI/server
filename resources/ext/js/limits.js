//
// BarcodeAPI.org, 2017-2025
// limits.js // limits.html
//

window.addEventListener("load", init);

function init() {

	// Load limiter info
	fetch('/limiter/')
		.then(response => {
			return response.json();
		})
		.then(data => {
			if (!data.enforce) {
				document.getElementsByClassName("notice-enforced")[0].style.display = 'block';
				document.getElementsByClassName("notice-abusers")[0].style.display = 'none';
			}

			document.getElementById("token_count").innerHTML = //
				((data.tokenCount == -1) ? "Unlimited" : data.tokenCount.toFixed(2));

			document.getElementById("token_limit").innerHTML = //
				((data.tokenLimit == -1) ? "Unlimited" : data.tokenLimit);

			// Log tracking event
			var setupMillis = ((new Date()) - timeStart);
			trackingEvent("AppEvents", "AppLoad", "Limits", setupMillis);
		});

	// Load plans info
	fetch('/plans/')
		.then(response => {
			return response.json();
		})
		.then(data => {

			// Add free plan details
			document.querySelector(".free-details").innerHTML = data.free.description;
			document.querySelector(".free-support").innerHTML = data.free.support;

			// Check if has paid plans
			if (data.paid.length > 0) {

				// Show free lmit in info message
				document.querySelector(".free-limit").innerHTML = data.free.limit.toLocaleString();

				// Show information about subscriber plans
				document.querySelector(".plan-overview").style.display = "block";

				// Loop and add each paid plan
				for (var x in data.paid) {
					addPlan(data.paid[x]);
				}
			}

			// Delete the plan template
			delTemplate();
		});
}

function addPlan(plan) {

	var info = document.getElementById("plan-template").cloneNode(true);

	info.setAttribute("id", "plan-X");

	info.querySelector(".plan-name").innerHTML = plan.name;

	var planPrices = document.createElement("span");

	for (option in plan.price) {

		if (option > 0) {
			var spacer = document.createElement("span");
			spacer.innerHTML = " || ";
			planPrices.appendChild(spacer);
		}

		var price = plan.price[option];
		var priceInfo = document.createElement("a");
		priceInfo.innerHTML = price.name;

		if (price.link) {
			priceInfo.href = price.link;
		}

		planPrices.appendChild(priceInfo);
	}

	info.querySelector(".plan-prices").appendChild(planPrices);

	info.querySelector(".plan-details").innerHTML = plan.description;
	info.querySelector(".plan-support").innerHTML = plan.support;

	document.getElementById("user-plans").append(info);
}

function delTemplate() {
	document.getElementById("user-plans")//
		.removeChild(document.getElementById("plan-template"));
}
