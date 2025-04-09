document.addEventListener("DOMContentLoaded", function () {
  const sidebarLinks = document.querySelectorAll(".sidebar__box a");
  const path = window.location.pathname;

  const storedHref = localStorage.getItem("activeSidebarLink");

  let found = false;
  sidebarLinks.forEach((link) => {
    const href = link.getAttribute("href");

    if (path === "/" && href === "/") {
      link.classList.add("active");
      found = true;
    }

    if (href === storedHref && path === href) {
      link.classList.add("active");
      found = true;
    }
  });

  if (!found) {
    localStorage.removeItem("activeSidebarLink");
  }

  sidebarLinks.forEach((link) => {
    link.addEventListener("click", function () {
      sidebarLinks.forEach((l) => l.classList.remove("active"));

      this.classList.add("active");

      localStorage.setItem("activeSidebarLink", this.getAttribute("href"));
    });
  });
});
