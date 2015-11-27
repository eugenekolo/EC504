package algore;

import java.util.HashMap;
import java.util.SortedMap;
import java.util.ArrayList;
import java.util.PriorityQueue; // This is default implemented as a MinPriorityQueue
import com.google.common.collect.MinMaxPriorityQueue;
import java.util.SortedSet;

public class PlaylistDB {

	// TODO(eugenek): Test out Guava.DBEQ vs PriorityQueue

	/* Can be configured with a maximum size and automatic removal of greatest element*/
	/* Top is default implemented as min */
	public MinMaxPriorityQueue<PlaylistNode> _playlistDB;

	/* Default implemented as a MinPriorityQueue */
	//public PriorityQueue<PlaylistNode> _playlistDB;
	//public HashMap<Integer, String> _top8;

	public PlaylistDB() {
		_playlistDB.maximumSize(1024).create();
		//_playlistDB = new PriorityQueue<PlaylistNode>;
		//_top8 = new HashMap<Integer, String>();
    }

    /****************************
    * If using DEBQ 
    ****************************/
    /** addPlaylist
    * @param: PlaylistNode playlist    A playlist to add to the database 
    * @return: true if playlist was added, false otherwise.
    * @note: A DBEQ only need addPlaylist and a maximum size specified to handle deleting Playlists too 
    */
	public void addPlaylist(PlaylistNode playlist) {
		_playlistDB.add(playlist);
	}

	public ArrayList<PlaylistNode> getTop8() {
		ArrayList<PlaylistNode> top8 = new ArrayList<PlaylistNode>();
		for (int i = 0; i < 8; i++) {
			PlaylistNode playlist = _playlistDB.pollLast();
			top8.add(playlist);
		}
		for (int i = 0; i < 8; i++) {
			_playlistDB.add(top8.get(i));
		}
		return top8;
	}
	
    /****************************
    * If using PriorityQueue
    ****************************/
    /** addPlaylist
    * @param: PlaylistNode playlist    A playlist to add to the database 
    * @return: true if playlist was added, false otherwise.
    * @note: A MinPriorityQueue requires a remove handler to handle DB size >= 1024
    */
	/*public boolean addPlaylist(PlaylistNode playlist) {
		if (_playlistDB.size() >= 1024) { // Should never be greater than 1024
			if (playlist.popularity > _playlistDB.peek().popularity) {
				_playlistDB.poll();
				_playListDB.add(playlist);
				return true;
			}
			else {
				// Don't add the playlist
				return false;
			}
		}
		else {
			_playlistDB.add(playlist);
			return true;
		}
	}
	*/


}