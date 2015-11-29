$(function() {

    /*************************************
    * On page load
    *************************************/

    /*************** 
        getTop8 
    ***************/
    /* On page load GET /api/getTop8 */
    /* Update the Top 8 Table with the results */
    $.ajax({
        type: 'GET',
        url: '/api/getTop8',
        data: 'json',
        datatype: 'application/json',
        success: function(data) {
            var hash = JSON.parse(data);

            for (var i = 0; i <= 7; i++) {
                // TODO(eugenek): Make this dynamic growth instead?
                if (typeof hash[i] === 'undefined') {
                    $('td.playlist-name:eq(' + i + ')').text('N/A');
                    $('td.playlist-popularity:eq(' + i + ')').text('N/A');
                } else {
                    var popularity = hash[i]['popularity'];
                    var songList = hash[i]['songList'];

                    /* Generate the playlist html to insert into the table */
                    var playlistHtml = "";
                    for (var j in songList) {
                        var title = songList[j]['title'];
                        var author = songList[j]['author'];
                        playlistHtml += "<b>"+ title + "</b>" + " by " + author + ", " 
                    }
                    playlistHtml.slice(0, -2); // Remove trailing comma

                    /* Update the table */
                    $('td.playlist-name:eq(' + i + ')').html(playlistHtml);
                    $('td.playlist-popularity:eq(' + i + ')').html(popularity);
                }
            }
        },
        error: function(data) {
            console.log('Failed to retrieve Top8 content');
        }
    });


    /*************************************
    * Logic Functions
    *************************************/

    /*********************
        suggestPlaylist
    *********************/
    function suggestPlaylist() {
        var parseTitle = $('#enter-song').val().replace(/\//g, '\\\/').split('#');
        var title = parseTitle[0];
        if (title === '') {
            console.log("LET ME KNOW");            
            removeTableRows();
            var author = $('#enter-song').val().split('#').pop();
            if (!(author === '')) {
                $('.song-suggest').text('No playlist found');
                $('.author-suggest').text('N/A');
            } else {
                $('.song-suggest').text('');
                $('.author-suggest').text('');
            }
            
        } else {
            var data = {};
            var author = $('#enter-song').val().split('#').pop();
            // Gets the song name text
            data['song'] = {
                'title': title,
                'author': author
            };

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
                    // Remove extra row create in above loop
                    $('.playlist-row:last').remove();
                },
                error: function(data) {
                    // alert('Failed to retrieve Top8 content');
                    console.log('Failed to retrieve suggestPlaylist content');
                    removeTableRows();
                    $('.song-suggest').text('No Playlist Found');
                    $('.author-suggest').text('N/A');
                }
            });
        }
    }

    /******************** 
        getAutocomplete
    *********************/
    /* Send a prefix and get autocomplete entries */
    /* Update the autocomplete table with the results */
    function autocomplete() {
        var parseTitle = $('#enter-song').val().replace(/\\/g, '\\/').split('#');
        var prefix = parseTitle[0];

        /* Send a prefix and get autocomplete entries */
        var data = {};
        data['song'] = prefix;

        $.ajax({
            type: 'POST',
            url: '/api/getAutocomplete',
            data: JSON.stringify(data),
            dataType: "text",
            contentType: 'application/json',
            success: function(data) {
                var hash = JSON.parse(data);

                /* Clear the table */
                for (var i = 0; i < 4; i++) {
                    $('.song-autocomplete').text('');
                    $('.author-autocomplete').text('');
                }

                /* Update the table */
                for (var i = 0; i < Object.keys(hash).length; i++) {
                    var title = hash[i]['title'];
                    var author = hash[i]['author'];
                    $('.song-autocomplete:eq(' + i + ')').text(title.replace(/\\/g, ''));
                    $('.author-autocomplete:eq(' + i + ')').text(author.replace(/\\/g, ''));
                }
            },
            error: function(data) {
                console.log('Failed to retrieve getAutocomplete content');
            }
        });
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
        // Hack to remove all rows except the first
        $('.playlist-row:first').attr('class', 'temp'); 
        $('.playlist-row').remove();
        $('.playlist-row:first').attr('class', 'playlist-row');
    }
    /*************************************
     * Event Listeners
     **************************************/

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
