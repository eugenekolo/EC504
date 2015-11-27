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

public class SongIdToTitleMap {
	public HashMap<Integer, String> _SongIdToTitleMap;

	public SongIdToTitleMap() {
		_SongIdToTitleMap = new HashMap<Integer, String>();
	}

	public void putSong(Integer id, String song) {
		_SongIdToTitleMap.put(id, song);
	}

	public String getSong(Integer id) {
		return _SongIdToTitleMap.get(id);
	}
}