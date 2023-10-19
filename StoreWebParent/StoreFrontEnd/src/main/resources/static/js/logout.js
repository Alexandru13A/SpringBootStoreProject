$(document).ready(function() {

  $("#logoutLinkHeader").on("click", function(e) {
    e.preventDefault(); 
    var logoutForm = document.querySelector('form[name="logoutForm"]');
    if (logoutForm) {
      logoutForm.submit(); 
    }
  });


  $("#logoutLinkDropdown").on("click", function(e) {
    e.preventDefault(); 
    var logoutForm = document.querySelector('form[name="logoutForm"]');
    if (logoutForm) {
      logoutForm.submit(); 
    }
  });
});





