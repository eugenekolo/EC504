
/*************************************
* Event listeners
**************************************/

// TODO(eugenek): Add validation.
/* On unploadFileBtn click, upload the specified file to the backend */
$("#uploadFileBtn").click(function () {
  var file = $('#chooseFileBtn').prop('files')[0]; // Only one file

  // You got me so high, so high I cannot feel the fire. And you keep telling me that you'll be sweet.
  // As long as I don't break these... promises...
  var data = {}; // 
  var promise = promiseFileContents(file, data);

  $.when(promise).then(function (contents) {
      $.ajax({
          type: 'POST',
          url: '/api/addPlaylists',
          data: JSON.stringify(data),
          dataType: "text",
          contentType: 'application/json',
          processData: false,
          success: function (data) {
            // TODO(eugenek): Add here.
          },
          error: function (data) {
            // TODO(eugenek): Add here.
          }
      });
  });

});

$("#uploadListBtn").click(function () {

  var data = {};
  $.ajax({
      type: 'POST',
      url: '/api/addPlaylist',
      data: JSON.stringify(data),
      dataType: "text",
      contentType: 'application/json',
      processData: false,
      success: function (data) {
        // TODO(eugenek): Add here.
      },
      error: function (data) {
        // TODO(eugenek): Add here.
      }
  });
}

/* Adds the specified song to the song list table */
$("#addListBtn").click(function () {
    var songRow = songListTable.insertRow(-1); // Add to bottom
    var songTitleCell = songRow.insertCell(0);
    var songAuthorCell = songRow.insertCell(1);
    songTitleCell.innerHTML = "";
    songAuthorCell.innerHTML = "";
});


/* #enter-song key press listener */
/* In #enter-song on DOWN_KEY jump to the autoocomplete suggestions */
/* In #enter-song on typeeable characters perform autocomplete */
/* On untypeable characters, do nothing */
$('#enter-song').bind("keyup", function(e) {
    switch (event.keyCode) {
      case 37: // Left
      case 38: // Up
      case 39: // Right
          break;
      
      // TODO(eugenek): Get this working.
      case 40: // Down
          $('#enter-song').blur();
          $("select:first").focus();
          $('.song-autocomplete:eq(' + 0 + ')').prop('selected', true);
          for (var i = 1; i <= 3; i++) {
              $('.song-autocomplete:eq(' + i + ')').prop('selected', false);
          }
          $('#suggested-playlist').prop('selected', false);

      default:
          autocomplete();
    }

});

/*************************************
* Logic functions
*************************************/
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
function promiseFileContents(file, dst) {
  var promise = $.Deferred(function(dfd) {
      var reader = new FileReader();  
      reader.onload = function(event) {  
          var text = event.target.result;
          dst[file.name] = text;
          dfd.resolve(text);
      };
      reader.readAsText(file, "UTF-8");
  }).promise();

  return promise;
};
