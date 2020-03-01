# BarcodeAPI.org

The BarcodeAPI.org web server was designed to provide an easy to use barcode generation API via the HTTP protocol for use in external / mobile applications where generation libraries might not exist; the WebUI is also designed to be responsive such that users may generate barcodes to be scanned directly in their web browser, or download the barcodes after testing them out.

## About

### Web Server

The server comes with a set of static HTML and Javascript files that allow users to navigate to a simple UI and generate barcodes in their web browser with ease.

### API Server

The server will generate a barcode for any content passed to the `/api` endpoint; this can be done using a web browser, fetched through a user script, or even simply with cURL.

```
curl https://barcodeapi.org/api/A_Barcode > gen.png
```

#### Automatic Code Type Detection

When simply calling the api endpoint without specifying an eplitit code type, the server will make its best judgement as to which code type will be best suited for the supplied data.

```
curl https://barcodeapi.org/api/abc123
curl https://barcodeapi.org/api/auto/abc123
```

#### Defined Code Type

A list of all supported barcodes types is available by calling the `/types/` endpoint; this will be a JSON Array containing details for each type.

A specific barcode type may be requested by adding the type string in the request URL:

```
# Code 128 (128, code128)
curl https://barcodeapi.org/api/128/Code-128

# QR Code (qr, qrcode)
curl https://barcodeapi.org/api/qr/QR_Code
```

#### Response Headers

```
$ curl --head https://barcodeapi.org/api/auto/abc123

X-Barcode-Type: Code128
X-Barcode-Content: abc123
Content-Type: image/png;charset=utf-8
Content-Disposition: filename=abc123.png
```

### Server Statistics

The server will keep counters for most basic actions, such as total number of hits, generation requests, and render times for each type of code.

Server statistics are available at the `/stats/` endpoint.

### Caching

The server implements a basic cache for rendered images. Provided the image has not expired and evicted from cache, a render request will first attempt a cache lookup before being rendered. Only requests matching certain criteria will be cached.

All cache details are available at the `/cache/` endpoint.

### Sessions

A simple session cache will track a users render requests. This can be used to provide a UI that will show a user a list of their most used codes.

A user's session details are available at the `/session/` endpoint.

## Third-Party

BarcodeAPI.org is only made possible with the use of third-party software.

**Jetty, [Apache 2.0](https://www.eclipse.org/jetty/licenses.html)**

* The BarcodeAPI server was built around the [Jetty](https://www.eclipse.org/jetty/) web server framework.

**Barcode4J, [Apache 2.0](http://barcode4j.sourceforge.net/#Introduction)**

* [Barcode4J](http://barcode4j.sourceforge.net/) is an open source barcode generator; it is used for the generation of the following code types:

**ZXing, [Apache 2.0](https://github.com/zxing/zxing/blob/master/LICENSE)**

* [ZXing](https://github.com/zxing/zxing/) is a barcode processing library that makes QR code generation possible.

## License

```text
Copyright 2017 BarcodeAPI.org

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

