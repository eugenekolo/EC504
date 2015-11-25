import static spark.Spark.*;

public class AlGore {
    public static void main(String[] args) {
        get("/", (req, res) -> "Hello, World!");
    }
}
