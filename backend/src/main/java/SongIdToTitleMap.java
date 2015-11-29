/********************************************************************************
* Al Gore Rhythms 
* Playlist Application Project
*
* This file implements a Song Id to Song Title Map.
*
* @author: Eugene Kolo
* @email: eugene@kolobyte.com
* @version: 0.0
* @since: November 25, 2015
********************************************************************************/

package algore;

import java.util.HashMap;

public class SongIdToTitleMap {
    public HashMap<String, String> _SongIdToTitleMap;

    public SongIdToTitleMap() {
        _SongIdToTitleMap = new HashMap<String, String>();
    }

    public void putSong(String id, String songTitle) {
        _SongIdToTitleMap.put(id, songTitle);
    }

    public String getSong(String id) {
        return _SongIdToTitleMap.get(id);
    }
}