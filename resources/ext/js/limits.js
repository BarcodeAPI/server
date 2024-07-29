function displayTokenCount() {
	fetch('/types/')
		.then(response => {
			return response.headers.get("x-ratelimit-tokens");
		})
		.then(data => {
			var count = (data == -1) ? "Unlimited" : data;
			document.getElementById("token_count").innerHTML = count;
		});
}
