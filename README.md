## Take-home test for candidates for Android SDK engineer position

### Background
Instana is an APM (Application Performance Monitoring) platform. An important aspect of APM tools is data collection. We collect all kinds of data such as metrics, traces and crash reports from the applications that we monitor. To do so, we run a small body of code inside of those applications. This code is called instrumentation code. 

### The task at hand
It is up to you to start writing some of this instrumentation code to instrument Android apps. The instrumentation code will be in the form of a library. You may choose yourself whether to use Java or Kotlin for this exercise. Your instrumentation code will be capturing information about http calls that an Android app makes. To limit the scope of this exercise we are only interested in http calls made using the [OkHttp](https://square.github.io/okhttp/) library. The goal is for the instrumentation code to have as little surface area as possible for the app developer. Ideally the developer would just have to include a gradle dependency and maybe a single line of initialization code. A small surface area is key for adoption by app developers that already have a large codebase in place. We don't want them to make significant changes to that codebase if they decide to monitor their app with Instana.

The instrumentation code communicates with the Instana backend over http. The Instana backend exposes an endpoint `/api` to which `calls` can be posted in json format. To keep this exercise simple calls are sent one at a time. A call is a simple json payload with some information related to the http request that is being made by the app.

~~~~
// example call:

{
  "timestamp": 1575428068897,
  "method": "GET",
  "host": "www.accuweather.com",
  "path": "/en/de/solingen/42651/weather-forecast/170363"
}
~~~~


### This repo
This repo contains a very simple app with just a single button. When you press that button an http request is made to google.com/search?instana. This project also contains two instrumentation tests. One of those tests is  [`mustReportAllHttpCallsToInstanaBackend`](https://github.com/instana/android-take-home-test/blob/master/app/src/androidTest/java/com/example/instanatracerdemo/HttpInstrumentationEndToEndTest.java#L66-L100) and this is the test you should fix. It tests if when clicking that button a `call` is being reported to the Instana backend. The test is currently failing as the app is not instrumented. The other instrumentation test is called [`backendStubTest`](https://github.com/instana/android-take-home-test/blob/master/app/src/androidTest/java/com/example/instanatracerdemo/HttpInstrumentationEndToEndTest.java#L102-L142) and serves as an example as to what data the Instana backend API expects. It also serves as a sanity check to demonstrate that the backend stub is actually working.
