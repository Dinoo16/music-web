//Toogle action menu button
function toggleActionMenu(event) {
  event.stopPropagation();
  const actionsButton = event.currentTarget;
  const actionsMenu = actionsButton.nextElementSibling;

  // Close other menu
  document.querySelectorAll(".actions-menu").forEach((menu) => {
    if (menu !== actionsMenu) {
      menu.style.display = "none";
    }
  });

  // Toggle menu
  if (actionsMenu.style.display === "block") {
    actionsMenu.style.display = "none";
  } else {
    actionsMenu.style.display = "block";
  }
}

// Close all menu action button
function closeActionMenu() {
  document.querySelectorAll(".actions-menu").forEach((menu) => {
    menu.style.display = "none";
  });
}

// Close when click outside
document.addEventListener("click", function (event) {
  const isClickInsideMenu =
    event.target.closest(".actions-menu") ||
    event.target.closest(".songlist-actionsButton-btn");

  if (!isClickInsideMenu) {
    closeActionMenu();
  }
});

//Handle navs in upload page
document.addEventListener("DOMContentLoaded", () => {
  const tabs = document.querySelectorAll(".upload-tab");
  const contentBlocks = [
    "songlist",
    "artistList",
    "albumList",
    "genreList",
    "playlist",
  ];

  const paths = [
    "/song/list",
    "/artist/list",
    "/album/list",
    "/genre/list",
    "/playlist/list",
  ];

  // Get active tab from server-rendered HTML
  const activeTab = document.querySelector(".upload-tab.upload-tab-active");
  let activeIndex = 0;

  if (activeTab) {
    tabs.forEach((tab, index) => {
      if (tab === activeTab) {
        activeIndex = index;
      }
    });
  }

  // Show only the active tab content
  contentBlocks.forEach((id, index) => {
    const el = document.getElementById(id);
    if (el) el.style.display = index === activeIndex ? "block" : "none";
  });

  // Handle tab switching
  tabs.forEach((tab, index) => {
    tab.addEventListener("click", () => {
      // Update active tab
      tabs.forEach((t) => t.classList.remove("upload-tab-active"));
      tab.classList.add("upload-tab-active");

      // Show the corresponding content block
      contentBlocks.forEach((id, i) => {
        const el = document.getElementById(id);
        if (el) el.style.display = i === index ? "block" : "none";
      });

      // Change URL without reloading
      const newPath = paths[index];
      history.pushState(null, "", newPath); // You can use replaceState if you don’t want to add to history
    });
  });

  // Optional: Handle back/forward browser navigation (optional logic if you want tabs to react to browser navigation)
  window.addEventListener("popstate", () => {
    const currentPath = window.location.pathname;
    const pathIndex = paths.indexOf(currentPath);
    if (pathIndex !== -1) {
      // Update UI
      tabs.forEach((t) => t.classList.remove("upload-tab-active"));
      tabs[pathIndex].classList.add("upload-tab-active");

      contentBlocks.forEach((id, i) => {
        const el = document.getElementById(id);
        if (el) el.style.display = i === pathIndex ? "block" : "none";
      });
    }
  });
});
