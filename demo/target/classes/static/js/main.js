/**
 * Student Library Access Management System - Main JavaScript
 * Handles interactive features and API calls
 */

// ============================================================
// CONSTANTS
// ============================================================
const API_BASE = "/api";
const ENTER_LIBRARY_URL = `${API_BASE}/issuances/enter`;
const EXIT_LIBRARY_URL = `${API_BASE}/issuances/exit`;
const ISSUE_BOOK_URL = `${API_BASE}/issuances/issue`;
const ISSUE_MULTIPLE_URL = `${API_BASE}/issuances/issue-multiple`;
const GET_BOOKS_URL = `${API_BASE}/books`;
const IN_LIBRARY_URL = `${API_BASE}/issuances/in-library`;

// ============================================================
// UTILITY FUNCTIONS
// ============================================================

/**
 * Show alert message
 */
function showAlert(message, type = "success") {
  const alertDiv = document.createElement("div");
  alertDiv.className = `alert alert-${type} fade-in`;
  alertDiv.innerHTML = `<span>${message}</span>`;

  const mainContent = document.querySelector(".main-content");
  mainContent.insertBefore(alertDiv, mainContent.firstChild);

  // Auto-remove after 5 seconds
  setTimeout(() => {
    alertDiv.style.animation = "fadeOut 0.3s ease-out";
    setTimeout(() => alertDiv.remove(), 300);
  }, 5000);
}

/**
 * Make API call
 */
async function apiCall(url, method = "GET", data = null) {
  try {
    const options = {
      method,
      headers: {
        "Content-Type": "application/json",
        "X-CSRF-TOKEN": getCsrfToken(),
      },
    };

    if (data) {
      options.body = JSON.stringify(data);
    }

    const response = await fetch(url, options);

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    return await response.json();
  } catch (error) {
    console.error("API Error:", error);
    throw error;
  }
}

/**
 * Get CSRF token from meta tag or cookie
 */
function getCsrfToken() {
  // Try meta tag first
  const meta = document.querySelector('meta[name="csrf-token"]');
  if (meta) return meta.getAttribute("content");

  // Try cookie
  const name = "_csrf";
  const value = `; ${document.cookie}`;
  const parts = value.split(`; ${name}=`);
  if (parts.length === 2) return parts.pop().split(";").shift();

  return "";
}

/**
 * Format date and time
 */
function formatDateTime(dateString) {
  const date = new Date(dateString);
  return date.toLocaleString();
}

/**
 * Format time duration
 */
function formatDuration(minutes) {
  if (minutes < 0) return "Still in library";
  if (minutes < 60) return `${minutes} min`;
  const hours = Math.floor(minutes / 60);
  const mins = minutes % 60;
  return `${hours}h ${mins}m`;
}

// ============================================================
// NAVBAR FUNCTIONALITY
// ============================================================

document.addEventListener("DOMContentLoaded", () => {
  const navbarToggle = document.querySelector("#navbarToggle");
  const navbarMenu = document.querySelector("#navbarMenu");

  if (navbarToggle && navbarMenu) {
    navbarToggle.addEventListener("click", () => {
      navbarMenu.classList.toggle("mobile-active");
    });

    // Close menu when link is clicked
    navbarMenu.querySelectorAll(".navbar-item").forEach((item) => {
      item.addEventListener("click", () => {
        navbarMenu.classList.remove("mobile-active");
      });
    });
  }
});

// ============================================================
// BOOK SEARCH FUNCTIONALITY
// ============================================================

const bookSearchInput = document.querySelector("#bookSearchInput");
if (bookSearchInput) {
  let searchTimeout;

  bookSearchInput.addEventListener("input", (e) => {
    clearTimeout(searchTimeout);
    const searchQuery = e.target.value.trim();

    if (searchQuery.length < 2) {
      loadAvailableBooks();
      return;
    }

    searchTimeout = setTimeout(() => {
      searchBooks(searchQuery);
    }, 300);
  });
}

/**
 * Search books by title and author
 */
async function searchBooks(query) {
  try {
    const titleResults = await apiCall(
      `${GET_BOOKS_URL}/search/title?q=${encodeURIComponent(query)}`,
    );
    const authorResults = await apiCall(
      `${GET_BOOKS_URL}/search/author?q=${encodeURIComponent(query)}`,
    );

    // Combine results, remove duplicates
    const combined = [...titleResults, ...authorResults];
    const unique = Array.from(
      new Map(combined.map((book) => [book.id, book])).values(),
    );

    displayBooks(unique);
  } catch (error) {
    console.error("Search error:", error);
    showAlert("Error searching books", "error");
  }
}

/**
 * Load available books
 */
async function loadAvailableBooks() {
  try {
    const books = await apiCall(`${GET_BOOKS_URL}/available`);
    displayBooks(books);
  } catch (error) {
    console.error("Error loading books:", error);
  }
}

/**
 * Display books in the grid
 */
function displayBooks(books) {
  const booksContainer = document.querySelector("#booksContainer");
  if (!booksContainer) return;

  if (books.length === 0) {
    booksContainer.innerHTML =
      '<p style="text-align: center; color: #999;">No books available</p>';
    return;
  }

  booksContainer.innerHTML = books
    .map(
      (book) => `
        <div class="book-card stagger-item">
            <div class="book-cover">📖</div>
            <div class="book-content">
                <div class="book-title">${book.title}</div>
                <div class="book-author">by ${book.author}</div>
                <div class="book-category">${book.category}</div>
                <div class="book-quantity ${book.quantity > 0 ? "available" : "unavailable"}">
                    ${book.quantity > 0 ? `✓ ${book.quantity} available` : "Out of stock"}
                </div>
                <button class="btn btn-primary btn-block" ${book.quantity <= 0 ? "disabled" : ""} 
                        onclick="issueBook(${book.id}, '${book.title.replace(/'/g, "\\'")}')"
                        id="btn-book-${book.id}">
                    Issue Book
                </button>
            </div>
        </div>
    `,
    )
    .join("");
}

// ============================================================
// BOOK ISSUANCE
// ============================================================

let selectedBooks = [];

/**
 * Issue a single book
 */
async function issueBook(bookId, bookTitle) {
  try {
    const button = document.querySelector(`#btn-book-${bookId}`);
    button.disabled = true;
    button.innerHTML = '<span class="loader loader-sm"></span> Issuing...';

    const result = await apiCall(ISSUE_BOOK_URL + `?bookId=${bookId}`, "POST");

    button.innerHTML = "✓ Issued";
    button.style.backgroundColor = "#48dbfb";
    showAlert(`${bookTitle} has been issued successfully!`, "success");

    // Reload books after a short delay
    setTimeout(loadAvailableBooks, 1500);
  } catch (error) {
    const button = document.querySelector(`#btn-book-${bookId}`);
    button.disabled = false;
    button.innerHTML = "Issue Book";
    showAlert("Failed to issue book. Please try again.", "error");
  }
}

/**
 * Issue multiple books at once
 */
async function issueMultipleBooks() {
  const checkboxes = document.querySelectorAll(
    'input[name="bookSelection"]:checked',
  );
  if (checkboxes.length === 0) {
    showAlert("Please select at least one book", "error");
    return;
  }

  const bookIds = Array.from(checkboxes).map((cb) => parseInt(cb.value));

  try {
    const button = document.querySelector("#issueMultipleBtn");
    button.disabled = true;
    button.innerHTML = '<span class="loader loader-sm"></span> Issuing...';

    const results = await apiCall(ISSUE_MULTIPLE_URL, "POST", { bookIds });

    button.disabled = false;
    button.innerHTML = "Issue Selected Books";
    showAlert(`${results.length} books issued successfully!`, "success");

    // Clear selections
    checkboxes.forEach((cb) => (cb.checked = false));
    setTimeout(loadAvailableBooks, 1500);
  } catch (error) {
    const button = document.querySelector("#issueMultipleBtn");
    button.disabled = false;
    button.innerHTML = "Issue Selected Books";
    showAlert("Failed to issue books. Please try again.", "error");
  }
}

// ============================================================
// LIBRARY ENTRY/EXIT
// ============================================================

/**
 * Enter library
 */
async function enterLibrary() {
  try {
    const button = document.querySelector("#enterLibraryBtn");
    if (button) {
      button.disabled = true;
      button.innerHTML = '<span class="loader loader-sm"></span> Entering...';
    }

    const result = await apiCall(ENTER_LIBRARY_URL, "POST");

    if (button) {
      button.disabled = false;
      button.innerHTML = "✓ In Library";
      button.style.backgroundColor = "#48dbfb";
    }

    showAlert("Welcome to the library!", "success");
    updateLibraryStatus();
  } catch (error) {
    const button = document.querySelector("#enterLibraryBtn");
    if (button) {
      button.disabled = false;
      button.innerHTML = "Enter Library";
    }
    showAlert("Failed to enter library. Please try again.", "error");
  }
}

/**
 * Exit library
 */
async function exitLibrary() {
  if (!confirm("Are you sure you want to exit the library?")) return;

  try {
    const button = document.querySelector("#exitLibraryBtn");
    if (button) {
      button.disabled = true;
      button.innerHTML = '<span class="loader loader-sm"></span> Exiting...';
    }

    const result = await apiCall(EXIT_LIBRARY_URL, "POST");

    if (button) {
      button.disabled = false;
      button.innerHTML = "Exit Library";
      button.style.backgroundColor = "#667eea";
    }

    showAlert(
      `You have exited the library. Time spent: ${result.durationMinutes} minutes`,
      "success",
    );
    updateLibraryStatus();
  } catch (error) {
    const button = document.querySelector("#exitLibraryBtn");
    if (button) {
      button.disabled = false;
      button.innerHTML = "Exit Library";
    }
    showAlert("Failed to exit library. " + (error.message || ""), "error");
  }
}

/**
 * Update library status display
 */
async function updateLibraryStatus() {
  try {
    const inLibrary = await apiCall(IN_LIBRARY_URL);

    const enterBtn = document.querySelector("#enterLibraryBtn");
    const exitBtn = document.querySelector("#exitLibraryBtn");
    const statusIndicator = document.querySelector("#libraryStatus");

    if (inLibrary) {
      if (enterBtn) enterBtn.style.display = "none";
      if (exitBtn) exitBtn.style.display = "inline-block";
      if (statusIndicator) {
        statusIndicator.innerHTML =
          '<span style="color: #48dbfb;">● In Library</span>';
      }
    } else {
      if (enterBtn) enterBtn.style.display = "inline-block";
      if (exitBtn) exitBtn.style.display = "none";
      if (statusIndicator) {
        statusIndicator.innerHTML =
          '<span style="color: #999;">● Not in Library</span>';
      }
    }
  } catch (error) {
    console.error("Error updating library status:", error);
  }
}

// ============================================================
// PAGE LOAD
// ============================================================

document.addEventListener("DOMContentLoaded", () => {
  // Load books if on student dashboard
  const booksContainer = document.querySelector("#booksContainer");
  if (booksContainer) {
    loadAvailableBooks();
  }

  // Update library status if element exists
  const statusIndicator = document.querySelector("#libraryStatus");
  if (statusIndicator) {
    updateLibraryStatus();
  }

  // Set up auto-refresh for admin dashboard
  const adminDashboard = document.querySelector(".admin-dashboard");
  if (adminDashboard) {
    // Refresh every 30 seconds
    setInterval(() => {
      location.reload();
    }, 30000);
  }
});

// ============================================================
// EXPORT FOR GLOBAL USE
// ============================================================
window.issueBook = issueBook;
window.issueMultipleBooks = issueMultipleBooks;
window.enterLibrary = enterLibrary;
window.exitLibrary = exitLibrary;
