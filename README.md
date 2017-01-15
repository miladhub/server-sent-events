Server Sent Events example
===

Sample app demonstrating [Server Sent Events](https://www.html5rocks.com/en/tutorials/eventsource/basics/)
using Java Servlets.

This app runs on all major browsers -- IE 11+ is covered by this polyfill: <https://github.com/Yaffle/EventSource>.

# Run

`mvn jetty:run`

Watch events at <http://localhost:9000/>

# Async Servlet implementation

<http://localhost:9000/async.html>

# Deploying on Tomcat

If you want to deploy this app on [Tomcat](http://tomcat.apache.org/), build it:

`mvn clean install`

and deploy the WAR:

`cp target/*.war ~/apache-tomcat-8.0.39/webapps/`

The app should be ready at <http://localhost:8080/sse/> and <http://localhost:8080/sse/async.html>.