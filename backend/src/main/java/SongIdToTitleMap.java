/********************************************************************************
* Al Gore Rhythms 
* Playlist Application Project
*
* This file implements a Song Id to Song Title Map.
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

    public void putSong(Integer id, String songTitle) {
        _SongIdToTitleMap.put(id, songTitle);
    }

    public String getSong(Integer id) {
        return _SongIdToTitleMap.get(id);
    }
}