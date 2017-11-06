# BarcodeAPI.org

The barcode server was designed to serve an easy to use API via web protocol for use in mobile applications.

When served behind a simple Apache web server providing a UI the barcode server can be used for a wide range of tasks.


## About

### Server Statistics

The server will keep counters for most basic actions, such as total number of hits, generation requests, and render times for each type of code.

Server statistic are available at the `/stats` endpoint.

### Caching

The server implements a basic cache for rendered images. Provided the image has not expired and evicted from cache, a render request will first attempt a cache lookup before being rendered. Only requests matching certain criteria will be cached.

All cache details are available at the `/cache` endpoint.

### Sessions

A simple session cache will track a users render requests. This can be used to provide a UI that will show a user a list of their most used codes.

A user's session details are available at the `/session` endpoint.


## Third-Party

BarcodeAPI.org is only made possible with the use of third-party software.

### Jetty

[Apache 2.0](https://www.eclipse.org/jetty/licenses.html)<br/>
The BarcodeAPI server was built around the [Jetty](https://www.eclipse.org/jetty/) web server framework.

### Barcode4J

[Apache 2.0](http://barcode4j.sourceforge.net/#Introduction)<br/>
[Barcode4J](http://barcode4j.sourceforge.net/) is an open source barcode generator; it is used for the generation of the following code types:

- Code39
- Code128
- Codabar
- EAN-8
- EAN-13
- UPC-A
- UPC-E

### ZXing

[Apache 2.0](https://github.com/zxing/zxing/blob/master/LICENSE)<br/>
[ZXing](https://github.com/zxing/zxing/) is a barcode processing library that makes QR code generation possible.


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
