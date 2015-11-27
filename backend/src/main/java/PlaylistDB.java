/********************************************************************************
* Al Gore Rhythms 
* Playlist Application Project
*
* This is the backend part. Including the web and data structure logic.
*
* Features:
*   + Autocomplete song
*   + List top 8 playlists
*   + Add up to 1024 playlists
*   + Suggest most popular playlist with input song
*   + Restful API
*   + Hackable (separated front end, separated data structures)
*   + Always returns JSON
*   + Efficient
*   + Nice frontend 
*
* @author: Eugene Kolo
* @email: eugene@kolobyte.com
* @version: 0.6
* @since: November 25, 2015
********************************************************************************/

package algore;

import java.util.HashMap;
import java.util.ArrayList;
//import java.util.PriorityQueue; // This is default implemented as a MinPriorityQueue
import com.google.common.collect.MinMaxPriorityQueue;
import java.util.HashSet;
import java.util.Set;

public class PlaylistDB {

	// TODO(eugenek): Test out Guava.DBEQ vs PriorityQueue

	/* Can be configured with a maximum size and automatic removal of greatest element*/
	/* Top is default implemented as min */
	public MinMaxPriorityQueue<PlaylistNode> _playlistDB;

	/* Default implemented as a MinPriorityQueue */
	//public PriorityQueue<PlaylistNode> _playlistDB;
	//public HashMap<Integer, String> _top8;

	public PlaylistDB() {
		//_playlistDB = new MinMaxPriorityQueue<PlaylistNode>();
		MinMaxPriorityQueue.Builder builder = _playlistDB.maximumSize(1024);
		_playlistDB = builder.create();

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
		// TODO(eugenek): You can add the same playlist multiple times
		// TODO(eugenek): Is that a problem??
		_playlistDB.add(playlist);

		// TODO(eugenek): Update SongToPop map here too
	}

	public ArrayList<PlaylistNode> getTop8List() {
		ArrayList<PlaylistNode> top8 = new ArrayList<PlaylistNode>();
		Integer stopSize = Math.min(8, _playlistDB.size()); // Handle case size < 8

		/* Pop the top 8 playlists from the end of the queue */
		for (int i = 0; i < stopSize; i++) {
			PlaylistNode playlist = _playlistDB.pollLast();
			top8.add(playlist);
		}

		/* Put the top 8 playlists back to the end of the queue */
		for (int i = 0; i < stopSize; i++) {
			_playlistDB.add(top8.get(i));
		}

		return top8;
	}

	public HashMap<Integer, PlaylistNode> getTop8Map() {
		ArrayList<PlaylistNode> top8List = getTop8List();
		HashMap<Integer, PlaylistNode> top8Map = new HashMap<Integer, PlaylistNode>();
		Integer stopSize = Math.min(8, _playlistDB.size()); // Handle case size < 8

		for (int i = 0; i < top8List.size(); i++) {
			top8Map.put(i, top8List.get(i));
		}

		return top8Map;
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