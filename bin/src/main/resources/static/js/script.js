console.log("Script loaded");

// change theme work
let currentTheme = getTheme();
//initial -->

document.addEventListener("DOMContentLoaded", () => {
  changeTheme();
});

//TODO:
function changeTheme() {
  //set to web page

  changePageTheme(currentTheme, "");
  //set the listener to change theme button
  const changeThemeButton = document.querySelector("#theme_change_button");

  changeThemeButton.addEventListener("click", (event) => {
    let oldTheme = currentTheme;
    console.log("change theme button clicked");
    if (currentTheme === "dark") {
      //theme ko light
      currentTheme = "light";
    } else {
      //theme ko dark
      currentTheme = "dark";
    }
    console.log(currentTheme);
    changePageTheme(currentTheme, oldTheme);
  });
}

//set theme to localstorage
function setTheme(theme) {
  localStorage.setItem("theme", theme);
}

//get theme from localstorage
function getTheme() {
  let theme = localStorage.getItem("theme");
  return theme ? theme : "light";
}

//change current page theme
function changePageTheme(theme, oldTheme) {
  //localstorage  update
  setTheme(currentTheme);
  //remove the current theme

  if (oldTheme) {
    document.querySelector("html").classList.remove(oldTheme);
  }
  //set the current theme
  document.querySelector("html").classList.add(theme);

  // change the text of button
  document
    .querySelector("#theme_change_button")
    .querySelector("span").textContent = theme == "light" ? "Dark" : "Light";
}

//change page change theme


//navbar
document.addEventListener("DOMContentLoaded", function () {
    const currentPage = window.location.pathname;
    const navLinks = document.querySelectorAll(".nav-link");

    // Highlight the active page
    navLinks.forEach(link => {
        if (link.getAttribute("href") === currentPage) {
            link.classList.add("text-blue-600", "font-bold"); // Highlight active link
        } else {
            link.classList.remove("text-blue-600", "font-bold"); // Remove from inactive links
        }
    });

    // Mobile menu toggle
    const menuToggle = document.getElementById("menu-toggle");
    const mobileMenu = document.getElementById("mobile-menu");

    if (menuToggle && mobileMenu) {
        menuToggle.addEventListener("click", function () {
            mobileMenu.classList.toggle("hidden");
        });
    }
});
/* Course Details Page js  */
document.addEventListener('alpine:init', () => {
            Alpine.store('courseInteractions', {
                init() {
                    // Smooth transition handling
                    const observer = new MutationObserver(() => {
                        document.querySelectorAll('.course-card').forEach(card => {
                            card.style.transform = 'translateY(0)';
                        });
                    });
                    observer.observe(document.body, { childList: true, subtree: true });
                }
            });
        });
		
		
		
		// Additional script to ensure course counts are calculated correctly
		        document.addEventListener('alpine:init', () => {
		            Alpine.store('courseStats', {
		                countCourses() {
		                    const upcomingCount = document.querySelectorAll('.upcoming-course').length;
		                    const inProgressCount = document.querySelectorAll('.inprogress-course').length;
		                    const completedCount = document.querySelectorAll('.completed-course').length;
		                    
		                    return { upcomingCount, inProgressCount, completedCount };
		                }
		            });
		        });
        
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				