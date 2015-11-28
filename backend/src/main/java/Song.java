/********************************************************************************
* Al Gore Rhythms 
* Playlist Application Project
*
* This file implements a song structure.
* A song has a title, author, popularity, and its best best playlist.
*
* @author: Eugene Kolo
* @email: eugene@kolobyte.com
* @version: 0.8
* @since: November 25, 2015
********************************************************************************/

package algore;

import java.util.Set;

public class Song implements Comparable {
    public String _title;
    public String _author;
    public Integer _popularity;
    public PlaylistNode _bestPlaylist;

    public Song(String title) {
        _title = title;
        _popularity = 0; // TODO(eugenek): Make this null?
        _author = null;
    }

    public Song(String title, String author) {
        _title = title;
        _popularity = 0; // TODO(eugenek): Make this null?
        _author = author;
    }

    public Song(String title, Integer popularity) {
        _title = title;
        _popularity = _popularity;
        _author = null;
    }

    public Song(String title, String author, Integer popularity) {
        _title = title;
        _popularity = _popularity;
        _author = author;
    }


    public void setPopularity(Integer popularity) {
        _popularity = popularity;
    }

    public Integer getPopularity() {
        return _popularity;
    }


    public String getTitle() {
        return _title;
    }

    public void setTitle(String title) {
        _title = title;
    }


    /**
    * Sets the bestPlaylist if the input's popularity is greater
    * return true on replacing, and false otherwise.
    */
    public boolean setBestPlaylist(PlaylistNode bestPlaylist) {
        if (_bestPlaylist == null) {
            _bestPlaylist = bestPlaylist;
            return true;
        }

        if (bestPlaylist.getPopularity() > _bestPlaylist.getPopularity()) {
            _bestPlaylist = bestPlaylist;
            return true;
        }

        return false;
    }
    
    public PlaylistNode getBestPlaylist() {
        return _bestPlaylist;
    }


    /**
    * Let song be comparable by its popularity.
    */
    @Override
    public int compareTo(Object other) {
        if (_popularity < ((Song)other).getPopularity()) {
            return -1;
        }
        if (_popularity > ((Song)other).getPopularity()) {
            return 1;
        }
        return 0;
    }   
}