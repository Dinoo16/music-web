// Global variables
let currentPopupType = null; // 'create' or 'edit'
let currentPlaylistId = null;

// Show popup (generic function)
function showPopup(type = "create", playlistId = null) {
  currentPopupType = type;
  currentPlaylistId = playlistId;

  // Set form action based on popup type
  const form = document.getElementById("playlistForm");
  form.action = type === "create" ? "/playlist/add" : "/playlist/update";

  // If editing, you might want to load playlist data here
  if (type === "edit" && playlistId) {
    // You could add code to pre-fill the form with existing data
  }

  // Show the popup
  document.querySelector(".overlay-popup").style.display = "block";
  document.querySelector(".popup").style.display = "flex";
}

// Close popup (generic function)
function closePopup() {
  document.querySelector(".overlay-popup").style.display = "none";
  document.querySelector(".popup").style.display = "none";

  // Reset form if needed
  if (currentPopupType === "create") {
    document.getElementById("playlistForm").reset();
  }

  currentPopupType = null;
  currentPlaylistId = null;
}

// Specific function to show create playlist popup
function showCreatePopup() {
  showPopup("create");
}

// Specific function to show edit playlist popup
function showEditPopup(playlistId) {
  showPopup("edit", playlistId);
}

// Handle form submission
function handlePlaylistSubmit(event) {
  event.preventDefault();
  const form = event.target;

  if (currentPopupType === "edit" && currentPlaylistId) {
    // Add playlist ID to form data if editing
    const idInput = document.createElement("input");
    idInput.type = "hidden";
    idInput.name = "playlistID";
    idInput.value = currentPlaylistId;
    form.appendChild(idInput);
  }

  form.submit();
}

// function saveEditPlaylist() {
//   document.getElementsByClassName("roundedButton").submit();
// }

// Initialize form submission handler
document.addEventListener("DOMContentLoaded", function () {
  const form = document.getElementById("playlistForm");
  if (form) {
    form.addEventListener("submit", handlePlaylistSubmit);
  }
});

// Guest popup functions
function openGuestPopup() {
  document.querySelector(".guestPopup-overlay").style.display = "block";
}

function closeGuestPopup() {
  document.querySelector(".guestPopup-overlay").style.display = "none";
}
