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

//

document.addEventListener("DOMContentLoaded", function () {
  const selectBtn = document.querySelector(".select-btn");
  const listItems = document.querySelectorAll(".list-items .item");
  const btnText = document.querySelector(".btn-text");

  if (!selectBtn || !btnText || listItems.length === 0) {
    console.warn("Dropdown elements not found");
    return;
  }

  selectBtn.addEventListener("click", () => {
    selectBtn.classList.toggle("open");
  });

  listItems.forEach((item) => {
    item.addEventListener("click", () => {
      item.classList.toggle("checked");

      const checkedItems = document.querySelectorAll(".list-items .checked");
      if (checkedItems.length > 0) {
        btnText.innerText = `${checkedItems.length} Selected`;
      } else {
        btnText.innerText = "Select Genres";
      }
    });
  });
});
