/********************************************************************************
* Al Gore Rhythms 
* Playlist Application Project
*
* This is the backend part. Including the web and data structure logic.
*
* @author: Eugene Kolo
* @email: eugene@kolobyte.com
* @version: 0.8
* @since: November 25, 2015

* Features:
*   + Autocomplete song, and list top 4 songs based on popularity
*   + List top 8 most popular playlists
*   + Add up to 1024 playlists
*   + Suggest most popular playlist with input song
*   + Restful API (Spark Java)
*   + Hackable (separated front end, separated data structures)
*   + Always returns JSON
*   + Efficient
*   + Nice frontend 
*
********************************************************************************/

package algore;

import java.util.HashMap;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.StringReader;
import java.util.ArrayList;
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
        System.out.println("[*] Al Gore Rhythms started!");

        /****************************
        * Initialize data structures 
        ****************************/
        mSongIdToTitleMap = new SongIdToTitleMap();
        mSongTitleToSongMap = new SongTitleToSongMap();
        mPlaylistDB = new PlaylistDB();
        mAutocompleteDB = new AutocompleteDB();

        File song_list_txt = new File("./assets/song_list.txt");
        try (BufferedReader reader = new BufferedReader(new FileReader(song_list_txt))) {
            String songLine = reader.readLine();
            while (songLine != null) {
                /* Map song ids -> song titles */
                String[] songLineProps = songLine.split("\t");
                String songId = songLineProps[0];
                String songTitle = songLineProps[1];
                String songAuthor = songLineProps[2];
                mSongIdToTitleMap.putSong(songId, songTitle);

                /* Map song titles -> song objects */
                Song song = new Song(songTitle, songAuthor);
                mSongTitleToSongMap.putSong(songTitle, song);

                /* Add all song titles lower case to the AutocompleteDB */
                /* Store a concated lowercase version and a case sensitive version */
                mAutocompleteDB.putSong(songTitle.toLowerCase() + "##sep##" + songTitle);

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

        /** POST /api/addPlaylist *******************************************************
        *   Gets fileData and parses out the individual playlists and adds them to the database
        *
        *   @note: Updating the playlistDB also updates SongTitleToSongMap   
        *
        *   @req: JSON of <fileName> assosicated with <fileData>
        *       {<fileName>: <fileData>}
        *   @res: 200 if successful
        ********************************************************************************/
        post("/api/addPlaylist", (req, res) -> {
            // TODO(eugenek): How do you handle specifiying popularity when doing 1 entry?
            HashMap<String, String> json = jsonToMap(req.body());
            Integer attemptedAdd = 0;
            Integer actualAdd = 0;

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
                ArrayList<Song> songList = new ArrayList<Song>();
                for (int i = 0; i < songIdList.length; i++) {
                    String songTitle = mSongIdToTitleMap.getSong(songIdList[i]);
                    Song song = mSongTitleToSongMap.getSong(songTitle);
                    songList.add(song);
                }

                /* Add the Playlist to the playListDB*/
                Playlist playlist = new Playlist(popularity, songList);
                playlistLine = reader.readLine();

                /* Server side logging */
                boolean isActuallyAdded = mPlaylistDB.addPlaylist(playlist);
                if (isActuallyAdded) {
                    actualAdd += 1;
                }
                attemptedAdd += 1;
            }

            /* Server side logging */
            System.out.println("[addPlaylist] attempted: " + attemptedAdd + " actual: " + actualAdd + " playlists");
            System.out.println("[addPlaylist] size of playlistDB: " + mPlaylistDB._playlistDB.size());

            res.status(200);
            return "Successfully added playlists";
        });


        /** GET /api/getTop8 ************************************************************
        *   Returns Top 8 playlists based on popularity.
        *
        *   @req: blank
        *   @res: JSON map of top 8 playlists song list sepated by ##sep##
        *       {"1":{"title":"Apple##sep##Orange##sep##Watermelon", "popularity": "92"},
        *        "2":{"title":"Ferrari##sep##Lamboughini##sep##BMW", "popularity": "78"},
        *         ...
        *       }
        ********************************************************************************/
        get("/api/getTop8", (req, res) -> {
            ArrayList<Playlist> top8List = mPlaylistDB.getTop8();

            /* Convert the top8 list to a JSON map */
            HashMap<Integer, HashMap<String, String>> top8Map = new HashMap<Integer, HashMap<String, String>> ();
            for (int i = 0; i < top8List.size(); i++) {
                Playlist playlist = top8List.get(i);

                HashMap<String, String> playlistData = new HashMap<String,String>();
                playlistData.put("title", playlistToTitle(playlist));
                playlistData.put("popularity", String.valueOf(playlist.getPopularity()));

                top8Map.put(i, playlistData);
            }

            /* Server side logging */
            System.out.println("[getTop8] successful returning: ");
            for (int i = 0; i < top8List.size(); i++) {
                System.out.println("[+]\t" + i + ". " + top8Map.get(i).get("title") + "\t" +
                                   top8Map.get(i).get("popularity"));
            }

            return mapToJson(top8Map);
        });


        /** POST /api/getAutocomplete ****************************************************
        *   Gets a string and searches the database for the most popular matches
        *
        *   @note: Case insensitive. 
        *
        *   @req: JSON of "song" matched to partial completion
        *       {"song": "obses"}
        *   @res: JSON with top 5 most popular autocompleted songs
        *       {"0":"Obsesion","1":"Obsesionado","2":"Obsession Confession"}
        *********************************************************************************/
        post("/api/getAutocomplete", (req, res) -> {
            HashMap<String, String> json = jsonToMap(req.body());    
            String lowerCaseSong = json.get("song").toLowerCase();
            ArrayList<String> concatTitles = mAutocompleteDB.getPrefixList(lowerCaseSong);

            /* Extract the case sensitive result */
            ArrayList<String> songTitles = new ArrayList<String>();
            for (String concatTitle : concatTitles) {
                String[] bothTitles = concatTitle.split("##sep##");
                songTitles.add(bothTitles[1]); // The case sensitive title
            }

            /* Change the ArrayList of song titles to a sorted song list of max size 4*/
            ArrayList<Song> songList = new ArrayList<Song>();
            for (String songTitle : songTitles) {
                Song song = mSongTitleToSongMap.getSong(songTitle);

                // Filter songList to be the 4 most popular songs
                if (songList.size() < 4) {
                    songList.add(song);
                } 
                else {
                    Song worstSong = Collections.min(songList);
                    if (worstSong.getPopularity() < song.getPopularity()) {
                        songList.remove(worstSong);
                        songList.add(song);
                    }
                }
            }
            Collections.sort(songList, Collections.reverseOrder());

            /* Converted the sorted song list to a song title map indexed by rank */
            HashMap<Integer, String> songTitlesMap = new HashMap<Integer, String>();
            for (int i = 0; i < songList.size(); i++) {
                songTitlesMap.put(i, songList.get(i).getTitle());
            }

            /* Server side logging */
            System.out.println("[getAutocomplete] successful, looking for \"" + json.get("song") + "\"");
            for (Song song : songList) { // Should be <= 4
                System.out.println("[+]\tfound: " + song.getTitle() + " " 
                    + song.getPopularity());
            }

            return mapToJson(songTitlesMap);
        });


        /** POST /api/suggestPlaylist ***************************************************
        *   Gets a song title and suggests the most popular playlist that has it
        *
        *   @req: JSON of "song" matches to <songTitle>
        *       {"song": "Obsesionado"}
        *   @res: JSON with most popular playlist that has the song
        *       {"mostPopular":"Obsesionado##sep##Me Gusta Todo De Ti##sep##La Promocion##sep##No Puedo Volver
        *            ##sep##El Celoso##sep##El Columpio##sep##La Gran Senora##"}
        ********************************************************************************/
        post("/api/suggestPlaylist", (req, res) -> {
            HashMap<String, String> json = jsonToMap(req.body());    
            String songTitle = json.get("song");

            /* Get best playlist */
            Song song = mSongTitleToSongMap.getSong(songTitle);
            if (song == null || song.getBestPlaylist() == null) {
                res.status(400);
                return "Sorry, song doesn't exist, or no playlist contains it.";
            }
            Playlist bestPlaylist = song.getBestPlaylist();
            String playlistTitle = playlistToTitle(bestPlaylist);

            /* Convert playlist string to a JSON map */
            HashMap<String, String> mostPopular = new HashMap<String, String>();
            mostPopular.put("mostPopular", playlistTitle);

            /* Server side logging */
            System.out.println("[suggestPlaylist] looking for best playlist with: \"" + json.get("song") + "\"");
            System.out.println("[+]\tfound: \"" + playlistTitle + "\"");

            return mapToJson(mostPopular);
        });
        

    }


    /****************************
    /* Helper functions         
    ****************************/
    /**
    * Converts an POJO to a JSON string 
    */
    public static String mapToJson(Object object) {
        Gson gson = new Gson();
        return gson.toJson(object);
    }

    /** 
    * Converts a JSON string to a HashMap.
    */
    public static HashMap<String, String> jsonToMap(String json) {
        HashMap<String,String> map = new Gson().fromJson(json, 
            new TypeToken<HashMap<String, String>>(){}.getType());
        return map;
    }

    /**
    * Converts a ArrayList<Song> to a string 
    */
    public static String songListToString(ArrayList<Song> songList) {
        String playlistSongString = "";
        for (Song song : songList) {
            playlistSongString += song.getTitle() + "##sep##";
        }
        return playlistSongString;
    }

    /**
    * Converts a Playlist to a string
    */
    public static String playlistToTitle(Playlist playlist) {
        return songListToString(playlist.getSongList());
    }

}