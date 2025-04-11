// Prevent onclick in <a> tag and playing song
function playSong(event) {
  event.preventDefault();
  event.stopPropagation();

  //if user logged in then play music
  //esle user not loggin then display guest pupup
  openGuestPopup();
  // alert("Playing song...");
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

// Select multiple genres and artists
document.addEventListener("DOMContentLoaded", function () {
  // Initialize with selected values - for both add and edit forms
  const selectedGenres = /*[[${selectedGenreIds}]]*/ [];
  const selectedArtists = /*[[${selectedArtistIds}]]*/ [];

  // Initialize genres
  selectedGenres.forEach((id) => {
    const item = document.querySelector(
      `.edit-songlist-genres li[data-id="${id}"]`
    );
    if (item) {
      item.classList.add("checked");
    }
  });

  // Initialize artists
  selectedArtists.forEach((id) => {
    const item = document.querySelector(
      `.edit-songlist-artists li[data-id="${id}"]`
    );
    if (item) {
      item.classList.add("checked");
    }
  });

  // Update the hidden inputs
  document.getElementById("selectedGenres").value = selectedGenres.join(",");
  document.getElementById("selectedArtists").value = selectedArtists.join(",");

  // Initialize dropdown functionality for both add and edit forms
  const dropdowns = document.querySelectorAll(
    ".add-song-dropdown, .edit-songlist-dropdown"
  );

  dropdowns.forEach((dropdown) => {
    const selectBtn = dropdown.querySelector(".select-btn");
    const listItems = dropdown.querySelectorAll(".list-items .item");
    const btnText = dropdown.querySelector(".btn-text");
    const type = dropdown.dataset.type; // "genres" or "artists"

    const hiddenInput = document.getElementById(
      type === "genres" ? "selectedGenres" : "selectedArtists"
    );

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

//
