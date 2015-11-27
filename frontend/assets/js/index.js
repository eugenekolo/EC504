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
                $('td.playlist-name:eq(' + (i - 1) + ')').text(data[i]['title']);
                $('td.playlist-popularity:eq(' + (i - 1) + ')').text(data[i]['popularity']);
            }
        },
        error: function(data) {
            // alert('Failed to retrieve Top8 content');
            console.log('Failed to retrieve Top8 content');
        }
    });
});
