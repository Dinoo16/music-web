// Function to toggle a song as favorite/unfavorite
function toggleFavorite(event, button) {
  event.preventDefault();
  event.stopPropagation();

  const songID = button.closest(".favorite-icon").getAttribute("data-song-id");
  const heartIcon = button.querySelector("i");
  const message = document.querySelector(".message-wrapper");
  fetch("/api/favorites/toggle", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({ songID: parseInt(songID) }),
  })
    .then((response) => response.json())
    .then((data) => {
      if (data.isFavorite) {
        heartIcon.classList.remove("fa-regular");
        heartIcon.classList.add("fa-solid");

        setTimeout(() => {
          message.style.display = "none";
        }, 3000);
        message.style.display = "flex";
      } else {
        heartIcon.classList.remove("fa-solid");
        heartIcon.classList.add("fa-regular");
      }
    })
    .catch((error) => {
      console.error("Error toggling favorite:", error);
    });
}

// Function to check if songs are in favorites and update UI accordingly
function checkFavoriteStatus() {
  const favoriteIcons = document.querySelectorAll(".favorite-icon");

  favoriteIcons.forEach((icon) => {
    const songID = icon.getAttribute("data-song-id");
    const heartIcon = icon.querySelector("i");

    fetch(`/api/favorites/check/${songID}`)
      .then((response) => response.json())
      .then((data) => {
        if (data.isFavorite) {
          heartIcon.classList.remove("fa-regular");
          heartIcon.classList.add("fa-solid");
        } else {
          heartIcon.classList.remove("fa-solid");
          heartIcon.classList.add("fa-regular");
        }
      })
      .catch((error) => {
        console.error("Error checking favorite status:", error);
      });
  });
}

// Run the check when the page loads
document.addEventListener("DOMContentLoaded", function () {
  checkFavoriteStatus();
});

// Function to filter favorites by genre
function filterfavoritessByGenre() {
  const genreSelect = document.getElementById("genreSelect");
  const selectedGenre = genreSelect.value;

  // If 'all' is selected, show all songs
  if (selectedGenre === "all") {
    document.querySelectorAll(".song-card").forEach((card) => {
      card.style.display = "flex";
    });
    return;
  }

  // Otherwise, filter by the selected genre
  document.querySelectorAll(".song-card").forEach((card) => {
    // This implementation assumes there's a way to get the genre from the song card
    // You might need to add a data attribute to the song card with the genres
    // For now, this is a placeholder implementation
    const songGenres = card.getAttribute("data-genres") || "";

    if (songGenres.includes(selectedGenre)) {
      card.style.display = "flex";
    } else {
      card.style.display = "none";
    }
  });
}
