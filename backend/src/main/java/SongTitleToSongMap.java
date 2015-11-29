/********************************************************************************
* Al Gore Rhythms 
* Playlist Application Project
*
* This file implements a Song Title to Song Object Map.
*
* @author: Eugene Kolo
* @email: eugene@kolobyte.com
* @version: 0.9
* @since: November 25, 2015
********************************************************************************/

package algore;

import java.util.HashMap;

public class SongTitleToSongMap {
    public HashMap<String, Song> _SongTitleToSongMap;

    public SongTitleToSongMap() {
        _SongTitleToSongMap = new HashMap<String, Song>();
    }

    public void putSong(String songTitle, Song song) {
        _SongTitleToSongMap.put(songTitle, song);
    }

    public Song getSong(String songTitle) {
        return _SongTitleToSongMap.get(songTitle);
    }
}