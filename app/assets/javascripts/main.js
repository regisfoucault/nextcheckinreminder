$("#btn-search").click(function(e){
  var token = $("#search-input").attr("data-token");
  var date = $("#search-input").attr("data-date");
  var searchInput = $("#search-input").val();
  var place = $(".custom-input-place").val();
  $(".output").html('<i class="icon-spinner icon-spin"></i> Asking Foursquare ... ');
  $.ajax({
    type: "GET",
    url: "https://api.foursquare.com/v2/venues/search?near=" + place + "&limit=20&query=" + searchInput + "&oauth_token=" + token + "&v=" + date,
    success: function(s) {
      console.log("success");
      console.log(s.response.venues);

      var html = "<ul>";
      _.each(s.response.venues, function(venue){
        html += '<li data-id="' + venue.id + '">';
          html += '<div class="row-fluid">';
            html += '<div class="span3">';
              html += '<a href="' + venue.canonicalUrl + '">' + venue.name + '</a>';
            html += '</div>';
            html += '<div class="span6">' + venue.location.address + '</div>';
            html += '<div class="span3">' + venue.location.city + '</div>';
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
    },
    error: function(e) {
      console.log(e);
    }
  })
});

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

