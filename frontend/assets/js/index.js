$(function() {
    /*************** 
        getTop8 
    ***************/
    // On page load: Ajax GET request to '/api/getTop8'
    $.ajax({
        type: 'GET',
        url: '/api/getTop8',
        data: 'json',
        datatype: 'application/json',
        success: function(data) {
            // On success below jquery fills rows of Top8 table with data retrieved from /api/getTop8
            var hash = JSON.parse(data);
            // for (var i = 0; i <= 7; i++) {
            //     for (var key in hash[i]['songList']) {
            //         console.log(key);
            //     }
            // }
            for (var i = 0; i <= 7; i++) {
                if (typeof hash[i] === 'undefined') {
                    $('td.playlist-name:eq(' + i + ')').text('N/A');
                    $('td.playlist-popularity:eq(' + i + ')').text('N/A');
                } else {
                    for (var j in hash[i]['songList']) {
                        var currentPlaylistText = $('td.playlist-name:eq(' + i + ')').text();
                        currentPlaylistText += hash[i]['songList'][j]['title'] + ' by ' + hash[i]['songList'][j]['author'] + ', ';
                        $('td.playlist-name:eq(' + i + ')').text(formatPlaylist(currentPlaylistText));
                        $('td.playlist-popularity:eq(' + i + ')').text(hash[i]['popularity']);
                    }
                    // Remove trailing comma
                    $('td.playlist-name:eq(' + i + ')').text($('td.playlist-name:eq(' + i + ')').text().slice(0, -2));
                }
            }
        },
        error: function(data) {
            // alert('Failed to retrieve Top8 content');
            console.log('Failed to retrieve Top8 content');
        }
    });
    /******************** 
        suggestPlaylist
    *********************/
    function suggestPlaylist() {
        var parseTitle = $('#enter-song').val().replace(/\\/g, '\\/').split('#');
        var title = parseTitle[0];
        if (title === '') {
            console.log("LET ME KNOW");
            $('.author-suggest').text('');
            removeTableRows();
            var author = $('#enter-song').val().split('#').pop();
            if (!(author === '')) {
                $('.song-suggest').text('No playlist found');
            } else {
                $('.song-suggest').text('');
            }
        } else {
            var data = {};
            var author = $('#enter-song').val().split('#').pop();
            // Gets the song name text
            data['song'] = {
                'title': title,
                'author': author
            };
            // data['song'] = "As She's Walking Away (w\\/ Alan Jackson)"; // Gets the song name text
            console.log(data['song']);
            $.ajax({
                type: 'POST',
                url: '/api/suggestPlaylist',
                data: JSON.stringify(data),
                dataType: "text",
                contentType: 'application/json',
                success: function(data) {
                    // On success, below jquery suggests playlist with highest popularity containing entered song
                    console.log('Success!');
                    var hash = JSON.parse(data);
                    // console.log(hash['songList'][0]); 
                    removeTableRows();
                    for (var i in hash['songList']) {
                        $('.song-suggest:last').text(formatPlaylist(hash['songList'][i]['title']));
                        $('.author-suggest:last').text(formatPlaylist(hash['songList'][i]['author']));
                        addTableRows();
                        addTableRowsClasses();
                    }
                    // console.log($('.song-suggest:first').text());
                    // $('.playlist-row:first').remove();
                },
                error: function(data) {
                    // alert('Failed to retrieve Top8 content');
                    console.log('Failed to retrieve suggestPlaylist content');
                    removeTableRows();
                    $('.song-suggest').text('No Playlist Found');
                }
            });
        }
    }
    /******************** 
        getAutocomplete
    *********************/
    function autocomplete() {
        var parseTitle = $('#enter-song').val().replace(/\\/g, '\\/').split('#');
        var title = parseTitle[0];
        if (title === '') {
            console.log("LET ME KNOW");
            $('.song-autocomplete').text('');
            $('.author-autocomplete').text('');
        } else {
            var data = {};
            data['song'] = title; // Gets the song name text
            // console.log(data['song']);
            $.ajax({
                type: 'POST',
                url: '/api/getAutocomplete',
                data: JSON.stringify(data),
                dataType: "text",
                contentType: 'application/json',
                success: function(data) {
                    // On success, below jquery suggests playlist with highest popularity containing entered song
                    console.log('Success!');
                    var hash = JSON.parse(data);
                    console.log(hash); // Helps to know if the proper songs are being returned
                    for (var i = 0; i <= 3; i++) {
                        if (typeof hash[i] === 'undefined') {
                            // An autocomplete option is empty if the hash did not return the 
                            // specified index
                            $('.song-autocomplete:eq(' + i + ')').text('');
                            $('.author-autocomplete:eq(' + i + ')').text('');
                        } else {
                            // Otherwise fill the autocomplete option with the corresponding song name
                            $('.song-autocomplete:eq(' + i + ')').text(hash[i]['title'].replace(/\\/g, ''));
                            $('.author-autocomplete:eq(' + i + ')').text(hash[i]['author'].replace(/\\/g, ''));
                        }
                    }
                },
                error: function(data) {
                    console.log('Failed to retrieve getAutocomplete content');
                    for (var i = 0; i <= 3; i++) {
                        $('.song-autocomplete:eq(' + i + ')').text('');
                        $('.author-autocomplete:eq(' + i + ')').text('');
                    }
                }
            });
        }
    }
    /*************************************
     * Helper functions
     **************************************/
    // Formats the playlist to remove ##'s and \'s
    function formatPlaylist(playlist) {
        playlist = playlist.replace(/\\/g, '');
        return playlist;
    }

    function addTableRows() {
        var songRow = songListTable.insertRow(); // Add to bottom
        var songTitleCell = songRow.insertCell(0);
        var songAuthorCell = songRow.insertCell(1);
    }

    function addTableRowsClasses() {
        // Assigns class names to dynamically generated rows
        $('#songListTable tbody tr td').attr('class', ''); // remove all previous classes
        $('#songListTable tbody tr').attr('class', 'playlist-row');
        $('#songListTable tbody tr td:even').attr('class', 'song-suggest');
        $('#songListTable tbody tr td:odd').attr('class', 'author-suggest');
        $('#songListTable thead tr').attr('class', 'playlist-header'); //
    }

    function removeTableRows() {
        $('.playlist-row:first').attr('class', 'temp');
        $('.playlist-row').remove();
        $('.playlist-row:first').attr('class', 'playlist-row');
    }
    /*************************************
     * Event Listeners
     **************************************/
    // keypress jquery event handler
    $('#enter-song').bind("keyup", function(e) {
        if (e.keyCode == 40) {
            // If the down arrow is selected, jump to the autocomplete suggestions
            $('#enter-song').blur();
            $("select:first").focus();
            $('.song-autocomplete:eq(' + 0 + ')').prop('selected', true);
            for (var i = 1; i <= 3; i++) {
                $('.song-autocomplete:eq(' + i + ')').prop('selected', false);
            }
            $('#suggested-playlist').prop('selected', false);
        } else {
            // For each key pressed a playlist and 4 songs are suggested
            suggestPlaylist();
            autocomplete();
        }
    });
    // $('.song-autocomplete:eq(' + 0 + ')').bind("keyup", function(e) {
    //     if (e.keyCode == 40) {
    //         // If the down arrow is selected, jump to the autocomplete suggestions
    //         $('#enter-song').focus();
    //         $("select:first").blur();
    //         $('.song-autocomplete:eq(' + 0 + ')').blur('selected', true);
    //     } 
    // });
    // Clicking on an autocomplete suggestion changes the enter song text
    // to the autocomplete suggestion and updates the suggested playlist
    $('.autocomplete-row').click(function() {
        var enterSong = $('#enter-song');
        var song = $(this).children('.song-autocomplete').text();
        var author = $(this).children('.author-autocomplete').text();
        enterSong.val(song + '#' + author);
        var e = $.Event('keyup');
        e.which = 13; // 13 = enter key
        enterSong.trigger(e); // Triggers keyup event listener for enter song text input
        enterSong.focus();
    });

});
