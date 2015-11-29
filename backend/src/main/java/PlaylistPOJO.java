/********************************************************************************
* Al Gore Rhythms 
* Playlist Application Project
*
* This file implements a playlist structure.
* A playlist has a popularity, and song set.
*
* @author: Eugene Kolo
* @email: eugene@kolobyte.com
* @version: 0.8
* @since: November 25, 2015
********************************************************************************/

package algore;

import java.util.ArrayList;

public class PlaylistPOJO {
    public Integer popularity;
    public ArrayList<String> songList;

    public Integer getPopularity() {
    	return popularity;
    }

    public ArrayList<String> getSongList () {
    	return songList;
    }
}