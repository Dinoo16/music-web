//
function toggleActionMenu(event) {
  event.stopPropagation();
  let actionsMenu = event.currentTarget.nextElementSibling;

  // Close all other actions menus
  document.querySelectorAll(".songlist-actions-menu").forEach((menu) => {
    if (menu !== actionsMenu) {
      menu.style.display = "none";
    }
  });
  // Toggle visibility
  if (actionsMenu.style.display === "block") {
    actionsMenu.style.display = "none";
  } else {
    actionsMenu.style.display = "block";
  }
}

// Active tab in admin upload page
document.addEventListener("DOMContentLoaded", () => {
  const tabs = document.querySelectorAll(".upload-tab");
  const contentBlocks = ["songlist", "artistList", "albumList", "playlist"];

  tabs.forEach((tab, index) => {
    tab.addEventListener("click", () => {
      // Update active tab
      tabs.forEach((t) => t.classList.remove("upload-tab-active"));
      tab.classList.add("upload-tab-active");

      // Show/Hide content
      contentBlocks.forEach((id, i) => {
        const el = document.getElementById(id);
        if (el) el.style.display = i === index ? "block" : "none";
      });
    });
  });
});
