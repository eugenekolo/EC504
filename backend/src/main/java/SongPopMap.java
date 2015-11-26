package algore;

import java.util.HashMap;

public class SongPopMap {
	public HashMap<String, Integer> _SongPopMap;

	public SongPopMap() {
		_SongPopMap = new HashMap<String, Integer>();
	}

	public void setSong(String song, Integer pop) {
		_SongPopMap.put(song, pop);
	}

	public Integer getSong(String song) {
		return _SongPopMap.get(song);
	}
}