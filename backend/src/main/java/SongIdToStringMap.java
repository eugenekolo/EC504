package algore;

import java.util.HashMap;

public class SongIdToStringMap {
	public HashMap<Integer, String> _SongIdToStringMap;

	public SongIdToStringMap() {
		_SongIdToStringMap = new HashMap<Integer, String>();
	}

	public void putSong(Integer id, String song) {
		_SongIdToStringMap.put(id, song);
	}

	public String getSong(Integer id) {
		return _SongIdToStringMap.get(id);
	}
}