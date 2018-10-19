# Random Saying Generator
Web-service with RESTful API that allows to generate a random saying, like or dislike it and add own saying (adding a saying is currently unavailable).
Application uses Play framework and Cassandra as a database.

## Running

```
# developing mode
./gradlew run
```
or
```
# release mode
./gradlew dist
./build/stage/playBinary/bin/playBinary
```

And then go to http://localhost:9000/sayings to see the running web application.

<br/>Database configuration commands:
```
# drops existing keyspace with all data
./gradlew dropSchema

# creates keyspace and necessary tables (just executes commands from "conf/create.cql" file)
./gradlew createSchema

# fills existing tablies with data from "conf/init_data.txt" file
./gradlew fillTables
```

## API
**GET**   /sayings   HTTP/1.1 
<br/>*controllers.Main endpoint, from it you can generate random saying or add own.*
<br/>RESPONSE
<br/>HTTP/1.1 200 OK
<br/>Content-Type: application/hal+json
<br/>RESPONSE BODY
```json
{
  "_links": {
    "self": { "href": "/sayings" },
    "random": { "href": "/sayings/random" },
    "add": { "href": "/sayings/new" }
  }
}
```


**GET**   /sayings/{id}   HTTP/1.1
<br/>*Gets saying with a given id.*
<br/>RESPONSE
<br/>HTTP/1.1 200 OK
<br/>Content-Type: application/hal+json
<br/>RESPONSE BODY
```json
{
  "saying": {
    "text": "Text of saying",
    "author": "Author",
    "likes": 120,
    "dislikes": 15
  },
  "_links": {
    "self": { "href": "/sayings/{id}" } },
    "rate": { "href": "/sayings/{id}/rate" },
    "random": { "href": "/sayings/random" },
    "add": { "href": "/sayings/new" }
}
```


**GET**   /sayings/random   HTTP/1.1
<br/>*Gets random saying.*
<br/>RESPONSE
<br/>Exactly the same as for request **GET**   /sayings/{id}   HTTP/1.1


<br/>**POST**  /sayings/new    HTTP/1.1
<br/>*Adds new saying to the system.*
<br/>Accept: application/json
<br/>REQUEST BODY
```json
{
  "saying": {
    "text": "Text of saying",
    "author": "Author"
  }
}
```
<br/>RESPONSE
<br/>HTTP/1.1 201 Created
<br/>Location: /sayings/{id}

<br/><br/>If such saying already exists:
<br/>RESPONSE
<br/>HTTP/1.1 409 Conflict
<br/>Location: /sayings/{id}
<br/>Content-Type: application/json
<br/>RESPONSE BODY
```json
{
  "message": "Same or very similar saying already exists."
}
```


<br/>**POST**  /sayings/{id}/rate    HTTP/1.1
<br/>*Rate the saying as liked or disliked.
Value of field "rate" shoud be 1 or -1, otherwise "400 Bad request" will be returned.*
<br/>Accept: application/json
<br/>REQUEST BODY
```json
{
  "rate": 1
}
```
<br/>RESPONSE
<br/>HTTP/1.1 204 No Content


### Supported HTTP Status Codes:
<br/>**200 OK** Successful request.
<br/>**201 Created** Resource posted in request was successfully created.
<br/>**204 No Content** The server has fulfilled the request but does not need to return an entity-body.
<br/>**400 Bad Request** Wrong URI or JSON representation of data.
<br/>**404 Not found** The requested resource could not be found.
<br/>**409 Conflict** Same or very similar resource already exists.
<br/>**500 Internal Server Error** Unexpected condition was encountered on server and request can't be handled.

