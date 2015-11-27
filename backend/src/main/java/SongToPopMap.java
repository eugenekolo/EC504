/********************************************************************************
* Al Gore Rhythms 
* Playlist Application Project
* 
* @author: Eugene Kolo
* @email: eugene@kolobyte.com
* @version: 0.6
* @since: November 25, 2015
********************************************************************************/

package algore;

import java.util.HashMap;

public class SongToPopMap {
	public HashMap<String, Integer> _SongToPopMap;

	public SongToPopMap() {
		_SongToPopMap = new HashMap<String, Integer>();
	}

	public void putSong(String song, Integer pop) {
		_SongToPopMap.put(song, pop);
	}

	public Integer getSong(String song) {
		return _SongToPopMap.get(song);
	}
}