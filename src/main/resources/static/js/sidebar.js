document.addEventListener("DOMContentLoaded", function () {
  const sidebarLinks = document.querySelectorAll(".sidebar__box a");
  let isActiveSet = false;

  sidebarLinks.forEach((link) => {
    const path = window.location.pathname;
    if (
      path.startsWith("/discover") &&
      link.getAttribute("href") === "/discover"
    ) {
      link.classList.add("active");
      isActiveSet = true;
    }
    if (
      path.startsWith("/topchart") &&
      link.getAttribute("href") === "/topchart"
    ) {
      link.classList.add("active");
      isActiveSet = true;
    }
    if (path.startsWith("/genres") && link.getAttribute("href") === "/genres") {
      link.classList.add("active");
      isActiveSet = true;
    }
    if (path.startsWith("/song") && link.getAttribute("href") === "/song") {
      link.classList.add("active");
      isActiveSet = true;
    }
    if (path.startsWith("/artist") && link.getAttribute("href") === "/artist") {
      link.classList.add("active");
      isActiveSet = true;
    }
    if (path.startsWith("/album") && link.getAttribute("href") === "/album") {
      link.classList.add("active");
      isActiveSet = true;
    }

    if (path.startsWith("/genre") && link.getAttribute("href") === "/genre") {
      link.classList.add("active");
      isActiveSet = true;
    }
    if (
      path.startsWith("/myplaylist") &&
      link.getAttribute("href") === "/myplaylist"
    ) {
      link.classList.add("active");
      isActiveSet = true;
    }
    if (
      path.startsWith("/favorite") &&
      link.getAttribute("href") === "/favorite"
    ) {
      link.classList.add("active");
      isActiveSet = true;
    }
    if (
      path.startsWith("/setting") &&
      link.getAttribute("href") === "/setting"
    ) {
      link.classList.add("active");
      isActiveSet = true;
    }
    if (
      path.startsWith("/admin/songlist") &&
      link.getAttribute("href") === "/admin/songlist"
    ) {
      link.classList.add("active");
      isActiveSet = true;
    }
    if (
      path.startsWith("/admin/userlist") &&
      link.getAttribute("href") === "/admin/userlist"
    ) {
      link.classList.add("active");
      isActiveSet = true;
    }
    if (
      path.startsWith("/admin/playlist") &&
      link.getAttribute("href") === "/admin/playlist"
    ) {
      link.classList.add("active");
      isActiveSet = true;
    }
    if (
      path.startsWith("/admin/static") &&
      link.getAttribute("href") === "/admin/static"
    ) {
      link.classList.add("active");
      isActiveSet = true;
    }
  });

  // If no active link was set, default to Home
  if (!isActiveSet) {
    const homeLink = document.querySelector('.sidebar__box a[href="/"]');
    if (homeLink) {
      homeLink.classList.add("active");
    }
  }
});
