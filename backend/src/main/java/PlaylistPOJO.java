/********************************************************************************
* Al Gore Rhythms 
* Playlist Application Project
*
* This file implements a playlist structure.
* A playlist has a popularity, and song set.
*
* @author: Eugene Kolo
* @email: eugene@kolobyte.com
* @version: 0.9
* @since: November 25, 2015
********************************************************************************/

package algore;

import java.util.ArrayList;
import java.util.HashMap;

public class PlaylistPOJO {
    public Integer popularity;
    public ArrayList<HashMap<String,String>> songList;


    public PlaylistPOJO(Integer popularity_, ArrayList<HashMap<String,String>> songList_) {
    	popularity = popularity_;
    	songList = songList_;
    }

    public Integer getPopularity() {
    	return popularity;
    }

    public ArrayList<HashMap<String,String>> getSongList() {
    	return songList;
    }
}