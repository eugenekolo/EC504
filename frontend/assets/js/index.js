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
    function suggestPlaylist(title, author) {
        if (title === '') {
            return;
        }

        var data = {};
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
                var hash = JSON.parse(data);

                /* Clear the suggested playlist */
                /* Fix up the HTML */
                $('#songListTable tbody tr').not(':first').remove();

                /* Add the songs in the suggested playlist */
                for (var i in hash['songList']) {
                    $('.song-title:last').text(formatPlaylist(hash['songList'][i]['title']));
                    $('.song-author:last').text(formatPlaylist(hash['songList'][i]['author']));
                    
                    /* Add another row */
                    var songRow = songListTable.insertRow(); // Add to bottom
                    var songTitleCell = songRow.insertCell(0);
                    var songAuthorCell = songRow.insertCell(1);
                    $('#songListTable tbody tr td').attr('class', ''); // remove all previous classes
                    $('#songListTable tbody tr').attr('class', 'song-row');
                    $('#songListTable tbody tr td:even').attr('class', 'song-title');
                    $('#songListTable tbody tr td:odd').attr('class', 'song-author');
                }
            },
            error: function(data) {
                console.log('Failed to retrieve suggestPlaylist content');
            }
        });
    }

    /******************** 
        getAutocomplete
    *********************/
    /* Send a prefix and get autocomplete entries */
    /* Update the autocomplete table with the results */
    function autocomplete() {
        var prefix = $('#enter-song').val().replace(/\\/g, '\\/');

        if (prefix == "") {
            $('.song-autocomplete').text('');
            $('.author-autocomplete').text('');
            return;
        }

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
                $('.song-autocomplete').text('');
                $('.author-autocomplete').text('');
                
                /* Update the table */
                for (var i = 0; i < Object.keys(hash).length; i++) {
                    var title = hash[i]['title'];
                    var author = hash[i]['author'];
                    $('.song-autocomplete:eq(' + i + ')').text(title);
                    $('.author-autocomplete:eq(' + i + ')').text(author);
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

    function highlight(trIndex) {
        /* Loop back to the start if at end */
        if( (trIndex+1) > $('#autocompleteTable tbody tr').length) {
            trIndex = 0;
        }

        /* If tr exists, remove other row highlights and highlight the specific row */
        if($('#autocompleteTable tbody tr:eq('+trIndex+')').length > 0) {
            $('#autocompleteTable tbody tr').removeClass('highlight');        
            $('#autocompleteTable tbody tr:eq('+trIndex+')').addClass('highlight');
        }
    }

    function gotoNext(){
        var tr = $("#autocompleteTable tbody").find('.highlight').index();
        highlight(tr+1);
        $("#autocompleteTable").focus();
    }
    
    function gotoPrevious () {
        //TODO(eugenek): Bug ATM where cursor will jump to start of sentence and be left there. Annoying.
        var tr = $("#autocompleteTable tbody").find('.highlight').index();
        highlight(tr-1);
        $("#autocompleteTable").focus();
    }

    /*************************************
     * Event Listeners
     **************************************/

    /* Enter song key press listener */
    $('#enter-song').bind("keyup", function(e) {
        switch (event.keyCode) {
          case 37: // Left
          case 39: // Right
              break;

          case 38: // Up
              gotoPrevious();
              break;

          case 40: // Down
              gotoNext();
              break;

          case 13: // Enter
              var tr = $("#autocompleteTable tbody").find('.highlight');
              tr.trigger('click');
              break;

          default:
              autocomplete();
              break;
            }
    });

    /* When a autocomplete row is clicked, add the song to the song list */
    $('.autocomplete-row').click(function() {
        var tr = $(this).index();
        var title = $(this).children('.song-autocomplete').text();
        var author = $(this).children('.author-autocomplete').text();
        if (title == "" || author == "") {
           return;
        }
        highlight(tr); // Highlights clicked row
        suggestPlaylist(title, author);
    });

});
