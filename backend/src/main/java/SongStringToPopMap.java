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

import java.util.HashMap;

public class SongStringToPopMap {
	public HashMap<String, Integer> _SongStringToPopMap;

	public SongStringToPopMap() {
		_SongStringToPopMap = new HashMap<String, Integer>();
	}

	public void putSong(String song, Integer pop) {
		_SongStringToPopMap.put(song, pop);
	}

	public Integer getSong(String song) {
		return _SongStringToPopMap.get(song);
	}
}