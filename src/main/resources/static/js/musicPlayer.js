let isPlaylistOpen = false;

function openPlaylist() {
  const playlistSidebar = document.getElementById("playlistSidebar");

  if (isPlaylistOpen) {
    // If sidebar is open, close it
    closePlaylist();
  } else {
    // If sidebar is closed, open it
    playlistSidebar.style.display = "block";
    playlistSidebar.style.transform = "translateX(100%)";
    playlistSidebar.style.transition = "transform 0.3s ease-out";

    setTimeout(() => {
      playlistSidebar.style.transform = "translateX(0)";
    }, 10);

    isPlaylistOpen = true;

    // Add click handler for the close button (X icon)
    const closeBtn = playlistSidebar.querySelector(".fa-xmark");
    closeBtn.addEventListener("click", () => {
      closePlaylist();
      isPlaylistOpen = false;
    });
  }
}

function closePlaylist() {
  const playlistSidebar = document.getElementById("playlistSidebar");
  playlistSidebar.style.transform = "translateX(100%)";

  setTimeout(() => {
    playlistSidebar.style.display = "none";
  }, 300);

  isPlaylistOpen = false;
}

//Handle Play song button
// Global variables
let currentAudio = null;
let currentPlayingId = null;

function playSong(event, songId) {
  event.preventDefault();
  event.stopPropagation();

  const audioElement = document.getElementById(`audioPlayer_${songId}`);
  const clickedButton = event.currentTarget; // Get the actual button element

  // If clicking the same song's button
  if (currentPlayingId === songId) {
    if (audioElement.paused) {
      audioElement.play();
      clickedButton.innerHTML =
        '<i class="fa-solid fa-pause song-card-play-btn-icon"></i>';
    } else {
      audioElement.pause();
      clickedButton.innerHTML =
        '<i class="fa-solid fa-play song-card-play-btn-icon"></i>';
    }
    return;
  }

  // Pause any currently playing audio
  if (currentAudio) {
    currentAudio.pause();
    // Reset previous button
    const prevButton = document.querySelector(`[data-playing="true"]`);
    if (prevButton) {
      prevButton.innerHTML =
        '<i class="fa-solid fa-play song-card-play-btn-icon"></i>';
      prevButton.removeAttribute("data-playing");
    }
  }

  // Play the new audio
  currentAudio = audioElement;
  currentPlayingId = songId;
  clickedButton.setAttribute("data-playing", "true");
  audioElement.currentTime = 0;
  audioElement
    .play()
    .then(() => {
      clickedButton.innerHTML =
        '<i class="fa-solid fa-pause song-card-play-btn-icon"></i>';
    })
    .catch((error) => {
      console.error("Playback failed:", error);
      clickedButton.innerHTML =
        '<i class="fa-solid fa-play song-card-play-btn-icon"></i>';
      clickedButton.removeAttribute("data-playing");
      currentPlayingId = null;
    });

  // When song ends
  audioElement.onended = () => {
    clickedButton.innerHTML =
      '<i class="fa-solid fa-play song-card-play-btn-icon"></i>';
    clickedButton.removeAttribute("data-playing");
    currentPlayingId = null;
    currentAudio = null;
  };
}

// Prevent onclick in <a> tag and playing song
// function playSong(event) {
//   event.preventDefault();
//   event.stopPropagation();

//   //if user logged in then play music
//   //esle user not loggin then display guest pupup
//   openGuestPopup();
//   // alert("Playing song...");
// }
