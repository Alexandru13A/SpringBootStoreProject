$(document).ready(function () {
  customizeDropDownMenu();
});

function customizeDropDownMenu() {
  $(".navbar .dropdown").hover(
    function () {
      $(this).find('.dropdown-menu').first().stop(true, true).delay(150).slideDown();
    },
    function () {
      $(this).find('.dropdown-menu').first().stop(true, true).delay(100).slideUp();
    });




  $(".dropdown > a").click(function () {
    location.href = this.href
  });

  const toggleBtn = document.querySelector('.toggle_btn')
  const toggleBtnIcon = document.querySelector('.toggle_btn i')
  const dropDownMenu = document.querySelector('.dropdown_menu')

  toggleBtn.onclick = function () {
    dropDownMenu.classList.toggle('open')
    const isOpen = dropDownMenu.classList.contains('open')

    toggleBtnIcon.classList = isOpen
      ? 'fa-solid fa-xmark' : 'fa-solid fa-bars'
  }
}