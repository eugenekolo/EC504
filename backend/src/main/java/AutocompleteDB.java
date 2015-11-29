/********************************************************************************
* Al Gore Rhythms 
* Playlist Application Project
*
* This file implements an Autocomplete database utilizing a PatriciaTrie.
* You can `putSong`'s into it, and then navigate autocomplete with `getPrefixMap`.
*
* @author: Eugene Kolo
* @email: eugene@kolobyte.com
* @version: 0.8
* @since: November 25, 2015
********************************************************************************/

package algore;

import java.util.SortedMap;
import java.util.ArrayList;

import org.apache.commons.collections4.trie.PatriciaTrie;

/**  
* https://commons.apache.org/proper/commons-collections/apidocs/org/apache/commons/collections4/
* trie/PatriciaTrie.html#headMap(K)
*/
public class AutocompleteDB {
    public PatriciaTrie<String> _SongTrie;

    public AutocompleteDB() {
        _SongTrie = new PatriciaTrie<String>();
    }

    public void putSong(String song) {
        _SongTrie.put(song, song);
    }

    public SortedMap<String, String> getPrefixMap(String prefix) {
        if (prefix == null || prefix == "") {
            // TODO(eugenek): Can filter here if Brax doesn't.
        }
        SortedMap<String, String> map = _SongTrie.prefixMap(prefix);
        return map;
    }

    public ArrayList<String> getPrefixList(String prefix) {
        SortedMap<String, String> map = getPrefixMap(prefix);
        ArrayList<String> list = new ArrayList<String>(map.values());
        return list;
    }

}