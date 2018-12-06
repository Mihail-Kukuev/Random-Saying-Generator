#!/usr/bin/env python3
from sanic import Sanic
from sanic.response import json

app = Sanic(log_config=None)

links_response = {
  "_links": {
    "self": { "href": "/sayings" },
    "random": { "href": "/sayings/random" },
    "add": { "href": "/sayings/new" }
  }
} 

@app.route("/hello")
async def test(request):
    return json(links_response)

if __name__ == "__main__":
    app.run(debug=False, access_log=False, host="localhost", port=8080, workers=4)
