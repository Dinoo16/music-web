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

// Global variables
let currentAudio = null;
let currentPlayingId = null;
//handle playsong button sync with playsong in music player
function playSong(event, songId, title, artists, coverImagePath) {
  event.preventDefault();
  event.stopPropagation();

  const audioElement = document.getElementById(`audioPlayer_${songId}`);
  const clickedButton = event.currentTarget;

  // If clicking the same song's button
  if (currentPlayingId === songId) {
    if (audioElement.paused) {
      audioElement.play();
      clickedButton.innerHTML =
        '<i class="fa-solid fa-pause song-card-play-btn-icon"></i>';
      updateBottomPlayer(songId, title, artists, coverImagePath, true);
    } else {
      audioElement.pause();
      clickedButton.innerHTML =
        '<i class="fa-solid fa-play song-card-play-btn-icon"></i>';
      updateBottomPlayPauseIcon(false);
    }
    return;
  }

  // Pause any currently playing audio
  if (currentAudio && currentAudio !== audioElement) {
    currentAudio.pause();
    const prevButton = document.querySelector('[data-playing="true"]');
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
      updateBottomPlayer(songId, title, artists, coverImagePath, true);
    })
    .catch((error) => {
      console.error("Playback failed:", error);
      clickedButton.innerHTML =
        '<i class="fa-solid fa-play song-card-play-btn-icon"></i>';
      clickedButton.removeAttribute("data-playing");
      currentPlayingId = null;
      updateBottomPlayPauseIcon(false);
    });

  // When song ends
  audioElement.onended = () => {
    clickedButton.innerHTML =
      '<i class="fa-solid fa-play song-card-play-btn-icon"></i>';
    clickedButton.removeAttribute("data-playing");
    updateBottomPlayPauseIcon(false);
    currentAudio = null;
    currentPlayingId = null;
  };
}

// Update bottom music player
function updateBottomPlayer(songId, title, artists, coverImagePath, isPlaying) {
  document.querySelector(".music-title").textContent = title;
  document.querySelector(".music-artist").textContent = artists;
  document.querySelector(".music-cover").src = coverImagePath;
  updateBottomPlayPauseIcon(isPlaying);
}

function updateBottomPlayPauseIcon(isPlaying) {
  const icon = document.querySelector(".play-pause-btn i");
  if (icon) {
    icon.classList.remove("fa-play", "fa-pause");
    icon.classList.add(isPlaying ? "fa-pause" : "fa-play");
  }
}

// Handle bottom player play/pause
function toggleBottomPlayer() {
  if (!currentAudio) return;

  if (currentAudio.paused) {
    currentAudio.play();
    updateBottomPlayPauseIcon(true);
    updatePlayButtonInCard(currentSongId, true);
  } else {
    currentAudio.pause();
    updateBottomPlayPauseIcon(false);
    updatePlayButtonInCard(currentSongId, false);
  }
}

function updatePlayButtonInCard(songId, isPlaying) {
  const button = document.querySelector(
    `.song-card-play-btn[onclick*="'${songId}'"] i`
  );
  if (button) {
    button.classList.replace(
      isPlaying ? "fa-play" : "fa-pause",
      isPlaying ? "fa-pause" : "fa-play"
    );
  }
}
