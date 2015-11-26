package algore;

import java.util.HashMap;
import java.util.SortedMap;
import java.util.ArrayList;

import org.apache.commons.collections4.trie.PatriciaTrie;

public class AutocompleteDB {
	public PatriciaTrie<String> _SongTrie;

	public AutocompleteDB() {
 		_SongTrie = new PatriciaTrie<String>();
    }

	public void addSong(String song) {
		_SongTrie.put(song, song);
	}

	public SortedMap<String, String> getPrefixMap(String prefix) {
		SortedMap<String, String> map = _SongTrie.prefixMap(prefix);
		return map;
	}

	public ArrayList<String> getPrefixList(String prefix) {
		SortedMap<String, String> map = getPrefixMap(prefix);
		ArrayList<String> list = new ArrayList<String>(map.values());
		return list;
	}

}