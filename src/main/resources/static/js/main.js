// Prevent onclick in <a> tag and playing song
function playSong(event) {
  event.preventDefault();
  event.stopPropagation();
  alert("Playing song...");
}

// retrieve the function name dynamically
function handleClick(button) {
  let action = button.getAttribute("data-action");
  if (action === "addSong()") {
    alert("addsong");
  }
}

//Toggle play and pause icon in listSong
function togglePlayPause(button) {
  const playIcon = button.querySelector(".listOfSong-play-btn");
  const pauseIcon = button.querySelector(".listOfSong-pause-btn");

  // Toggle visibility
  if (playIcon.style.display === "none") {
    alert("Pause music");
    playIcon.style.display = "block";
    pauseIcon.style.display = "none";
  } else {
    alert("Play music");
    playIcon.style.display = "none";
    pauseIcon.style.display = "block";
  }
}

// Signin and dislay user sidebar with profile header

// Signin and dislay admin sidebar
