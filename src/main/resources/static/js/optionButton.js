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
let playlistToDeleteId = null;

function deletePlaylist(playlistId) {
  playlistToDeleteId = playlistId;
  // Show the confirmation popup
  document.querySelector(".confirmPopup-overlay").style.display = "block";
}

// Confirm delete action
function confirmDelete() {
  if (playlistToDeleteId !== null) {
    fetch(`/playlist/delete/${playlistToDeleteId}`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
      },
    })
      .then((response) => {
        if (response.ok) {
          // If successful, redirect to playlist list
          window.location.href = "/playlist/list";
        } else {
          // Handle error
          console.error("Delete failed");
        }
      })
      .catch((error) => {
        console.error("Error:", error);
      })
      .finally(() => {
        // Hide the popup regardless of success/failure
        document.querySelector(".confirmPopup-overlay").style.display = "none";
      });
  }
}

// Cancel
function cancel() {
  document.querySelector(".confirmPopup-overlay").style.display = "none";
}
// Share playlist button function
function sharePlaylist() {
  alert("share playlist");
}
