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

function playSong(event, songId) {
  event.preventDefault();

  // Get the song card that triggered the event
  const songCard = event.target.closest(".song-card");

  // Extract song data from the DOM
  const songName = songCard.querySelector(".song-card-name").textContent;
  const artistName = songCard.querySelector(".song-card-artist").textContent;
  const imagePath = songCard.querySelector("img").src;

  // Update player state
  playerState.currentSong = {
    id: songId,
    name: songName,
    artist: artistName,
    image: imagePath,
  };
  playerState.isPlaying = true;

  // Update the music player UI
  updateMusicPlayer(songName, artistName, imagePath);

  // Stop any currently playing audio
  document.querySelectorAll("audio").forEach((audio) => {
    audio.pause();
  });

  // Play the selected audio
  const audioPlayer = document.getElementById(`audioPlayer_${songId}`);
  audioPlayer.currentTime = 0;
  audioPlayer.play();

  // Change play/pause icon
  document.querySelector(".music-player-controls .play-pause-btn i").className =
    "fa-solid fa-pause";

  // Update duration display when metadata is loaded
  audioPlayer.onloadedmetadata = function () {
    document.querySelector(".total-duration").textContent = formatTime(
      audioPlayer.duration
    );
  };
}

//sync data 
function updateMusicPlayer(songName, artistName, imagePath) {
  document.getElementById("currentSongTitle").textContent = songName;
  document.getElementById("currentSongArtist").textContent = artistName;
  document.getElementById("currentSongCover").src = imagePath;
}

// Global player state
let playerState = {
  isPlaying: false,
  currentSong: null,
  queue: [],
};

function toggleBottomPlayer() {
  const player = document.querySelector(".music-player");
  playerState.isPlaying = !playerState.isPlaying;

  const playPauseBtn = document.querySelector(
    ".music-player-controls .play-pause-btn i"
  );
  playPauseBtn.className = playerState.isPlaying
    ? "fa-solid fa-pause"
    : "fa-solid fa-play";

  if (playerState.currentSong) {
    const audioPlayer = document.getElementById(
      `audioPlayer_${playerState.currentSong.id}`
    );
    if (playerState.isPlaying) {
      audioPlayer.play();
    } else {
      audioPlayer.pause();
    }
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
