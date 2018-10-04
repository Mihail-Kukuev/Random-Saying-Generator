package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import play.libs.Json;
import play.mvc.*;


public class RootController extends Controller {

    private JsonNode defaultResponse = Json.parse("{\n" +
            "  \"_links\": {\n" +
            "    \"self\": { \"href\": \"/sayings\" },\n" +
            "    \"random\": { \"href\": \"/sayings/random\" },\n" +
            "    \"add\": { \"href\": \"/sayings/new\" }\n" +
            "  }\n" +
            "}");

    public Result links() {
        return ok(defaultResponse);
    }
}
