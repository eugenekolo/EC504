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
            console.log(hash);
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
            data['song'] = $('#enter-song').val().replace(/\\/g,'\\'); // Gets the song name text
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
            $('.song-autocomplete').text('');
        } else {
            var data = {};
            data['song'] = $('#enter-song').val(); // Gets the song name text
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
                        } else {
                            // Otherwise fill the autocomplete option with the corresponding song name
                            $('.song-autocomplete:eq(' + i + ')').text(hash[i].replace(/\\/g, ''));
                        }
                    }
                },
                error: function(data) {
                    console.log('Failed to retrieve getAutocomplete content');
                    for (var i = 0; i <= 3; i++) {
                        $('.song-autocomplete:eq(' + i + ')').text('');
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
        playlist = playlist.slice(0, -2).replace(/##/g, ', ').replace(/\\/g, '');
        return playlist;
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
            $('#suggested-playlist').prop('selected',false);
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
    $('.song-autocomplete').click(function() {
        var enterSong = $('#enter-song');
        enterSong.val($(this).text());
        var e = $.Event('keyup');
        e.which = 13; // 13 = enter key
        enterSong.trigger(e); // Triggers keyup event listener for enter song text input
        enterSong.focus();
    });

});
