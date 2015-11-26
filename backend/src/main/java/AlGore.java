package algore;

import spark.ModelAndView;
import spark.template.mustache.MustacheTemplateEngine;
import static spark.Spark.*;
import java.util.HashMap;
import com.google.gson.Gson;

public class AlGore {

    public static SongPopMap mSongPopMap;
    //public PlaylistDB mPlaylistDB;
    public static AutocompleteDB mAutocompleteDB;

    public static void main(String[] args) {
        /****************************
        * Initalize
        ****************************/
        mSongPopMap = new SongPopMap();
        //mPlayListDB = new PlayListDB();
        mAutocompleteDB = new AutocompleteDB();


        /****************************
        * Spark Configuration
        ****************************/
        setPort(5000); // Run on port 5000
        setIpAddress("127.0.0.1"); // Run on localhost
        externalStaticFileLocation("../frontend"); // Serve HTML from the frontend directory
        // TODO(eugenek): Look into setSecure to keep the API channel private.


        /****************************
        * Route programming
        ****************************/
        /** POST /api/addPlaylist
        *   Gets fileData and parses out the individual playlists and adds them to the database
        *
        *   @req: JSON of <fileName> assosicated with <fileData>
        *       {<fileName>: <fileData>}
        *   @res: 200 if successful
        */
        post("/api/addPlaylist", (req, res) -> {
            String body = req.body();

            // TODO(eugenek): For bringup right now
            res.status(200);
            res.body("Successfully added playlists");
            return toJson(res);
        });

        /** GET /api/getTop8
        *   Returns Top 8 playlists based on popularity.
        *
        *   @req: blank
        *   @res: JSON map of top 8 song title
        *       {"1":"Foo", "2":"Bar", "3":"Baz"....}
        */
        get("/api/getTop8", (req, res) -> {
            String body = req.body();

            HashMap top8 = new HashMap<String, String>();
            // TODO(eugenek): For bringup right now
            top8.put("1", "TestSong_1");
            top8.put("2", "TestSong_2");
            top8.put("3", "TestSong_3");
            top8.put("4", "TestSong_4");
            top8.put("5", "TestSong_5");
            top8.put("6", "TestSong_6");
            top8.put("7", "TestSong_7");
            top8.put("8", "TestSong_8");

            return toJson(top8);
        });

        /** GET /api/getAutocomplete
        *   Gets a string and searches the database for the most popular matches
        *
        *   @req: JSON of "song" matched to partial completion
        *       {"song": <string to search for>}
        *   @res: JSON with top 5 most popular autocompleted songs
        *       {"1": "Hello Baby", "2": "Hellozzz", ...}
        */
        get("/api/getAutocomplete", (req, res) -> {
            String body = req.body();

            return "[GET] getAutocomplete";
        });

        /** GET /api/suggestPlaylist
        *   Gets a song title and suggests the most popular playlist that has it
        *
        *   @req: JSON of "song" matches to <songTitle>
        *       {"song": <song title to search for>}
        *   @res: JSON with most popular playlist that has the song
        *       {"mostPopular": "1 2 9 3 2 6 20 185\t81"}
        */
        get("/api/suggestPlaylist", (req, res) -> {
            String body = req.body();

            return "[GET] suggestPlayList";
        });

    }

    /****************************
    /* Helper functions         
    ****************************/
    /**
    * Convert a POJO to a JSON string
    */
    public static String toJson(Object object) {
        Gson gson = new Gson();
        return gson.toJson(object);
    }

} // END Class AlGore