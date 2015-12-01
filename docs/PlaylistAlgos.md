# Playlist App Report
Eugene Kolo and Braxton Brewton  
eugene@kolobyte.com  braxtonbrewton@gmail.com  
November 2015

## Web

### Frontend

We utilized HTML, CSS, and javascript to handle client side operations. Our HTML and CSS present a responsive layout for the user with the help of Twitter’s Bootstrap. Our javascript primarily used jQuery libraries to perform AJAX requests to the backend. These requests enabled our web app to respond real-time to user input.

### AJAX Requests

The web app performs 5 different requests to 5 different backend routes. 

Each time the suggest playlists page is loaded, a GET request is made to the /api/getTop8 backend route. Once the request is processed, the backend returns a hash containing the top 8 most popular playlists. The hash is then displayed on the browser in a table sorted by most to least popular.

On any character key press, an event handler is triggered that converts the text typed thus far in the enter song text input field into a string. The string is then immediately input into an `autocomplete()` function. The `autocomplete()` function performs a POST request with the string to the backend route /api/getAutocomplete, returning the songs in the database that match the entry.

Upon clicking or pressing enter on any of the four rows of the autocomplete table found in the suggest playlists page, the corresponding row’s song and artist are entered into a `suggestPlaylist()` function. The `suggestPlaylist()` function submits a POST request to the backend route /api/suggestPlaylist with the given artist and song, returning a hash that represents a playlist that contains the selected song.

In a situation where a user creates a playlist by selecting songs from the autocomplete table and inputting a popularity value, the user must click enter upload list button to upload all songs from the Songs to add table to the database as one playlist. Clicking the button triggers an event handler that performs a POST request to the backend route /api/addPlaylist with the given songs, artists, and popularity in the form of a hash. This route stores the playlist hash in the proper location within our playlistDB database.

Alternatively, a user may upload up to 128 playlists at a time to the database by uploading a text file containing playlists. By clicking on the upload file button the user triggers a POST request to the backend route /api/addPlaylists containing the playlists. This route stores the playlist hash in the proper location within our playlistDB database.

### Backend
We utilized Spark Java to create a RESTful API. The API can be used by any frontend, and features can be easily added to it. Our API always returns JSON and is documented here:

We have 5 routes:

    GET /api/getTop8
    POST /api/addPlaylist
    POST /api/addPlaylists
    POST /api/getAutocomplete
    POST /api/suggestPlaylist
	
## Algorithms and data structures
### Summary
1. Add new playlist(s) to database
2. Autocomplete to top 4 songs
	* List songs by popularity based on playlist's popularitys that have the song.
3. Suggest most popular playlist with a specific song.
4. List top 8 most popular playlists

Bulk of the work is done when you add a playlist.  
Special thought is put into keeping (2) highly responsive, as users are more understanding with adding a 100 playlists taking a longer time. Non-responsive autocomplete is awful.

Memory is considered lastly, as applications these days take up 200MB+ and nobody bats an eye. A playlist app can easily use 10MB and be fine. Nonetheless, the memory usage is minimal due to the usage of references and only allocating at the app launch.

### Definitions and data structures used
**Playlist database**  

`PlaylistDB` is a Priority Queue, implemented as a binary min heap. The starting database will be built in `O(L)` time (`L` inserts at `O(1)` time each). The key in the PriorityQueue is playlist popularity. The `PlaylistDB` is limited to 1024 playlists, and removes the least popular playlist on addition of any new playlists.

`L` = number of playlists at start  
`N` = number of playlists currently in the DB  

	Functions:   
	Insert - O(logN)
	Find-Min - O(1)  
	Delete-Min - O(logN)  
	Create - O(L)

**Top 8 cache**  
The top 8 cache, `Top8` is implemented as an 8 element linked list. Updated when `PlaylistDB` is updated. It stores the top 8 most popular playlists based on popularity. Due to the small size, all operations are constant. When a playlist is added, a playlist may also be removed due to being replaced. The `Top8` cache is updated based on the result of that replacement. 

    Functions:  
    Add - O(1)
    Remove - O(1)
    Peek - O(1)
    
**Autocomplete database**  

The **autocomplete database**, `AutocompleteDB` is a PatriciaTrie (a space-optimized Trie). A trie is a tree where each node is a letter. At app launch, all songs are added to it, taking `O(KM)` time. Searching the trie takes `O(M)`. The memory taken by the trie is on the order of `O(KM)`.

`K` = number of total songs  
`M` = length of longest song  
	
	Functions:  
	Find-Prefix-Map - O(M), performs a tree traversal, where each node is a letter.
	Create - O(KM)
	
	Algorithms:  
	Get-Top-4 - O(M), essentially a K-selection algorithm
	
**Song database**  
The **song database**, is a series of Maps that point to songs in the `PlaylistDB`. The song database is indexed by a song's title and author, 
and points to a **song object** stored in a `Playlist` in the `PlaylistDB`.

	Functions:  
	Add-Song - O(1) 
	Find-Song - O(1)  
	Set-Pop - O(1) 
	Set-Best - O(1)
	Get-Best - O(1)

### Initialization
During app start up a `SongDB` is created that stores all songs, with their titles, and authors. Additionally the song titles and authors are pushed into an `AutocompleteDB`. `PlaylistDB` is initially empty. 

Our web backend uses Spark Java, communicaties via HTTP and JSON. REST practices are attempted.

### Adding playlist
The playlist database, can have an `Insert` of a new playlist, `P`, done in `O(logN)`. Due to lack of space, (1024 playlists already exist), a `Delete-Min` might occur. During the process of adding a playlist to database, the `SongDB` and `Top8` are updated as well.

#### Update song data
The playlist, `P`, will contain `S` songs and 1 popularity value, `V`.

Each song in the `SongDB` will `Set-Pop` `O(S)` times in `O(1)`, and potentially perform a `Set-Best` in `O(1)`, if `P`'s popularity is greater than the song's previous best playlist's popularity. This allows for efficient lookup when suggesting the best playlist that contains a specific song.

The amount to `Set-Pop` to is determined based on if a playlist was added and removed, due to being replaced.

This results in each song's popularity increased in `O(S)` time.

    for s in S: // O(S)
		SongDB[s].Set-Pop(V) // O(1)
		if (SongDB[s].Get-Best().Pop < P.Pop):
			SongDB[s].Set-Best(P) // O(1)

#### Updating top 8 playlists
The top 8 playlists are stored in a simple list cache. The least popular playlist in the top 8 playlists is located on the end. When a new playlist is added to the database, its popularity is compared to the least popular playlist in the top 8. If the new playlist's popularity is more, it replaces the least popular one. Due to the small size, and efficient removal, all operations are O(1). 

	if P.Get-Pop() > Top8[0].Get-Pop():
		Top8.Replace(0, P)
		Sort(Top8)
				
### Autocompleting song
Songs are autocompleted using the `AutocompleteDB`, a Patricia Trie. Autocompleting is accomplished by sending a `POST /api/getAutocomplete` with an entered prefix. The Patricia Trie is then searched for that prefix. The top 4 songs that have the correct prefix are returned.

Because the number of songs returned is so small (song list is only 4000 elements), on average <4 songs, a simple sort is done to find the top 4 songs based on their popularity stored with them in their Java bean.


