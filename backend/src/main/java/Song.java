/********************************************************************************
* Al Gore Rhythms 
* Playlist Application Project
*
* This is the backend part. Including the web and data structure logic.
*
* Features:
*   + Autocomplete song
*   + List top 8 playlists
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
* @version: 0.6
* @since: November 25, 2015
********************************************************************************/

package algore;

import java.util.Set;

public class Song {
    public String _title;
	public Integer _popularity;
    public PlaylistNode _bestPlaylist;

	public Song(String title) {
        _title = title;
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

    public Integer getTitle() {
        return _title;
    }

    /** setBestPlaylist
    * Sets the bestPlaylist if the input's popularity is greater
    * return true on replacing, and false otherwise.
    */
    public boolean setBestPlaylist(PlaylistNode bestPlaylist) {
        if (bestPlaylist.getPopularity() > _bestPlaylist.getPopularity()) {
            _bestPlaylist = bestPlaylist;
            return true;
        }
        return false;
    }
    
    public boolean getBestPlaylist() {
        return _bestPlaylist;
    }
}