// Variable to store all courses
let allCourses = [];
// Variable to store filtered courses
let filteredCourses = [];
// Current filter settings
const filters = {
    categories: [],
    durations: [],
    providers: [],
    searchTerm: '',
    sortBy: 'Latest Up'
};

// Initialize page when DOM is fully loaded
document.addEventListener('DOMContentLoaded', function() {
    console.log('DOM fully loaded');
    
    // Check if we're on the courses page
    if (document.getElementById('courseContainer')) {
        console.log('Courses page detected, initializing...');
        loadCourses();
        setupEventListeners();
    } else {
        console.log('Not on courses page, skipping initialization');
    }
});

// Load courses from JSON file
function loadCourses() {
    console.log('Loading courses from JSON file');
    
    // Try with various path options to find the JSON file
    // First, try the absolute path with /static explicitly
    fetch('/static/js/data/courses.json')
        .then(response => {
            console.log('First path attempt response status:', response.status);
            if (!response.ok) {
                // If that fails, try with /js/data
                console.log('Trying second path option...');
                return fetch('/js/data/courses.json');
            }
            return response;
        })
        .then(response => {
            console.log('Second path attempt response status:', response.status || 'skipped');
            if (!response.ok) {
                // If that fails, try a deeper relative path
                console.log('Trying third path option...');
                return fetch('../static/js/data/courses.json');
            }
            return response;
        })
        .then(response => {
            console.log('Third path attempt response status:', response.status || 'skipped');
            if (!response.ok) {
                console.log('All paths failed, using fallback data');
                // If all fail, use fallback data
                return Promise.resolve({
                    json: () => Promise.resolve(FALLBACK_COURSES)
                });
            }
            return response;
        })
        .then(response => response.json())
        .then(data => {
            console.log('Raw JSON data:', JSON.stringify(data).slice(0, 100) + '...');
            
            if (!Array.isArray(data)) {
                throw new Error('Data is not an array');
            }
            
            if (data.length === 0) {
                throw new Error('No courses found in JSON');
            }
            
            console.log('First course sample:', data[0]);
            
            // Load the data into allCourses, but do NOT display them by default
            allCourses = data;
            
            // Initialize filteredCourses as empty - don't show courses until filters are applied
            filteredCourses = [];
            console.log('Courses loaded but not displayed. Apply filters to see courses.');
            
            // Display empty course container with message
            const courseContainer = document.getElementById('courseContainer');
            if (courseContainer) {
                courseContainer.innerHTML = '<div class="col-span-4 text-center py-8"><p class="text-xl text-gray-500">Select filters to view courses</p></div>';
            }
            
            // Set the clearAllUsed flag to true since we're starting with no courses displayed
            clearAllUsed = true;
        })
        .catch(error => {
            console.error('Error loading courses:', error);
            // Use fallback data if fetch fails
            console.log('Using fallback data due to error');
            
            // Log additional debug info about image paths for diagnosis
            console.log('Checking image paths in fallback data:');
            FALLBACK_COURSES.forEach(course => {
                console.log(`Course: ${course.title}, Image path: ${course.image}`);
            });
            
            // Load the data, but don't display courses initially
            allCourses = FALLBACK_COURSES;
            filteredCourses = [];
            
            // Display empty course container with message
            const courseContainer = document.getElementById('courseContainer');
            if (courseContainer) {
                courseContainer.innerHTML = '<div class="col-span-4 text-center py-8"><p class="text-xl text-gray-500">Select filters to view courses</p></div>';
            }
            
            // Set the clearAllUsed flag to true since we're starting with no courses displayed
            clearAllUsed = true;
        });
}

// Fallback courses data in case the JSON file can't be loaded
const FALLBACK_COURSES = [
  {
    "id": 1,
    "title": "Introduction to AI in Healthcare",
    "instructors": "Mr. ABC, Ms. DEF",
    "category": "Artificial Intelligence",
    "duration": "1-Day Program",
    "provider": "C-DAC Noida",
    "image": "images/Picture2.jpg",
    "description": "Learn the fundamentals of AI applications in healthcare systems",
    "date_added": "2023-07-15"
  },
  {
    "id": 2,
    "title": "Prescription Mgmt. using AI",
    "instructors": "Mr. ABC, Ms. DEF",
    "category": "Artificial Intelligence",
    "duration": "1-Day Program",
    "provider": "C-DAC Noida",
    "image": "images/Picture2.jpg",
    "description": "Learn how AI can streamline and improve prescription management systems",
    "date_added": "2023-08-10"
  },
  {
    "id": 3,
    "title": "Future of AI in Healthcare",
    "instructors": "Mr. ABC, Ms. DEF",
    "category": "Artificial Intelligence",
    "duration": "2-Day Program",
    "provider": "C-DAC Mohali",
    "image": "images/Picture2.jpg",
    "description": "Explore emerging trends and future applications of AI in healthcare",
    "date_added": "2023-09-05"
  },
  {
    "id": 4,
    "title": "AI Case Studies",
    "instructors": "Mr. ABC, Ms. DEF",
    "category": "Artificial Intelligence",
    "duration": "3-Day Program",
    "provider": "C-DAC Noida",
    "image": "images/Picture2.jpg",
    "description": "Analyze real-world applications and case studies of AI implementation",
    "date_added": "2023-06-20"
  },
  {
    "id": 5,
    "title": "Cybersecurity Fundamentals",
    "instructors": "Dr. GHI, Prof. JKL",
    "category": "Cyber Security",
    "duration": "1-Day Program",
    "provider": "C-DAC Mohali",
    "image": "images/Picture3.jpg",
    "description": "Introduction to core concepts and practices in cybersecurity",
    "date_added": "2023-10-15"
  },
  {
    "id": 6,
    "title": "Network Security Essentials",
    "instructors": "Dr. GHI, Prof. JKL",
    "category": "Cyber Security",
    "duration": "2-Day Program",
    "provider": "C-DAC Noida",
    "image": "images/Picture3.jpg",
    "description": "Learn principles and practices for securing network infrastructure",
    "date_added": "2023-11-10"
  },
  {
    "id": 7,
    "title": "Ethical Hacking Workshop",
    "instructors": "Dr. MNO, Prof. PQR",
    "category": "Cyber Security",
    "duration": "5-Day Program",
    "provider": "C-DAC Mohali",
    "image": "images/Picture3.jpg",
    "description": "Hands-on training in ethical hacking methodologies and tools",
    "date_added": "2023-12-05"
  },
  {
    "id": 8,
    "title": "Advanced Threat Intelligence",
    "instructors": "Dr. MNO, Prof. PQR",
    "category": "Cyber Security",
    "duration": "10-Day Program",
    "provider": "C-DAC Noida",
    "image": "images/Picture3.jpg",
    "description": "Advanced techniques for identifying and mitigating cyber threats",
    "date_added": "2024-01-20"
  }
];

// Set up event listeners for filters and search
function setupEventListeners() {
    console.log('Setting up event listeners');
    
    // Category checkboxes
    document.querySelectorAll('input[name="category"]').forEach(checkbox => {
        checkbox.addEventListener('change', handleCategoryFilter);
        console.log('Added category listener to:', checkbox.value);
    });

    // Duration checkboxes
    document.querySelectorAll('input[name="duration"]').forEach(checkbox => {
        checkbox.addEventListener('change', handleDurationFilter);
        console.log('Added duration listener to:', checkbox.value);
    });

    // Provider checkboxes
    document.querySelectorAll('input[name="provider"]').forEach(checkbox => {
        checkbox.addEventListener('change', handleProviderFilter);
        console.log('Added provider listener to:', checkbox.value);
    });

    // Search input
    const searchInput = document.getElementById('courseSearch');
    if (searchInput) {
        searchInput.addEventListener('keyup', function(e) {
            if (e.key === 'Enter') {
                handleSearch();
            }
        });
        console.log('Added search input listener');
    } else {
        console.warn('Search input element not found');
    }

    // Search button
    const searchButton = document.getElementById('searchButton');
    if (searchButton) {
        searchButton.addEventListener('click', handleSearch);
        console.log('Added search button listener');
    } else {
        console.warn('Search button element not found');
    }

    // Clear all button
    const clearButton = document.getElementById('clearAll');
    if (clearButton) {
        clearButton.addEventListener('click', clearAllFilters);
        console.log('Added clear all listener');
    } else {
        console.warn('Clear all button element not found');
    }

    // Sort dropdown
    const sortSelect = document.getElementById('sortSelect');
    if (sortSelect) {
        sortSelect.addEventListener('change', handleSort);
        console.log('Added sort select listener');
    } else {
        console.warn('Sort select element not found');
    }
}

// Handle category filter changes
function handleCategoryFilter(e) {
    const category = e.target.value;
    console.log('Category filter changed:', category, 'Checked:', e.target.checked);
    
    if (e.target.checked) {
        if (!filters.categories.includes(category)) {
            filters.categories.push(category);
        }
    } else {
        filters.categories = filters.categories.filter(cat => cat !== category);
    }
    applyFilters();
}

// Handle duration filter changes
function handleDurationFilter(e) {
    const duration = e.target.value;
    console.log('Duration filter changed:', duration, 'Checked:', e.target.checked);
    
    if (e.target.checked) {
        if (!filters.durations.includes(duration)) {
            filters.durations.push(duration);
        }
    } else {
        filters.durations = filters.durations.filter(dur => dur !== duration);
    }
    applyFilters();
}

// Handle provider filter changes
function handleProviderFilter(e) {
    const provider = e.target.value;
    console.log('Provider filter changed:', provider, 'Checked:', e.target.checked);
    
    if (e.target.checked) {
        if (!filters.providers.includes(provider)) {
            filters.providers.push(provider);
        }
    } else {
        filters.providers = filters.providers.filter(prov => prov !== provider);
    }
    applyFilters();
}

// Handle search input
function handleSearch() {
    const searchInput = document.getElementById('courseSearch');
    filters.searchTerm = searchInput ? searchInput.value.toLowerCase().trim() : '';
    console.log('Search term:', filters.searchTerm);
    applyFilters();
}

// Handle sorting change
function handleSort(e) {
    filters.sortBy = e.target.value;
    console.log('Sort changed to:', filters.sortBy);
    applySorting();
    displayCourses();
}

// Apply active filters to courses
function applyFilters() {
    console.log('Applying filters:', JSON.stringify(filters));
    console.log('All courses count before filtering:', allCourses.length);
    
    // If no filters are applied, keep courses empty
    if (filters.categories.length === 0 && 
        filters.durations.length === 0 && 
        filters.providers.length === 0 && 
        filters.searchTerm === '') {
        console.log('No filters applied, keeping courses empty');
        filteredCourses = [];
        clearAllUsed = true;
        displayCourses();
        return;
    }
    
    // Reset the clearAllUsed flag since we're applying filters now
    clearAllUsed = false;
    
    // Check if allCourses is properly populated
    if (!Array.isArray(allCourses) || allCourses.length === 0) {
        console.error('No courses available to filter');
        filteredCourses = [];
        displayCourses();
        return;
    }
    
    filteredCourses = allCourses.filter(course => {
        // For debugging, let's check one course
        if (course.id === 1) {
            console.log('Example course being filtered:', course);
        }
        
        // Filter by category
        if (filters.categories.length > 0 && !filters.categories.includes(course.category)) {
            return false;
        }
        
        // Filter by duration
        if (filters.durations.length > 0 && !filters.durations.includes(course.duration)) {
            return false;
        }
        
        // Filter by provider
        if (filters.providers.length > 0 && !filters.providers.includes(course.provider)) {
            return false;
        }
        
        // Filter by search term
        if (filters.searchTerm && !course.title.toLowerCase().includes(filters.searchTerm) && 
            !course.description.toLowerCase().includes(filters.searchTerm)) {
            return false;
        }
        
        return true;
    });
    
    console.log('Filtered courses count:', filteredCourses.length);
    updateFilteredByText();
    applySorting();
    displayCourses();
}

// Flag to track if clear all was used
let clearAllUsed = false;

// Display the filtered courses
function displayCourses() {
    const courseContainer = document.getElementById('courseContainer');
    if (!courseContainer) {
        console.warn('Course container element not found');
        return;
    }
    
    // Clear the current display
    courseContainer.innerHTML = '';
    
    // If there are no courses to display (either filtered or cleared)
    if (filteredCourses.length === 0) {
        if (clearAllUsed) {
            courseContainer.innerHTML = '<div class="col-span-4 text-center py-8"><p class="text-xl text-gray-500">No courses selected. Use filters to view courses.</p></div>';
        } else {
            courseContainer.innerHTML = '<div class="col-span-4 text-center py-8"><p class="text-xl text-gray-500">No courses found matching your criteria</p></div>';
        }
        return;
    }
    
    // Reset clear all flag if we're displaying courses
    clearAllUsed = false;
    
    // Create course cards
    filteredCourses.forEach(course => {
        // No need for image path code since we're removing images
        const courseCard = document.createElement('div');
        courseCard.className = 'bg-white rounded-lg shadow-lg overflow-hidden border border-gray-200 flex flex-col';
        
        courseCard.innerHTML = `
            <div class="p-6 flex-1 flex flex-col">
                <div class="text-left flex-1">
                    <h3 class="text-xl font-semibold text-gray-900 mb-2 text-left">${course.title}</h3>
                    <p class="text-gray-600 mb-4 text-left">${course.instructors}</p>
                    <p class="text-sm text-gray-500 mb-4 text-left">Category: ${course.category}</p>
                    <p class="text-sm text-gray-500 mb-4 text-left">Duration: ${course.duration}</p>
                    <p class="text-sm text-gray-500 mb-4 text-left">Provider: ${course.provider}</p>
                </div>
                <a href="#" class="w-full">
                    <button class="w-full bg-blue-600 text-white py-2 rounded hover:bg-blue-700 transition">
                        Explore
                    </button>
                </a>
            </div>
        `;
        
        courseContainer.appendChild(courseCard);
    });
    
    console.log('Displayed courses:', filteredCourses.length);
}

// Update the "Filtered by" text
function updateFilteredByText() {
    const filteredByElement = document.getElementById('filtered-label');
    if (!filteredByElement) {
        console.warn('Filtered by element not found');
        return;
    }
    
    if (filters.categories.length === 0 && filters.searchTerm === '') {
        filteredByElement.innerHTML = '';
        return;
    }
    
    let filteredByText = 'Filtered by: ';
    if (filters.categories.length > 0) {
        filteredByText += filters.categories.map(cat => {
            return `<span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-800 mr-2">
                      ${cat} <button class="ml-1 text-blue-500 hover:text-blue-700" onclick="removeFilter('category', '${cat}')">×</button>
                    </span>`;
        }).join('');
    }
    
    if (filters.searchTerm) {
        filteredByText += `<span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-800">
                            Search: "${filters.searchTerm}" <button class="ml-1 text-green-500 hover:text-green-700" onclick="clearSearch()">×</button>
                          </span>`;
    }
    
    filteredByElement.innerHTML = filteredByText;
}

// Make these functions available globally
window.removeFilter = function(type, value) {
    console.log('Removing filter:', type, value);
    switch(type) {
        case 'category':
            filters.categories = filters.categories.filter(cat => cat !== value);
            const categoryCheckbox = document.querySelector(`input[name="category"][value="${value}"]`);
            if (categoryCheckbox) {
                categoryCheckbox.checked = false;
            }
            break;
        case 'duration':
            filters.durations = filters.durations.filter(dur => dur !== value);
            const durationCheckbox = document.querySelector(`input[name="duration"][value="${value}"]`);
            if (durationCheckbox) {
                durationCheckbox.checked = false;
            }
            break;
        case 'provider':
            filters.providers = filters.providers.filter(prov => prov !== value);
            const providerCheckbox = document.querySelector(`input[name="provider"][value="${value}"]`);
            if (providerCheckbox) {
                providerCheckbox.checked = false;
            }
            break;
    }
    applyFilters();
};

// Make clearSearch globally available
window.clearSearch = function() {
    console.log('Clearing search');
    filters.searchTerm = '';
    const searchInput = document.getElementById('courseSearch');
    if (searchInput) {
        searchInput.value = '';
    }
    applyFilters();
};

// Apply sorting to filtered courses
function applySorting() {
    console.log('Applying sort:', filters.sortBy);
    
    switch(filters.sortBy) {
        case 'Latest Up':
            filteredCourses.sort((a, b) => new Date(b.date_added) - new Date(a.date_added));
            break;
        case 'Latest Down':
            filteredCourses.sort((a, b) => new Date(a.date_added) - new Date(b.date_added));
            break;
        case 'Relevance':
            // For simplicity, sort by ID
            filteredCourses.sort((a, b) => a.id - b.id);
            break;
        default:
            filteredCourses.sort((a, b) => new Date(b.date_added) - new Date(a.date_added));
    }
}

// Clear all filters
function clearAllFilters() {
    console.log('Clearing all filters');
    // Reset checkboxes
    document.querySelectorAll('input[type="checkbox"]').forEach(checkbox => {
        checkbox.checked = false;
    });
    
    // Reset search
    const searchInput = document.getElementById('courseSearch');
    if (searchInput) {
        searchInput.value = '';
    }
    
    // Reset filter object
    filters.categories = [];
    filters.durations = [];
    filters.providers = [];
    filters.searchTerm = '';
    
    // Apply changes
    const filteredLabel = document.getElementById('filtered-label');
    if (filteredLabel) {
        filteredLabel.innerHTML = '';
    }
    
    // Clear all courses - set to empty array
    filteredCourses = [];
    
    // Set flag to indicate clear all was used
    clearAllUsed = true;
    
    // Display the "no courses" message
    const courseContainer = document.getElementById('courseContainer');
    if (courseContainer) {
        courseContainer.innerHTML = '<div class="col-span-4 text-center py-8"><p class="text-xl text-gray-500">Select filters to view courses</p></div>';
    }
    
    console.log('Filters cleared, no courses displayed');
} 