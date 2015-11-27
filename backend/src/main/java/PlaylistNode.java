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

import java.util.Set;

public class PlaylistNode implements Comparable {
	public Integer _popularity;
	public Set<Song> _songSet;

	public PlaylistNode(Integer popularity, Set<Song> songSet) {
		_popularity = popularity;
		_songSet = songSet;
    }

    public Set<Song> getSongSet() {
    	return _songSet;
    }

    public Integer getPopularity() {
    	return _popularity;
    }

    @Override
	public int compareTo(Object other) {
    	if (_popularity < ((PlaylistNode)other).getPopularity()) {
    	    return -1;
    	}
    	if (_popularity > ((PlaylistNode)other).getPopularity()) {
    	    return 1;
    	}
    	return 0;
	}	
}