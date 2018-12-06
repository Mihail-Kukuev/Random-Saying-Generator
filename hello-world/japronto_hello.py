#!/usr/bin/env python3
from japronto import Application

links_response = {
  "_links": {
    "self": { "href": "/sayings" },
    "random": { "href": "/sayings/random" },
    "add": { "href": "/sayings/new" }
  }
} 

async def hello(request):
    return request.Response(json=links_response)


app = Application()
app.router.add_route('/hello', hello)
app.run(debug=False)
