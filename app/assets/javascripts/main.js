$("#btn-search").click(function(e){
  var token = $("#search-input").attr("data-token");
  var date = $("#search-input").attr("data-date");
  var searchInput = $("#search-input").val();
  var place = $(".custom-input-place").val();
  if (place == "") {
    $(".custom-input-place").addClass("error");
    $(".custom-input-place").val("Where ?");
  } else {
    $(".output").html('<i class="icon-spinner icon-spin"></i> Asking Foursquare ... ');
    $.ajax({
      type: "GET",
      url: "https://api.foursquare.com/v2/venues/search?near=" + place + "&limit=20&query=" + searchInput + "&oauth_token=" + token + "&v=" + date,
      success: function(s) {
        if (typeof(s)=="string") {
          s = JSON.parse(s);
        }
        if (s.response.venues.length>0) {
          var html = "<ul>";
          _.each(s.response.venues, function(venue){
            html += '<li data-id="' + venue.id + '">';
              html += '<div class="row-fluid">';
                html += '<div class="span3">';
                  html += '<a href="' + venue.canonicalUrl + '">' + venue.name + '</a>';
                html += '</div>';
                html += '<div class="span6">' + checkIfValue(venue.location.address) + '</br>' + checkIfValue(venue.location.city)  + '</div>';
                html += '<div class="span3">' + '<i class="icon-map-marker"></i> ' + venue.stats.checkinsCount + '</br><i class="icon-heart"></i> ' + venue.likes.count + '</div>';
              html += '</div>';
            html += '</li>';
          })
          html += "</ul>";
          $(".output").html(html);

          $(".output ul li").click(function(e) {
            var elt = $($($(e.target).closest("li"))[0]);
            if (elt.hasClass("selected")) {
              elt.removeClass("selected");
            } else {
              $(".output ul li").removeClass("selected");
              elt.addClass("selected");
            }
          })
        } else {
          $(".output").html("No venues found dude. Just create one !");
        }
      },
      error: function(e) {
        console.log(e);
        $(".output").html("This place does not seem to be on earth !");
        $(".custom-input-place").addClass("error");
      }
    })
  }
});

var checkIfValue = function(elt) {
  console.log(elt);
  if (elt == "" || typeof(elt) == "undefined") {
    return "";
  } else {
    return elt;
  }
}

$(".custom-input-place").focus(function(){
  $(".custom-input-place").removeClass("error");
})


$(".sendTrigger").click(function(e){
  var vid = $($(".selected")[0]).attr("data-id");
  var text = $(".textarea-trigger").val();
  jsRoutes.controllers.Users.sendTrigger().ajax({
    data: {
      vid : vid,
      text : text
    },
    success : function(data) {
      console.log(data);
      $(".success-storage").fadeIn("slow", function(){
        window.setTimeout(function() {
          $(".success-storage").fadeOut("slow");
        }, 2000);
      });
      //$(".success-storage").css("display", "block");
      $("#search-input").val("");
      $(".custom-input-place").val("");
      $(".output").html("");
      $(".textarea-trigger").val("");
    },
    error: function(e) {
      console.log(e);
    }
  });
})

