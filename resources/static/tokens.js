function displayTokenCount() {
	fetch('/session/')
		.then(response => {

			return response.headers.get("x-ratelimit-tokens");
		})
		.then(data => {

			document.getElementById("token_count").innerHTML = data;
		});
}
