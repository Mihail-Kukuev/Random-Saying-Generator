# Random Saying Generator
Web-service with RESTful API that allows to generate a random saying, like or dislike it and add own saying.

## API
**GET**   /sayings   HTTP/1.1 
<br/>*Main endpoint, from it you can generate random saying or add own.*
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
    "dislikes": 15,
  },
  "_links": {
    "self": { "href": "/sayings/{id}" } },
    "vote": { "href": "/sayings/{id}/vote" },
    "random": { "href": "/sayings/random" },
    "add": { "href": "/sayings/new" }
  }
}
```


**GET**   /sayings/random   HTTP/1.1
<br/>*Gets random saying.*
<br/>RESPONSE
<br/>Same as for **GET**   /sayings/{id}   HTTP/1.1


<br/>**POST**  /sayings/new    HTTP/1.1
<br/>*Add new saying to system.*
<br/>Accept: application/json
<br/>REQUEST BODY
```json
{
  "saying": {
    "text": "Text of saying",
    "author": "Author",
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


<br/>**POST**  /sayings/{id}/vote    HTTP/1.1
<br/>*Like or dislike saying with given id.
Value of field "vote" shoud be 1 or -1, otherwise "400 Bad request" will be returned.*
<br/>Accept: application/json
<br/>REQUEST BODY
```json
{
  "vote": 1
}
```
<br/>RESPONSE
<br/>HTTP/1.1 204 No Content


### Supported HTTP Status Codes:
<br/>**200 OK** Successful request.
<br/>**200 Created** Resourse posted in request was successfully created.
<br/>**400 Bad Request** Wrong URI or JSON representation of data.
<br/>**404 Not found** The requested resource could not be found.
<br/>**409 Conflict** Same or very similar resourse already exists.
<br/>**500 Internal Server Error** Unexpected condition was encountered on server and request can't be handled.

