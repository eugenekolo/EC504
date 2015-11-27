/********************************************************************************
* Al Gore Rhythms 
* Playlist Application Project
*
* This is the backend part. Including the web and data structure logic.
*
* Features:
*   + Autocomplete song, and list top 4 songs based on popularity
*   + List top 8 most popular playlists
*   + Add up to 1024 playlists
*   + Suggest most popular playlist with input song
*   + Restful API
*   + Hackable (separated front end, separated data structures)
*   + Always returns JSON
*   + Efficient
*   + Nice frontend 
*
* @author: Eugene Kolo
* @email: eugene@kolobyte.com
* @version: 0.7
* @since: November 25, 2015
********************************************************************************/

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
import java.util.SortedMap;
import java.util.Collections;

import static spark.Spark.*;
import spark.ModelAndView;
import spark.template.mustache.MustacheTemplateEngine;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class AlGore {

    public static SongIdToTitleMap mSongIdToTitleMap;
    public static SongTitleToSongMap mSongTitleToSongMap;
    public static PlaylistDB mPlaylistDB;
    public static AutocompleteDB mAutocompleteDB;

    public static void main(String[] args) {
        System.out.println("[*] Al Gore Rhythems started!");
        /****************************
        * Initialize data structures 
        ****************************/
        mSongIdToTitleMap = new SongIdToTitleMap();
        mSongTitleToSongMap = new SongTitleToSongMap();
        mPlaylistDB = new PlaylistDB();
        mAutocompleteDB = new AutocompleteDB();

        //TODO(eugenek): Consider song author too?
        File song_list_txt = new File("./assets/song_list.txt");
        try (BufferedReader reader = new BufferedReader(new FileReader(song_list_txt))) {
            String songLine = reader.readLine();
            while (songLine != null) {
                /* Map song ids -> song titles */
                String[] songLineProps = songLine.split("\t");
                mSongIdToTitleMap.putSong(Integer.parseInt(songLineProps[0]), songLineProps[1]);

                /* Map song titles -> song objects */
                Song song = new Song(songLineProps[1]);
                mSongTitleToSongMap.putSong(songLineProps[1], song);

                /* Add all song titles to the AutocompleteDB */
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

        System.out.println("[*] Started backend webserver");


        /****************************
        * Route programming
        ****************************/
        /** POST /api/addPlaylist
        *   Gets fileData and parses out the individual playlists and adds them to the database
        *
        *   @note: Updating the playlistDB also updates SongStringToPopMap   
        *
        *   @req: JSON of <fileName> assosicated with <fileData>
        *       {<fileName>: <fileData>}
        *   @res: 200 if successful
        */
        post("/api/addPlaylist", (req, res) -> {
            String body = req.body();
            HashMap<String, String> json = jsonToMap(body);
            Integer amountAdded = 0;
            /* Don't need the file name. */
            String data;
            HashMap.Entry<String,String> entry = json.entrySet().iterator().next();
            data = entry.getValue();

            /* Scan the playlist data line by line and add each playlist */
            BufferedReader reader = new BufferedReader(new StringReader(data));
            String playlistLine = reader.readLine();
            while (playlistLine != null) {
                /* Extract the song ids and popularity */
                String[] playlistLineSplit = playlistLine.split("\t");
                String[] songIdList = playlistLineSplit[0].split(" ");
                Integer popularity = Integer.parseInt(playlistLineSplit[1]);

                /* Map the input song ids to Song objects*/
                Set<Song> songSet = new HashSet<Song>();
                for (int i = 0; i < songIdList.length; i++) {
                    String songTitle = mSongIdToTitleMap.getSong(Integer.parseInt(songIdList[i]));
                    Song song = mSongTitleToSongMap.getSong(songTitle);
                    songSet.add(song);
                }

                /* Add the playlistNode to the playListDB*/
                PlaylistNode playlist = new PlaylistNode(popularity, songSet);
                mPlaylistDB.addPlaylist(playlist);

                amountAdded += 1;
                playlistLine = reader.readLine();
            }

            System.out.println("[+] addPlaylist successful added " + amountAdded + " playlists");

            res.status(200);
            res.body("Successfully added playlists");
            return res;
        });

        /** GET /api/getTop8
        *   Returns Top 8 playlists based on popularity.
        *
        *   @req: blank
        *   @res: JSON map of top 8 playlists song list sepated by ##
        *       {"1":"Apple##Orange##Watermelon", "2":"Ferrari##Lamboughini##BMW", "3":"Nas##Tupac##Biggie"....}
        */
        get("/api/getTop8", (req, res) -> {
            //TODO(eugenek): Test this is returning top 8 based on popularity
            ArrayList<PlaylistNode> top8List = mPlaylistDB.getTop8List();
            HashMap<Integer, String> top8Map = new HashMap<Integer, String>();

            for (int i = 0; i < top8List.size(); i++) {
                Set<Song> songSet = top8List.get(i).getSongSet();
                String playlistSongString = "";
                for (Song song : songSet) {
                    playlistSongString += song.getTitle() + "##";
                }
                top8Map.put(i, playlistSongString);
            }

            // TODO(eugenek): Song order is not preserved right now because it uses an Unordered Hashmap
            System.out.println("[+] getTop8 successful returning: ");
            for (int i = 0; i < top8List.size(); i++) {
                System.out.println("[+] --------- " + i + ". " + top8Map.get(i));
            }

            return mapToJson(top8Map);
        });

        /** POST /api/getAutocomplete
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

            ArrayList<String> songTitles = mAutocompleteDB.getPrefixList(json.get("song"));

            /* Change the ArrayList of song titles to a sorted song list of max size 4*/
            ArrayList<Song> songList = new ArrayList<Song>();
            for (int i = 0; i < songTitles.size(); i++) {
                Song song = mSongTitleToSongMap.getSong(songTitles.get(i));
                if (songList.size() < 4) {
                    songList.add(song);
                } else {
                    Song minSong = Collections.min(songList);
                    if (minSong.getPopularity() < song.getPopularity()) {
                        songList.remove(minSong);
                        songList.add(song);
                    } else {
                        // Do nothing, song popularity is less than the other 4 already
                    }
                }
            }
            Collections.sort(songList);

            /* Converted the sorted song set to a song title map index by rank */
            HashMap<Integer, String> songTitlesMap = new HashMap<Integer, String>();
            for (int i = 0; i < songList.size(); i++) {
                songTitlesMap.put(i, songList.get(i).getTitle());
            }

            System.out.println("[+] getAutocomplete successful, found \"" + json.get("song") + "\"");
            for (Song song : songList) { // Should be <= 4
                System.out.println("[+] --------------------------- found: " + song.getTitle() + " " 
                    + song.getPopularity());
            }

            return mapToJson(songTitlesMap);
        });

        /** POST /api/suggestPlaylist
        *   Gets a song title and suggests the most popular playlist that has it
        *
        *   @req: JSON of "song" matches to <songTitle>
        *       {"song": <song title to search for>}
        *   @res: JSON with most popular playlist that has the song
        *       {"mostPopular": "1 2 9 3 2 6 20 185\t81"}
        */
        //post("/api/suggestPlaylist", (req, res) -> {
        //    String body = req.body();
        //    HashMap<String, String> json = jsonToMap(body);    
        //    String songTitle = json.get("song");
//
        //    /* Get best playlist */
        //    Song song = mSongTitleToSongMap.get(songTitle);
        //    PlaylistNode playlist = song.getBestPlaylist();
//
        //    /* Convert best playlist to string */
        //    Set<Song> songSet = playlist.getSongSet();
        //    String playlistSongString = songSetToString(songSet)
//
        //    /* Convert playlist string to a JSON map */
        //    HashMap<String, String> mostPopular = new HashMap<String, String>();
        //    mostPopular.put("mostPopular", playlistSongString);
//
        //    return mapToJson(mostPopular);
        //});
        
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

    public static String songSetToString(Set<Song> songSet) {
        String playlistSongString = "";
            for (Song song : songSet) {
                playlistSongString += song.getTitle() + "##";
            }
        return playlistSongString;
    }

} // END Class AlGore