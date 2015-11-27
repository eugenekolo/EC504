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

import java.util.Set;

public class Song implements Comparable {
    public String _title;
    public Integer _popularity;
    public PlaylistNode _bestPlaylist;

    public Song(String title) {
        _title = title;
        _popularity = 0;
    }

    public Song(String title, Integer popularity) {
        _title = title;
        _popularity = _popularity;
    }

    public void setPopularity(Integer popularity) {
        _popularity = popularity;
    }

    public Integer getPopularity() {
        return _popularity;
    }

    public String getTitle() {
        return _title;
    }

    /** setBestPlaylist
    * Sets the bestPlaylist if the input's popularity is greater
    * return true on replacing, and false otherwise.
    */
    public boolean setBestPlaylist(PlaylistNode bestPlaylist) {
        if (_bestPlaylist == null) {
            _bestPlaylist = bestPlaylist;
            return true;
        }

        if (bestPlaylist.getPopularity() > _bestPlaylist.getPopularity()) {
            _bestPlaylist = bestPlaylist;
            return true;
        }
        return false;
    }
    
    public PlaylistNode getBestPlaylist() {
        return _bestPlaylist;
    }

    @Override
    public int compareTo(Object other) {
        if (_popularity < ((Song)other).getPopularity()) {
            return -1;
        }
        if (_popularity > ((Song)other).getPopularity()) {
            return 1;
        }
        return 0;
    }   
}