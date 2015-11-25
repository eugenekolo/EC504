import spark.ModelAndView;
import spark.template.mustache.MustacheTemplateEngine;
import static spark.Spark.*;
import java.util.Map;
import java.util.HashMap;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AlGore {

	public static MustacheTemplateEngine mustache = new MustacheTemplateEngine("../../../frontend");

    public static void main(String[] args) {
    	/* Spark Configuration */
    	setPort(5000); // Run on port 5000
    	setIpAddress("127.0.0.1"); // Run on localhost
        externalStaticFileLocation("../frontend");

        System.out.println(AlGore.class.getResource(AlGore.class.getSimpleName() + ".class"));

        post("/api/addPlaylist", (req, res) -> {
             return "Hello World";
        });


    }

} // END Class AlGore
