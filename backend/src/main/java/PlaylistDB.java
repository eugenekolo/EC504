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
* @version: 0.8
* @since: November 25, 2015
********************************************************************************/

package algore;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.PriorityQueue; // This is default implemented as a MinPriorityQueue
import java.util.HashSet;
import java.util.Set;
import java.util.Collections;

public class PlaylistDB {
    public PriorityQueue<PlaylistNode> _playlistDB;
    public ArrayList<PlaylistNode> _top8;

    public PlaylistDB() {
        _playlistDB = new PriorityQueue<PlaylistNode>();
        _top8 = new ArrayList<PlaylistNode>();
    }

    /** addPlaylist
    * @param: PlaylistNode playlist    A playlist to add to the database 
    * @return: true if playlist was added, false otherwise.
    */
    public boolean addPlaylist(PlaylistNode playlist) {
        // TODO(eugenek): You can add the same playlist multiple times
        // TODO(eugenek): Is that a problem??
        /* Figure out if to add the playlist if to update the top8, and how much to update 
        the popularity of each song by */
        Integer amountToChange = 0;
        boolean isAdded = false;
        if (_playlistDB.size() >= 1024) {
            PlaylistNode leastPopular = _playlistDB.peek();
            if (playlist.getPopularity() > leastPopular.getPopularity()) {
                amountToChange += playlist.getPopularity();
                amountToChange -= leastPopular.getPopularity();
                _playlistDB.poll(); // Remove the leastPopular entry
                isAdded = true;
            } else {
                amountToChange = 0;
                isAdded = false;
            }

        } else {
            amountToChange += playlist.getPopularity();
            isAdded = true;
        }

        /* Update the databases */
        if (isAdded) {
            _playlistDB.add(playlist);
            /* Update each song's best playlist and popularity */
            Set<Song> songSet = playlist.getSongSet();
            for (Song song : songSet) {
                song.setPopularity(song.getPopularity() + amountToChange);
                song.setBestPlaylist(playlist);
            }

            /* Update the top 8 */
            addTop8(playlist);
        }

        return isAdded;
    }

    public boolean addTop8(PlaylistNode playlist) {
        if (_top8.size() < 8) {
            _top8.add(playlist);
            return true;
        }

        PlaylistNode worstTop8 = Collections.min(_top8);
        if (playlist.getPopularity() > worstTop8.getPopularity()) {
            _top8.remove(worstTop8);
            _top8.add(playlist);
            return true;
        }

        return false;
    }

    public ArrayList<PlaylistNode> getTop8() {
        Collections.sort(_top8, Collections.reverseOrder());
        return _top8;
    }
}