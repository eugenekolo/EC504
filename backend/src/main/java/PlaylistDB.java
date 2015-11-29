/********************************************************************************
* Al Gore Rhythms 
* Playlist Application Project
*
* This file implemenets the Playlist database.
*
* @author: Eugene Kolo
* @email: eugene@kolobyte.com
* @version: 0.9
* @since: November 25, 2015
********************************************************************************/

package algore;

import java.util.ArrayList;
import java.util.PriorityQueue; // This is default implemented as a MinPriorityQueue
import java.util.Collections;

public class PlaylistDB {
    public PriorityQueue<Playlist> _playlistDB;
    public ArrayList<Playlist> _top8;

    public PlaylistDB() {
        _playlistDB = new PriorityQueue<Playlist>();
        _top8 = new ArrayList<Playlist>();
    }

    /**
    * @param: Playlist playlist    A playlist to add to the database 
    * @return: true if playlist was added, false otherwise.
    */
    public boolean addPlaylist(Playlist playlist) {
        /* Figure out if to add the playlist if to update the top8, and how much to update 
        the popularity of each song by */
        Integer amountToChange = 0;
        boolean isAdded = false;
        if (_playlistDB.size() >= 1024) {
            Playlist leastPopular = _playlistDB.peek();
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
            ArrayList<Song> songList = playlist.getSongList();
            for (Song song : songList) {
                song.setPopularity(song.getPopularity() + amountToChange);
                song.setBestPlaylist(playlist);
            }

            /* Update the top 8 */
            addTop8(playlist);
        }

        return isAdded;
    }

    public boolean addTop8(Playlist playlist) {
        if (_top8.size() < 8) {
            _top8.add(playlist);
            return true;
        }

        Playlist worstPlaylist = Collections.min(_top8);
        if (playlist.getPopularity() > worstPlaylist.getPopularity()) {
            _top8.remove(worstPlaylist);
            _top8.add(playlist);
            return true;
        }

        return false;
    }

    public ArrayList<Playlist> getTop8() {
        Collections.sort(_top8, Collections.reverseOrder());
        return _top8;
    }
}