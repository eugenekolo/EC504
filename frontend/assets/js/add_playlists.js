/*************************************
* On page load
*************************************/
var mSongListSize = 0;

/*************************************
* Event listeners
**************************************/

/* On unploadFileBtn click, upload the specified file to the backend */
$("#uploadFileBtn").click(function () {
  var file = $('#chooseFileBtn').prop('files')[0]; // Only one file

  // You got me so high, so high I cannot feel the fire. And you keep telling me that you'll be sweet.
  // As long as I don't break these... promises...
  var data = {}; // 
  var promise = promiseFileContents(file, data);

  

  $.when(promise).then(function (contents) {
      if (contents.split("\n").length > 128+1) {
        alert("Warning: Only adding the first 128 playlists from the file");
      }

      $.ajax({
          type: 'POST',
          url: '/api/addPlaylists',
          data: JSON.stringify(data),
          dataType: "text",
          contentType: 'application/json',
          processData: false,
          success: function (data) {
          },
          error: function (data) {
          }
      });
  });

});

$("#uploadListBtn").click(function () {
  var data = {};
  var popularity = $("#uploadPopularity").val();
  var songList = $('#songListTable tbody tr');

  /* Transform the songListTable to a list of song maps */
  var songArray = []
  for (var i = 0; i < mSongListSize; i++) {
    var title = $(songList[i]).children('.song-title').text();
    var author = $(songList[i]).children('.song-author').text();
    var song = {'title':title, 'author':author};
    console.log(title, author);
    songArray.push(song);
  }

  data['songList'] = songArray;
  data['popularity'] = popularity;
  $.ajax({
      type: 'POST',
      url: '/api/addPlaylist',
      data: JSON.stringify(data),
      dataType: "text",
      contentType: 'application/json',
      processData: false,
      success: function (data) {
      },
      error: function (data) {
      }
  });
});

/* Add the entire autocomplete table to the song list */
$("#addListBtn").click(function () {
  $('.autocomplete-row').trigger('click');
});

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

$("#enter-song").keydown(function(e){
    if(e.which == 38 || e.which == 40) {
        e.preventDefault();
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
    
    highlight(tr);
    addSongToList(title, author);
});


/*************************************
* Logic functions
*************************************/
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

function addSongToList(title, author) {
    /* Gotta grow the table */
    if (mSongListSize >= 4) {
      var songRow = songListTable.insertRow(); // Add to bottom
      var songTitleCell = songRow.insertCell(0);
      var songAuthorCell = songRow.insertCell(1);
    }


    /* Fix up the HTML */
    $('#songListTable tbody tr td').attr('class', ''); // remove all previous classes
    $('#songListTable tbody tr').attr('class', 'song-row');
    $('#songListTable tbody tr td:even').attr('class', 'song-title');
    $('#songListTable tbody tr td:odd').attr('class', 'song-author');

    /* Add the song to the table */
    var index = mSongListSize;
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
