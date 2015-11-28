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

// TODO(eugenek): Change name to Playlist and songset to songlist?
package algore;

import java.util.Set;

public class PlaylistNode implements Comparable {
    public Integer _popularity;
    public Set<Song> _songSet;

    public PlaylistNode(Integer popularity, Set<Song> songSet) {
        _popularity = popularity;
        _songSet = songSet;
    }


    public Set<Song> getSongSet() {
        return _songSet;
    }

    public void setSongSet(Set<Song> songSet) {
        _songSet = songSet;
    }


    public Integer getPopularity() {
        return _popularity;
    }

    public void getPopularity(Integer popularity) {
        _popularity = popularity;
    }


    /**
    * Let playlistNode be comparable by its popularity.
    */
    @Override
    public int compareTo(Object other) {
        if (_popularity < ((PlaylistNode)other).getPopularity()) {
            return -1;
        }
        if (_popularity > ((PlaylistNode)other).getPopularity()) {
            return 1;
        }
        return 0;
    }   
}