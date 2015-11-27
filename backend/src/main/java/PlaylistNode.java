package algore;

import java.util.Set;

public class PlaylistNode {
	public Integer _popularity;
	public Set<Integer> _songSet;

	public PlaylistNode(Integer popularity, Set<Integer> songSet) {
		_popularity = popularity;
		_songSet = songSet;
    }
}