/*************************************
* On page load
*************************************/
var mSongListSize = 0;

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
});

/* Adds the specified song to the song list table */
$("#addListBtn").click(function () {
  $('.autocomplete-row').trigger('click');
});

/* Click handlers for navigating the autocomplete table */
$('#goto_prev').click(gotoPrevious);
$('#goto_next').click(gotoNext);


/* Enter song key press listener */
$('#enter-song').bind("keyup", function(e) {
    switch (event.keyCode) {
      case 37: // Left
      case 39: // Right
          break;

      case 38: // Up
          gotoPrevious();
          break;

      // TODO(eugenek): Get this working.
      case 40: // Down
          gotoNext();
          break;

      case 13: // Enter

      default:
          autocomplete();
          break;
    }
});


/* */
//$('.autocomplete-row').click(function() {
//    var title = $(this).children('.song-autocomplete').text();
//    var author = $(this).children('.author-autocomplete').text();
//
//    addSongToList(title, author);
//});


/*************************************
* Logic functions
*************************************/
/* Send a prefix and get autocomplete entries */
/* Update the autocomplete table with the results */
function autocomplete() {
    //TODO(eugenek): Watch out for empty text
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
                $('.song-autocomplete:eq(' + i + ')').text(title.replace(/\\/g, ''));
                $('.author-autocomplete:eq(' + i + ')').text(author.replace(/\\/g, ''));
            }
        },
        error: function(data) {
            console.log('Failed to retrieve getAutocomplete content');
        }
    });
}

function addSongToList(title, author) {
    /* Gotta grow the table */
    if (mSongListSize >= 4) {
      var songRow = songListTable.insertRow(); // Add to bottom
      var songTitleCell = songRow.insertCell(0);
      var songAuthorCell = songRow.insertCell(1);
    } else {
      var index = mSongListSize;
    }

    /* Fix up the HTML */
    $('#songListTable tbody tr td').attr('class', ''); // remove all previous classes
    $('#songListTable tbody tr').attr('class', 'song-row');
    $('#songListTable tbody tr td:even').attr('class', 'song-title');
    $('#songListTable tbody tr td:odd').attr('class', 'song-author');

    /* Add the song to the table */
    $('.song-title:eq(' + index + ')').text(title);
    $('.song-author:eq(' + index + ')').text(author);
    mSongListSize += 1;
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
}
