# BarcodeAPI.org

The BarcodeAPI.org web server was designed to provide an easy to use barcode generation API via the HTTP protocol for use in external / mobile applications where generation libraries might not exist or resources limited; the WebUI is also designed to be responsive such that users may generate barcodes to be scanned directly from their web browser or download the barcodes after testing them out.

## Web Server

The server comes with a minimal set of static HTML and Javascript files that allow users to generate barcodes in their web browser with ease. The UI allows users to quickly download, print, or copy the images for use in other applications.

### API Server

The server will generate a barcode for any content passed to the `/api` endpoint; this can be done using a web browser, fetched through a user script, or even simply with cURL.

```
curl https://barcodeapi.org/api/A_Barcode > gen.png
```

#### Automatic Code Type Detection

When calling the API endpoint without specifying an eplitit code type the server will make its best judgement as to which code type will be best suited for the supplied data.

```
curl https://barcodeapi.org/api/abc123
curl https://barcodeapi.org/api/auto/abc123
```

#### Defined Code Type

A list of all supported barcodes types is available by calling the `/types/` endpoint; this will be a JSON Array containing details for each type including a regex format to prevalidate the date before a request.

Specific barcode types may be requested by adding the type string in the request URL:

```
curl https://barcodeapi.org/api/128/abc123
curl https://barcodeapi.org/api/qr/abc123
```

#### Response Headers

The server will add several headers related to the barcode including the type and encoded contents.

```
$ curl --head https://barcodeapi.org/api/auto/abc123

X-Barcode-Type: Code128
X-Barcode-Content: abc123
Content-Type: image/png;charset=utf-8
Content-Disposition: filename=abc123.png
```

#### Control Characters

Barcodes will frequently contain control characters for various reasons; as they can be difficult to enter in a text field, the API server has implemented a special mechanism for allowing users to easily generate barcodes containing these characters. Using a supported barcode, the prefix `$$` will shift any character value by -64. See the below table for examples.

```
@ -> NUL
A -> SOH
B -> STX
...
```

Refer to [ascii.cl](https://ascii.cl/) for a complete table.

### Server Statistics

The server will keep counters for all handlers and caches, these statistics are available at the `/stats/` endpoint.

### Caching

The server implements a basic cache for rendered images, details about the current cache state can be found at the `/cache/` endpoint.

Provided the cached object has not expired and been evicted, a request for a previously rendered barcode will will be rapidly served from memory insted of being rendered; the cache will only render requests matching a certain criteria.

### Sessions

A simple session cache will track all user actions; this can be used to provide the user with a list of their most used barcodes; this information is available at the `/session/` endpoint.

### Multi-Barcode Generation

Some users will want to generate a large number of barcodes with one request - a basic JavaScript utility is provided at `/multi.html` which will generate as many images as requested then prepare the file to be printed.

```
https://barcodeapi.org/multi.html?Barcode1&Barcode2&dm/A%20Data%20Matrix&qr/And%20QR/Automatic
```

### Add Admin

```
java -cp server.jar org.barcodeapi.core.utils.AuthUtils username pa@ssw0rd >> config/authlist.conf
```

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
Copyright 2020 BarcodeAPI.org

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

