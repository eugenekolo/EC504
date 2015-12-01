/********************************************************************************
* Al Gore Rhythms 
* Playlist Application Project
*
* This file implements a playlist structure.
* A playlist has a popularity, and song set.
*
* @author: Eugene Kolo
* @email: eugene@kolobyte.com
* @version: 1.0
* @since: November 25, 2015
********************************************************************************/

package algore;

import java.util.ArrayList;

public class Playlist implements Comparable {
    public Integer _popularity;
    public ArrayList<Song> _songList;

    public Playlist(Integer popularity, ArrayList<Song> songList) {
        _popularity = popularity;
        _songList = songList;
    }


    public ArrayList<Song> getSongList() {
        return _songList;
    }

    public void setSongList(ArrayList<Song> songList) {
        _songList = songList;
    }


    public Integer getPopularity() {
        return _popularity;
    }

    public void getPopularity(Integer popularity) {
        _popularity = popularity;
    }
    

    /**
    * Let Playlist be comparable by its popularity.
    */
    @Override
    public int compareTo(Object other) {
        if (_popularity < ((Playlist)other).getPopularity()) {
            return -1;
        }
        if (_popularity > ((Playlist)other).getPopularity()) {
            return 1;
        }
        return 0;
    }   
}