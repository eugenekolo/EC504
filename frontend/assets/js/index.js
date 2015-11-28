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
            for (var i = 0; i <= 7; i++) {
                if (typeof hash[i] === 'undefined') {
                    $('td.playlist-name:eq(' + i + ')').text('N/A');
                    $('td.playlist-popularity:eq(' + i + ')').text('N/A');
                } else {
                    $('td.playlist-name:eq(' + i + ')').text(formatPlaylist(hash[i]['title']));
                    // $('td.playlist-name:eq(' + i + ')').text(hash[i]['title'].replace(/##/g,', ').replace(/\\/g,''));
                    $('td.playlist-popularity:eq(' + i + ')').text(hash[i]['popularity']);
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
        if ($('#enter-song').val() === '') {
            console.log("LET ME KNOW");
            $('#suggested-playlist').text('');
        } else {
            var data = {};
            data['song'] = $('#enter-song').val(); // Gets the song name text
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
                    $('#suggested-playlist').text(formatPlaylist(hash['mostPopular']));
                },
                error: function(data) {
                    // alert('Failed to retrieve Top8 content');
                    console.log('Failed to retrieve suggestPlaylist content');
                    $('#suggested-playlist').text('No Playlist Found');
                }
            });
        }
    }
    /******************** 
        getAutocomplete
    *********************/
    function autocomplete() {
        if ($('#enter-song').val() === '') {
            console.log("LET ME KNOW");
            $('option.song-autocomplete').text('');
        } else {
            var data = {};
            data['song'] = $('#enter-song').val(); // Gets the song name text
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
                            $('option.song-autocomplete:eq(' + i + ')').text('');
                        } else {
                            // Otherwise fill the autocomplete option with the corresponding song name
                            $('option.song-autocomplete:eq(' + i + ')').text(hash[i].replace(/\\/g, ''));
                        }
                    }
                },
                error: function(data) {
                    console.log('Failed to retrieve getAutocomplete content');
                    for (var i = 0; i <= 3; i++) {
                        $('option.song-autocomplete:eq(' + i + ')').text('');
                    }
                }
            });
        }
    }
    /*************************************
     * Helper functions
     **************************************/
    // keypress jquery event handler
    $('#enter-song').bind("keyup", function(e) {
        // if (e.keyCode == 13) { // start post method after enter key pressed
        //     e.preventDefault();
        //     suggestPlaylist();
        // }
        suggestPlaylist();
        autocomplete();
    });
    // Formats the playlist to remove ##'s and \'s
    function formatPlaylist(playlist) {
        playlist = playlist.slice(0, -2).replace(/##/g, ', ').replace(/\\/g, '');
        return playlist;
    }
    // Clicking on an autocomplete suggestion changes the enter song text
    // to the autocomplete suggestion and updates the suggested playlist
    $('option.song-autocomplete').click(function() {
        var enterSong = $('#enter-song');
        enterSong.val($(this).val());
        var e = $.Event('keyup');
        e.which = 13; // 13 = enter key
        enterSong.trigger(e); // Triggers keyup event listener for enter song text input
        enterSong.focus();
    });
});
