/*
* TODO(eugenek): Fill out headers
*
*
*
*/

package algore;

import java.util.HashMap;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

import static spark.Spark.*;
import spark.ModelAndView;
import spark.template.mustache.MustacheTemplateEngine;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class AlGore {

    public static SongIdToStringMap mSongIdToStringMap;
    public static SongToPopMap mSongToPopMap;
    public static PlaylistDB mPlaylistDB;
    public static AutocompleteDB mAutocompleteDB;

    public static void main(String[] args) {
        System.out.println("[*] Al Gore Rhythems started!");
        /****************************
        * Initialize data structures 
        ****************************/
        mSongIdToStringMap = new SongIdToStringMap();
        mSongToPopMap = new SongToPopMap();
        mPlaylistDB = new PlaylistDB();
        mAutocompleteDB = new AutocompleteDB();

        /* Match song id's with song strings */
        /* Add all song strings to the AutocompleteDB */
        //TODO(eugenek): Consider song author too?
        File song_list_txt = new File("./assets/song_list.txt");
        try (BufferedReader reader = new BufferedReader(new FileReader(song_list_txt))) {
            String songLine = reader.readLine();
            while (songLine != null) {
                String[] songLineProps = songLine.split("\t");
                mSongIdToStringMap.putSong(Integer.parseInt(songLineProps[0]), songLineProps[1]);
                mAutocompleteDB.putSong(songLineProps[1]);
                songLine = reader.readLine();
            }
        } catch(Exception e) {
            System.out.println("[-] Could not parse song_list.txt");
            return;
        }

        System.out.println("[*] Initialized data structures");


        /****************************
        * Spark Configuration
        ****************************/
        setPort(5000); // Run on port 5000
        setIpAddress("127.0.0.1"); // Run on localhost
        externalStaticFileLocation("../frontend"); // Serve HTML from the frontend directory
        // TODO(eugenek): Look into setSecure to keep the API channel private.

        System.out.println("[*] Started backend webserver");


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
            HashMap<String, String> json = jsonToMap(body);

            /* Don't need the file name. */
            String data;
            HashMap.Entry<String,String> entry = json.entrySet().iterator().next();
            data = entry.getValue();

            BufferedReader reader = new BufferedReader(new StringReader(data));

            String playlistLine = reader.readLine();
            while (playlistLine != null) {
                String[] playListLineSplit = playlistLine.split("\t");
                String[] songList = playListLineSplit[0].split(" ");
                Integer popularity = Integer.parseInt(playListLineSplit[1]);

                // TODO(eugenek): Might be more efficient to change SongSet to a Set<String> instead
                Set<Integer> songSet = new HashSet<Integer>();
                for (int i = 0; i < songList.length; i++) {
                    songSet.add(Integer.parseInt(songList[i]));
                }

                PlaylistNode playlist = new PlaylistNode(popularity, songSet);
                mPlaylistDB.addPlaylist(playlist);

                playlistLine = reader.readLine();
            }

            //TODO(eugenek): Make this reutrn 200;
            return "good";
        });

        /** GET /api/getTop8
        *   Returns Top 8 playlists based on popularity.
        *
        *   @req: blank
        *   @res: JSON map of top 8 song title
        *       {"1":"Foo", "2":"Bar", "3":"Baz"....}
        */
        get("/api/getTop8", (req, res) -> {
            ArrayList<PlaylistNode> top8List = mPlaylistDB.getTop8List();
            HashMap<Integer, String> top8Map = new HashMap<Integer, String>();

            for (int i = 0; i < top8List.size(); i++) {
                Set<Integer> songSet = top8List.get(i).getSongSet();
                String playlistSongString = "";
                for (Integer songId : songSet) {
                    playlistSongString += mSongIdToStringMap.getSong(songId) + "||";
                }
                top8Map.put(i, playlistSongString);
            }

            // TODO(eugenek): Song order is not preserved right now because it uses an Unordered Hashmap
            return mapToJson(top8Map);
        });

        /** GET /api/getAutocomplete
        *   Gets a string and searches the database for the most popular matches
        *
        *   @req: JSON of "song" matched to partial completion
        *       {"song": <string to search for>}
        *   @res: JSON with top 5 most popular autocompleted songs
        *       {"1": "Hello Baby", "2": "Hellozzz", ...}
        */
        post("/api/getAutocomplete", (req, res) -> {
            //TODO(eugenek): Limit return to top 5 most popular
            String body = req.body();
            HashMap<String, String> json = jsonToMap(body);    

            ArrayList<String> songs = mAutocompleteDB.getPrefixList(json.get("song"));
            HashMap<Integer, String> autoCompleteSongs = new HashMap<Integer, String>();
            for (int i = 0; i < songs.size(); i++) {
                String song = songs.get(i);
                autoCompleteSongs.put(i, song);
            }
        
            return mapToJson(autoCompleteSongs);
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
    public static String mapToJson(Object object) {
        Gson gson = new Gson();
        return gson.toJson(object);
    }

    public static HashMap<String, String> jsonToMap(String json) {
        HashMap<String,String> map = new Gson().fromJson(json, 
            new TypeToken<HashMap<String, String>>(){}.getType());
        return map;
    }

} // END Class AlGore