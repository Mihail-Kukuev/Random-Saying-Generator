# Random Saying Generator
Web-service with RESTful API that allows to generate a random saying, like or dislike it and add own saying.

## API
**GET**   /sayings   HTTP/1.1 
<br/>*Main endpoint, from it you can generate random saying or add own.*
<br/>RESPONSE
<br/>Content-Type: application/hal+json
<br/>RESPONSE BODY
```json
{
  "_links": {
    "self": { "href": "/sayings" },
    "random": "/sayings/{id}",
    "add": "/sayings/new"
  }
}
```

**GET**   /sayings/{id}   HTTP/1.1
<br/>*Gets saying with a given id.*
<br/>RESPONSE
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
    "self": { "href": "/sayings/{id}" },
    "like": "/sayings/{id}/like",
    "dislike": "/sayings/{id}/dislike",
    "random": "/sayings/{another_id}",
    "add": "/sayings/new"
  }
}
```

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
<br/>HTTP/1.1 200 OK


<br/>**PUT**  /sayings/{id}/like    HTTP/1.1
<br/>*Increments counter of likes for saying with given id.*
<br/>RESPONSE
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
    "self": { "href": "/sayings/{id}/like" },
    "random": "/sayings/{another_id}",
    "add": "/sayings/new"
  }
}
```

<br/>**PUT**  /sayings/{id}/dislike    HTTP/1.1
<br/>*Increments counter of dislikes for saying with given id.*
<br/>RESPONSE
<br/>Similar to **POST**  /sayings/{id}/like  

### Status Codes
There are 3 HTTP status codes that you need to check for: 200, 404, and 500.
<br/>A successful request returns a 200 response and no error_code.
<br/>When the requested resource could not be found 404 response will be returned.
<br/>500 response will be returned if an unexpected condition was encountered on server and request can't be handled.

