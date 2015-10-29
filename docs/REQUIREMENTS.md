# Playlist/Song App
## Playlist and Song Description
Playlist Database `Pdb` can hold up to **1024** playlists `p`:

`0 <= |Pdb| <= 1024`

Playlist format is a list of songs (by their ID), a tab, and the playlist's popularity:

`p0 = 44 5 0 29 1 257 7 153\t85`  
`p1 = 1 2 3 8 5\t96`

The list of all songs possible exist in a file organized as:  
`<song id><tab><song title><tab><artist>`  
`13\tChampagne Life\tNe-Yo`

There can not be repeated songs in a playlist.

## Feature Set
1. Add playlists
2. Display top 8 playlists
3. Suggest a playlist for a given song
4. Autocomplete song and display top 4 most popular

### Add playlists to the database
Up to **128** playlists can be added to the current database. In order to make room in the database, playlists with the lowest popularity should be removed.  

Ex:  
> Database has **1020** playlists  
> Adding **10** playlists should result in the database being size **1024**  
> ??? (emailed Ata Turk)

### Display the top 8 playlists
The top 8 playlists by popularity should be displayed

### Suggest a playlist
Once a user has chosen a song, the most popular playlist that contains it should be suggested. 

### Autocomplete and suggest top 4
When a user is typing a song, after every keystroke the top 4 most popular songs that are able to be completed using the typed string so far should be suggested.

Song popularity is the sum of the popularities of the playlists that contain it.

Ex:
> 'all' ->  
> 1. "all about me" <545>  
> 2. "all about the base" <529>  
> 3. "all that remains" <529>  
> 4. "all along the watchtower" <522>  
> 'all o' ->  
> 1. "all of me" <421>   
> 2. "all of me loves all of you" <421>  
> 3. "all of my life" <382>  
> 4. "all of the stars" <306>  


