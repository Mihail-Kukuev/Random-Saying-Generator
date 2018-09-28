package controllers;

import play.mvc.*;

public class RootController extends Controller {

    public Result sayHello() {
        return ok("Hello world!");
    }

}
