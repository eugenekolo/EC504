/********************************************************************************
* Al Gore Rhythms 
* Playlist Application Project
* 
* @author: Eugene Kolo
* @email: eugene@kolobyte.com
* @version: 0.6
* @since: November 25, 2015
********************************************************************************/

package algore;

import java.util.Set;
import java.util.ArrayList;

public class PlaylistNode implements Comparable {
	public Integer _popularity;
	public Set<Integer> _songSet;

	public PlaylistNode(Integer popularity, Set<Integer> songSet) {
		_popularity = popularity;
		_songSet = songSet;
    }

    public Set<Integer> getSongSet() {
    	return _songSet;
    }

    public Integer getPopularity() {
    	return _popularity;
    }

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