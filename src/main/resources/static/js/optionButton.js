function toggleOptionMenu(event) {
  event.stopPropagation();
  let optionMenu = event.currentTarget.nextElementSibling;

  // Close all other option menus
  document.querySelectorAll(".option-menu").forEach((menu) => {
    if (menu !== optionMenu) {
      menu.style.display = "none";
    }
  });
  // Toggle visibility
  if (optionMenu.style.display === "block") {
    optionMenu.style.display = "none";
  } else {
    optionMenu.style.display = "block";
  }
}

// Close option menus when clicking outside
document.addEventListener("click", function () {
  document.querySelectorAll(".option-menu").forEach((menu) => {
    menu.style.display = "none";
  });
});



// Delete playlist button function
function deletePlaylist() {
  alert("delete playlist");
}
// Share playlist button function
function sharePlaylist() {
  alert("share playlist");
}
