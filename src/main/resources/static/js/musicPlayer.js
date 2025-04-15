let isPlaylistOpen = false;

//Handle open song queue list
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

//Handle close song queue list
function closePlaylist() {
  const playlistSidebar = document.getElementById("playlistSidebar");
  playlistSidebar.style.transform = "translateX(100%)";

  setTimeout(() => {
    playlistSidebar.style.display = "none";
  }, 300);

  isPlaylistOpen = false;
}

let currentAudio = null;
let currentPlayingId = null;
let hasPlayCountBeenUpdated = false;
// Global player state
let playerState = {
  isPlaying: false,
  currentSong: null,
  queue: [],
};
// Handle play/pause when clicking song card play button
function playSong(event, songId) {
  event.preventDefault();
  event.stopPropagation();

  const audioElement = document.getElementById(`audioPlayer_${songId}`);
  const clickedButton = event.currentTarget;

  // Try to find parent containers of both styles
  const songCard = clickedButton.closest(".song-card");
  const listSong = clickedButton.closest(".listOfSong-item");

  // Extract data from whichever exists
  const songName = songCard
    ? songCard.querySelector(".song-card-name").textContent
    : listSong.querySelector(".listOfSong-song-name").textContent;

  const artistName = songCard
    ? songCard.querySelector(".song-card-artist").textContent
    : listSong.querySelector(".listOfSong-info-artist").textContent;

  const imagePath = songCard
    ? songCard.querySelector("img").src
    : listSong.querySelector("img").src;

  // Check if user is logged in
  // Show guest popup instead of playing
  if (!isLoggedIn) {
    openGuestPopup();
    updateImageGuestPopup(imagePath);
    return; // Exit the function early
  }

  const musicPlayer = document.getElementById("music-player");
  musicPlayer.style.display = "flex";
  // Update global player state
  playerState.currentSong = {
    id: songId,
    name: songName,
    artist: artistName,
    image: imagePath,
  };

  // If same song is clicked again
  if (currentPlayingId === songId) {
    if (audioElement.paused) {
      audioElement.play();
      updateBottomPlayer(songName, artistName, imagePath, true);
      updateSidebar(songName, artistName, imagePath, true);
      updatePlayButtonInCard(songId, true);
      updatePlayButtonInSonglist(songId, true);
    } else {
      audioElement.pause();
      updateBottomPlayer(songName, artistName, imagePath, false);
      updateSidebar(songName, artistName, imagePath, false);
      updatePlayButtonInCard(songId, false);
      updatePlayButtonInSonglist(songId, false);
    }
    return;
  }

  // Pause any previously playing audio
  if (currentAudio && currentAudio !== audioElement) {
    currentAudio.pause();
    updatePlayButtonInCard(currentPlayingId, false);
    updatePlayButtonInSonglist(currentPlayingId, false);
    // Reset play count tracking when switching songs
    hasPlayCountBeenUpdated = false;
  }

  // Play new song
  currentAudio = audioElement;
  currentPlayingId = songId;
  audioElement.currentTime = 0;

  audioElement
    .play()
    .then(() => {
      updateBottomPlayer(songName, artistName, imagePath, true);
      updateSidebar(songName, artistName, imagePath, true);
      updatePlayButtonInCard(songId, true);
      updatePlayButtonInSonglist(songId, true);
      // Only send play count update if it hasn't been done yet for this song
      if (!hasPlayCountBeenUpdated) {
        fetch(`/song/play/${songId}`, {
          method: "POST",
        })
          .then(() => {
            hasPlayCountBeenUpdated = true;
          })
          .catch((err) => console.error("Failed to update play count:", err));
      }
      fetch(`/recentlyplayed/${songId}`, {
        method: "POST",
      })
        .then(() => {
          console.log("Recently played song updated.");
        })
        .catch((err) => {
          console.error("Failed to update recently played songs:", err);
        });
    })
    .catch((error) => {
      console.error("Playback failed:", error);
      updateBottomPlayer(songName, artistName, imagePath, false);
      updateSidebar(songName, artistName, imagePath, false);
      updatePlayButtonInCard(songId, false);
      updatePlayButtonInSonglist(songId, false);
      currentPlayingId = null;
    });

  // Reset on song end
  audioElement.onended = () => {
    updateBottomPlayPauseIcon(false);
    updatePlayButtonInCard(songId, false);
    updatePlayButtonInSonglist(songId, false);
    currentAudio = null;
    currentPlayingId = null;
    hasPlayCountBeenUpdated = false;
  };
}

// Bottom player info update
function updateBottomPlayer(songName, artistName, imagePath, isPlaying) {
  document.getElementById("currentSongTitle").textContent = songName;
  document.getElementById("currentSongArtist").textContent = artistName;
  document.getElementById("currentSongCover").src = imagePath;
  updateBottomPlayPauseIcon(isPlaying);
}

//Playlist sidebar info update
function updateSidebar(songName, artistName, imagePath, isPlaying) {
  document.getElementById("currentSongTitleSidebar").textContent = songName;
  document.getElementById("currentSongArtistSidebar").textContent = artistName;
  document.getElementById("currentSongCoverSidebar").src = imagePath;
  updateBottomPlayPauseIcon(isPlaying);
}

function updateImageGuestPopup(imagePath) {
  const img = document.getElementById("image-background");
  if (img) {
    img.src = imagePath;
  }
}

// Update bottom player play/pause icon
function updateBottomPlayPauseIcon(isPlaying) {
  const icon = document.querySelector(".play-pause-btn i");
  if (icon) {
    icon.classList.remove("fa-play", "fa-pause");
    icon.classList.add(isPlaying ? "fa-pause" : "fa-play");
  }
}

// Toggle bottom player play/pause
function toggleBottomPlayer() {
  if (!currentAudio) return;

  if (currentAudio.paused) {
    currentAudio.play();
    updateBottomPlayPauseIcon(true);
    updatePlayButtonInCard(currentPlayingId, true);
    updatePlayButtonInSonglist(currentPlayingId, true);
  } else {
    currentAudio.pause();
    updateBottomPlayPauseIcon(false);
    updatePlayButtonInCard(currentPlayingId, false);
    updatePlayButtonInSonglist(currentPlayingId, false);
  }
}

// Update song card button icon
function updatePlayButtonInCard(songId, isPlaying) {
  const buttonIcon = document.querySelector(
    `.song-card-play-btn[onclick*="'${songId}'"] i`
  );
  if (buttonIcon) {
    buttonIcon.classList.remove("fa-play", "fa-pause");
    buttonIcon.classList.add(isPlaying ? "fa-pause" : "fa-play");
  }
}

//Update list song button icon
function updatePlayButtonInSonglist(songId, isPlaying) {
  const sidebarBtn = document.querySelector(
    `.listOfSong-play-pause-btn[data-song-id="sidebar"] i`
  );
  if (sidebarBtn) {
    sidebarBtn.classList.remove("fa-play", "fa-pause");
    sidebarBtn.classList.add(isPlaying ? "fa-pause" : "fa-play");
  }

  const buttonIcon = document.querySelector(
    `.listOfSong-play-pause-btn[onclick*="'${songId}'"] i`
  );
  if (buttonIcon) {
    buttonIcon.classList.remove("fa-play", "fa-pause");
    buttonIcon.classList.add(isPlaying ? "fa-pause" : "fa-play");
  }
}

// Initialize audio event listeners
document.addEventListener("DOMContentLoaded", function () {
  // Update progress bar as song plays
  document.querySelectorAll("audio").forEach((audio) => {
    audio.addEventListener("timeupdate", function () {
      if (this.id === `audioPlayer_${playerState.currentSong?.id}`) {
        const progress = (this.currentTime / this.duration) * 100;
        document.querySelector(".seek-bar").value = progress;

        // Update current time display
        document.querySelector(".current-time").textContent = formatTime(
          this.currentTime
        );
      }
    });
  });
});

function formatTime(seconds) {
  const mins = Math.floor(seconds / 60);
  const secs = Math.floor(seconds % 60);
  return `${mins}:${secs < 10 ? "0" : ""}${secs}`;
}

//Handle next song

//Handle backward song

//Handle click or drag on the duration bar
