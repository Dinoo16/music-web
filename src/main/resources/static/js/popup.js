// Show create playlist pop up
function showPopup() {
  document.querySelector(".overlay-popup").style.display = "block";
  document.querySelector(".popup").style.display = "flex";
}
// Close create playlist popup
function closePopup() {
  document.querySelector(".overlay-popup").style.display = "none";
  document.querySelector(".popup").style.display = "none";
}

// Show edit playlist pop up
function editPlaylist() {
  document.querySelector(".overlay-popup").style.display = "block";
  document.querySelector(".popup").style.display = "flex";
}
// Close edit playlist popup
function closePopup() {
  document.querySelector(".overlay-popup").style.display = "none";
  document.querySelector(".popup").style.display = "none";
}

//open guest popup
function openGuestPopup() {
  document.querySelector(".guestPopup-overlay").style.display = "block";
}

//close guest popup
function closeGuestPopup() {
  document.querySelector(".guestPopup-overlay").style.display = "none";
}
