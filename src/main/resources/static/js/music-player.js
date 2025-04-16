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
/**
 * Universal playSong function that handles both DOM-based and parameter-based calls
 * @param {Event} event - The click event (optional)
 * @param {string} songId - The ID of the song to play
 * @param {string} [songName] - Name of the song (optional if called from song card/list)
 * @param {string} [artistName] - Artist name (optional if called from song card/list)
 * @param {string} [songDuration] - Duration of the song (optional)
 * @param {string} [imagePath] - URL of the song cover image (optional if called from song card/list)
 */
function playSong(
  event,
  songId,
  songName = null,
  artistName = null,
  songDuration = null,
  imagePath = null
) {
  // Prevent default behavior if event exists
  if (event) {
    event.preventDefault();
    event.stopPropagation();
  }

  const audioElement = document.getElementById(`audioPlayer_${songId}`);
  if (!audioElement) {
    console.error(`Audio element not found for song ID: ${songId}`);
    return;
  }

  // If parameters not provided, try to extract from DOM
  const clickedButton = event?.currentTarget;
  if (clickedButton && (!songName || !artistName || !imagePath)) {
    const songCard = clickedButton.closest(".song-card");
    const listSong = clickedButton.closest(".listOfSong-item");

    if (songCard || listSong) {
      const container = songCard || listSong;
      songName =
        songName ||
        container.querySelector(".song-card-name, .listOfSong-song-name")
          ?.textContent;
      artistName =
        artistName ||
        container.querySelector(".song-card-artist, .listOfSong-info-artist")
          ?.textContent;
      songDuration =
        songDuration ||
        container.querySelector(".song-card-duration, .listOfSong-duration")
          ?.textContent;
      imagePath = imagePath || container.querySelector("img")?.src;
    }
  }

  // Validate we have required data
  if (!songName || !artistName || !imagePath) {
    console.error("Missing required song information");
    return;
  }

  // Check if user is logged in
  if (!isLoggedIn) {
    openGuestPopup();
    updateImageGuestPopup(imagePath);
    return;
  }

  // Show music player
  const musicPlayer = document.getElementById("music-player");
  if (musicPlayer) {
    musicPlayer.style.display = "flex";
  }

  // Update global player state
  playerState.currentSong = {
    id: songId,
    name: songName,
    artist: artistName,
    image: imagePath,
  };

  // If same song is clicked again - toggle play/pause
  if (currentPlayingId === songId) {
    if (audioElement.paused) {
      audioElement
        .play()
        .then(() => {
          updateAllPlayerUI(
            songName,
            artistName,
            songDuration,
            imagePath,
            true
          );
        })
        .catch(handlePlaybackError);
    } else {
      audioElement.pause();
      updateAllPlayerUI(songName, artistName, songDuration, imagePath, false);
    }
    return;
  }

  // Pause any previously playing audio
  if (currentAudio && currentAudio !== audioElement) {
    currentAudio.pause();
    updatePlayButtonStates(currentPlayingId, false);
    hasPlayCountBeenUpdated = false;
  }

  // Play new song
  currentAudio = audioElement;
  currentPlayingId = songId;
  audioElement.currentTime = 0;

  audioElement
    .play()
    .then(() => {
      updateAllPlayerUI(songName, artistName, songDuration, imagePath, true);
      updatePlayCount(songId);
      updateRecentlyPlayed(songId);
    })
    .catch(handlePlaybackError);

  // Handle song end
  audioElement.onended = () => {
    resetPlayerState();
  };
}

// Helper Functions

function updateAllPlayerUI(
  songName,
  artistName,
  duration,
  imagePath,
  isPlaying
) {
  updateBottomPlayer(songName, artistName, duration, imagePath, isPlaying);
  updateSidebar(songName, artistName, duration, imagePath, isPlaying);
  updatePlayButtonStates(currentPlayingId, isPlaying);
}

function updatePlayButtonStates(songId, isPlaying) {
  updatePlayButtonInCard(songId, isPlaying);
  updatePlayButtonInSonglist(songId, isPlaying);
}

function updatePlayCount(songId) {
  if (!hasPlayCountBeenUpdated) {
    fetch(`/song/play/${songId}`, { method: "POST" })
      .then(() => (hasPlayCountBeenUpdated = true))
      .catch((err) => console.error("Play count update failed:", err));
  }
}

function updateRecentlyPlayed(songId) {
  fetch(`/recentlyplayed/${songId}`, { method: "POST" }).catch((err) =>
    console.error("Recently played update failed:", err)
  );
}

function handlePlaybackError(error) {
  console.error("Playback failed:", error);
  if (playerState.currentSong) {
    updateAllPlayerUI(
      playerState.currentSong.name,
      playerState.currentSong.artist,
      null,
      playerState.currentSong.image,
      false
    );
  }
  currentPlayingId = null;
}

function resetPlayerState() {
  updateBottomPlayPauseIcon(false);
  updatePlayButtonStates(currentPlayingId, false);
  currentAudio = null;
  currentPlayingId = null;
  hasPlayCountBeenUpdated = false;
}

// Bottom player info update
function updateBottomPlayer(
  songName,
  artistName,
  songDuration,
  imagePath,
  isPlaying
) {
  document.getElementById("currentSongTitle").textContent = songName;
  document.getElementById("currentSongArtist").textContent = artistName;
  document.getElementById("currentSongDuration").textContent = songDuration;
  document.getElementById("currentSongCover").src = imagePath;
  updateBottomPlayPauseIcon(isPlaying);
}

//Playlist sidebar info update
function updateSidebar(
  songName,
  artistName,
  songDuration,
  imagePath,
  isPlaying
) {
  document.getElementById("currentSongTitleSidebar").textContent = songName;
  document.getElementById("currentSongArtistSidebar").textContent = artistName;
  document.getElementById("currentSongDurationSidebar").textContent =
    songDuration;
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
