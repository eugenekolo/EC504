# Playlist algorithms and data structures
## Summary
1. Add/delete new playlist(s) to database
2. Autocomplete to top 4 songs
	1. Get song popularity based on playlist popularity
	2. Suggest playlist
3. List top 8 PLs


Bulk of the work is done when you add/delete a playlist.  
Special thought is put into keeping (2) highly responsive, as users are probably alright/more understanding with adding a 100 playlists taking a longer time. Non-responsive autocomplete is awful.

Memory is considered lastly, as applications these days take up 200MB+ and nobody bats an eye. A playlist app can easily use 10MB and be fine.

## Definitions and data structures used
1. `L` = number of playlists at start  
`N` = number of playlists currently in the DB  
**Playlist database**, `PlaylistDB` is a Double-ended Priority Queue (DEPQ), implemented as a binary min-max heap. The starting database will be built in `O(L)` time (`L` inserts at `O(1)` time each). The key in the DEPQ is playlist popularity.

	Functions:   
	`Insert - O(1)`  
	`Find-Min - O(1)`  
	`Find-Max - O(1)`    
	`Delete-Min - O(logN)`  
	`Delete-Max - O(logN)`  
	`Top-K - O(KlogN) // Really just Find-Max+Delete-Max K times`  
	`Create - O(L)`

2. `K` = number of total songs  
`M` = length of longest song  
The **autocomplete database**, `AutocompleteDB` will be a radix tree/ternary search tree. The starting database will be built in `O(KM)`. Total memory requirement is on the order of `O(KM)`. 
	
	Functions:  
	`Find-Word - O(M)`  
	`Find-Sub-Words - O(M*O(DFS))`  
	`Create - O(KM)`

3. `K` = number of total songs  
The **song popularity database**, `SongPopMap` will be a map, implemented as a hashmap. Memory requirement will be on the order of `O(K)`, and it will take `Add-Song` ran `K` times for a runtime of `O(K)` to create the database. 

	Functions:  
	`Add-Song - O(1)`  
	`Find-Song - O(1)`  
	`Increase-Pop - O(1)`  
	`Decrease-Pop - O(1)`  
	`Set-Best - O(1)`  
	`Get-Best - O(1)`

## Adding 1 playlist
#### Adding playlist to database
The playlist database, can have an `Insert` of a new playlist, `P`, done in `O(1)` average ([proof](https://en.wikipedia.org/wiki/Binary_heap#Insert)). Due to lack of space, (1024 playlists already exist), a `Delete-Min` might occur. See below for information on that. 

#### Increase song popularity 
The playlist, `P`, will contain `S` songs and 1 popularity value, `V`.

The `SongPopMap` will `Increase-Pop` `O(S)` times in `O(1)` time, and potentially `Set-Best`. 
This results in each song's popularity increased in `O(S)` time.

    for s in S: // O(S)
		SongPopMap[s].Increase-Pop(V) // O(1)
		if (SongPopMap[s].Get-Best().Pop < P.Pop):
			SongPopMap[s].Set-Best(P) // O(1)
	// O(S) total

## Removing 1 playlist
#### Removing playlist from database
The playlist database occasionally will require making room for new playlists. This is accomplished by `Find-Min`, and then `Delete-Min` to make space for 1 new playlist. This is a runtime of `O(logN)`. 

#### Decrease song popularity
The only way a song's popularity can decrease is if a playlist gets removed. The only way for a playlist to get removed is if it was in the bottom most popular ones. Therefore if a song has lost its best playlist, then it does not belong to any other playlist now. 

 	for s in S: // O(S)
		SongPopMap[s].Decrease-Pop(V) // O(1)
		if (SongPopMap[s].Get-Best() == P):
			SongPopMap[s].Set-Best(null) // O(1)
	// O(S) total


## Autocompleting song
Songs will be autocompleted using a radix tree.

The radix tree provides us with `O(M)` lookup, and `O(MK)` build time. Additionally the radix tree can be stepped back and forth for responsive autocompletion. 

Finding all possible songs possible for a given string takes `O(M)*O(DFS)`. Depth-first Search takes `O(|V|+|E|)` at worst. 

#### Song popularity
Simultaneously to the word finding, the top 4 will be chosen using the `SongPopMap` and doing a simple max-search. There is no need to make this too fancy, as this will already be `O(possible matches)`, and every song must be checked. 

#### Suggesting best playlist 
Once the user has chosen a song, a playlist has to be suggested, the `SongPopMap` is used. `Get-Best` is called onto the song, runtime of `O(1)`. 

## List top 8
Listing top 8 can be more efficient, the DEPQ has to perform a `Top-8`. `Top-8` can be implemented as `Find-Max()` + `Delete-Max` on a temp heap, for `O(8*logN)` run-time.

This can be improved perhaps by using a cache (a simple array) that is synced with `Insert`, and holds the top 8 always. This would make `Top-8` `O(1)`, but increase the run-time of `Insert`, and `Delete-*` slightly. 

