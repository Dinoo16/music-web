// Prevent onclick in <a> tag
function playSong(event) {
  event.preventDefault();
  event.stopPropagation();
  alert("Playing song...");
}

//

