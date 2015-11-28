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
            for (var i = 1; i <= 8; i++) {
                // If there is an undefined element in the data hash or if an element is
                // equal to '}' assign N/A to represent the absense of a playlist 
                if ((typeof data[i] === 'undefined') || (data[i] === '}')) {
                    $('td.playlist-name:eq(' + (i - 1) + ')').text('N/A');
                    $('td.playlist-popularity:eq(' + (i - 1) + ')').text('N/A');
                } else {
                    $('td.playlist-name:eq(' + (i - 1) + ')').text(data[i]['title']);
                    $('td.playlist-popularity:eq(' + (i - 1) + ')').text(data[i]['popularity']);
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
        data = $('#enter-song').val();
        $.ajax({
            type: 'POST',
            url: '/api/suggestPlaylist',
            data: JSON.stringify(data),
            dataType: "text",
            contentType: 'application/json',
            success: function(data) {
                // On success below jquery fills rows of Top8 table with data retrieved from /api/getTop8
                console.log('Success!');
            },
            error: function(data) {
                // alert('Failed to retrieve Top8 content');
                console.log('Failed to retrieve suggestPlaylist content');
            }
        });
    }
    // $('#enter-song').submit(function(event) {
    //     var str = $('#enter-song').val();
    //     $.ajax({
    //         type: 'POST',
    //         url: '/api/suggestPlaylist',
    //         data: {"song":str},
    //         datatype: 'application/json',
    //         success: function(data) {
    //             // On success below jquery fills rows of Top8 table with data retrieved from /api/getTop8
    //             console.log('Success!');
    //         },
    //         error: function(data) {
    //             // alert('Failed to retrieve Top8 content');
    //             console.log('Failed to retrieve Top8 content');
    //         }
    //     });
    // });
});
