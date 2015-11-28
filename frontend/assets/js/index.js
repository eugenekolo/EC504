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
    $('#enter-song').bind("keypress", function(e) { // keypress jquery event handler
        if (e.keyCode == 13) { // start post method after enter key pressed
            e.preventDefault();
            suggestPlaylist();
        }
    });

    function suggestPlaylist() {
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
            }
        });
    }
    /*************************************
    * Helper functions
    **************************************/
    function formatPlaylist(playlist) {
        playlist = playlist.slice(0,-2).replace(/##/g,', ').replace(/\\/g,'');
        return playlist;
    }
});
