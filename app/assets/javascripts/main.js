$("#search-input").change(function(e){
  var token = $(e.target).attr("data-token");
  var date = $(e.target).attr("data-date");
  var searchInput = $(e.target).val();
  $.ajax({
    type: "GET",
    url: "https://api.foursquare.com/v2/venues/search?near=nantes&limit=20&query=" + searchInput + "&oauth_token=" + token + "&v=" + date,
    success: function(s) {
      console.log("success");
      console.log(s.response.venues);

      var html = "<ul>";
      _.each(s.response.venues, function(venue){
        html += '<li data-id="' + venue.id + '"">';
        html += '<a href="' + venue.canonicalUrl + '">' + venue.name + '</a></li>';
        //console.log(venue);
      })
      html += "</ul>";
      $(".output").html(html);

      $(".select").click(function(e){
        console.log($($(e.target).parent()[0])[0]);
        $(".selected-zone").html("ok");
      })

      $(".output ul li").click(function(e) {
        if ($(e.target).hasClass("selected")) {
          $(e.target).removeClass("selected");
        } else {
          $(".output ul li").removeClass("selected");
          $(e.target).addClass("selected");
        }

        //$(e.target).css("background-color","red");
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
    },
    error: function(e) {
      console.log(e);
    }
  });
})

