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

// Select multiple genres and artists
document.addEventListener("DOMContentLoaded", function () {
  // Initialize with selected values - for both add and edit forms
  const selectedGenres = /*[[${selectedGenreIds}]]*/ [];
  const selectedArtists = /*[[${selectedArtistIds}]]*/ [];
  const selectedSongs = /*[[${selectedSongIds}]]*/ [];

  const initSelections = (type, selectedArray, className) => {
    selectedArray.forEach((id) => {
      const item = document.querySelector(`.${className} li[data-id="${id}"]`);
      if (item) {
        item.classList.add("checked");
      }
    });

    const hiddenInput = document.getElementById(`selected${capitalize(type)}`);
    if (hiddenInput) {
      hiddenInput.value = selectedArray.join(",");
    }
  };

  const capitalize = (str) => str.charAt(0).toUpperCase() + str.slice(1);

  initSelections("genres", selectedGenres, "edit-songlist-genres");
  initSelections("artists", selectedArtists, "edit-songlist-artists");
  initSelections("songs", selectedSongs, "add-song-songs");

  // Initialize dropdown functionality
  const dropdowns = document.querySelectorAll(
    ".add-song-dropdown, .edit-songlist-dropdown"
  );

  dropdowns.forEach((dropdown) => {
    const selectBtn = dropdown.querySelector(".select-btn");
    const listItems = dropdown.querySelectorAll(".list-items .item");
    const btnText = dropdown.querySelector(".btn-text");
    const type = dropdown.dataset.type;

    const hiddenInput = document.getElementById(`selected${capitalize(type)}`);

    if (!selectBtn || !btnText || listItems.length === 0 || !hiddenInput)
      return;

    // Update button text based on initial selection
    const initialSelected = dropdown.querySelectorAll(".list-items .checked");
    btnText.innerText = initialSelected.length
      ? `${initialSelected.length} Selected`
      : `Select ${type}`;

    selectBtn.addEventListener("click", () => {
      dropdown.classList.toggle("open");
    });

    listItems.forEach((item) => {
      item.addEventListener("click", () => {
        item.classList.toggle("checked");

        const checkedItems = dropdown.querySelectorAll(".list-items .checked");
        const selectedIds = [];

        checkedItems.forEach((checkedItem) => {
          const id = checkedItem.getAttribute("data-id");
          if (id) selectedIds.push(id);
        });

        btnText.innerText = selectedIds.length
          ? `${selectedIds.length} Selected`
          : `Select ${type}`;

        hiddenInput.value = selectedIds.join(",");
      });
    });
  });
});

// display not loggin
function openCloseAddToSongCard() {
  if (!isLoggedIn) {
    const tooltipBox = document.getElementById("header__tooltip-box");
    tooltipBox.style.display = "block";
    return;
  }

  const addToPlaylistCard = document.querySelector(
    ".addToPlaylistCard-wrapper"
  );

  if (!addToPlaylistCard) return;

  const isVisible = addToPlaylistCard.style.display === "block";

  addToPlaylistCard.style.display = isVisible ? "none" : "block";
}

// Add song from playlist function
// Add song from playlist function
window.addSong = function (songId, playlistId) {
  const message = document.querySelector(".message-wrapper");

  // Gửi request thêm bài hát
  fetch(`/playlist/songs/add?playlistId=${playlistId}&songId=${songId}`, {
    method: "POST",
    headers: {
      "Content-Type": "application/x-www-form-urlencoded",
      // 'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
    },
  })
    .then((response) => {
      if (response.ok) {
        location.reload(); // reload sẽ làm mất luôn message, có thể bỏ nếu muốn giữ
      } else {
        alert("Failed to add song");
      }
    })
    .catch((error) => {
      console.error("Error:", error);
      alert("Error adding song");
    });
};

// Remove song from playlist function
function removeSong(songId, playlistId) {
  if (!playlistId) {
    // Try to get playlistId from the body data attribute if not provided
    playlistId = document.body.dataset.playlistId;
  }

  if (!playlistId) {
    console.error("Playlist ID not found");
    alert("Error: Could not determine which playlist to remove from");
    return;
  }

  fetch(`/playlist/songs/${playlistId}/remove-song?songId=${songId}`, {
    method: "POST",
    headers: {
      "Content-Type": "application/x-www-form-urlencoded",
    },
  })
    .then((response) => {
      if (response.ok) {
        location.reload();
      } else {
        alert("Failed to remove song from playlist");
      }
    })
    .catch((error) => {
      console.error("Error removing song:", error);
      alert("Error removing song from playlist");
    });
}
