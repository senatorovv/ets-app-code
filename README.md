# productorder-API1

@Todo
fix117991
Example Microservice naming convention: productorder API as project, productorder-api as the namespace k1s

Port: 8080

Swagger API (default redirect):
@Todo
http://localhost:8080/camel/dev/api/swagger-ui.html

Required ActiveMQ Queue _"productorderapi-queue"_  

Testing endpoints:

Camel API
http://localhost:8080/camel/dev/api/test/api-doc

Camel Rest Routing only
http://localhost:8080/camel/dev/api/test/hello

Camel Sends test message to ActiveMQ
http://localhost:8080/camel/dev/api/test/send-message
