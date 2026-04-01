// Main JavaScript file for Job Portal

document.addEventListener('DOMContentLoaded', function() {
    // Initialize Bootstrap tooltips
    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'))
    var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl)
    })
    
    // Initialize Bootstrap popovers
    var popoverTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="popover"]'))
    var popoverList = popoverTriggerList.map(function (popoverTriggerEl) {
        return new bootstrap.Popover(popoverTriggerEl)
    })
    
    // Auto-hide alerts after 5 seconds
    setTimeout(function() {
        $('.alert').alert('close');
    }, 5000);
    
    // Job search form validation
    const searchForm = document.getElementById('job-search-form');
    if (searchForm) {
        searchForm.addEventListener('submit', function(event) {
            const keyword = document.getElementById('keyword').value.trim();
            const location = document.getElementById('location').value.trim();
            
            if (keyword === '' && location === '') {
                event.preventDefault();
                
                // Create alert
                const alert = document.createElement('div');
                alert.className = 'alert alert-warning alert-dismissible fade show';
                alert.setAttribute('role', 'alert');
                alert.innerHTML = `
                    Please enter at least one search criteria (keyword or location).
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                `;
                
                // Insert before form
                searchForm.parentNode.insertBefore(alert, searchForm);
                
                // Auto-hide after 3 seconds
                setTimeout(function() {
                    alert.classList.remove('show');
                    setTimeout(function() {
                        alert.remove();
                    }, 150);
                }, 3000);
            }
        });
    }
    
    // Application form validation
    const applicationForm = document.getElementById('application-form');
    if (applicationForm) {
        applicationForm.addEventListener('submit', function(event) {
            const coverLetter = document.getElementById('coverLetter').value.trim();
            const resumeFile = document.getElementById('resumeFile').value;
            
            if (coverLetter === '') {
                event.preventDefault();
                document.getElementById('coverLetterHelp').classList.add('text-danger');
                document.getElementById('coverLetterHelp').textContent = 'Please provide a cover letter';
            }
            
            if (resumeFile === '') {
                event.preventDefault();
                document.getElementById('resumeFileHelp').classList.add('text-danger');
                document.getElementById('resumeFileHelp').textContent = 'Please upload your resume';
            }
        });
    }
    
    // Handle file input custom styling
    const fileInputs = document.querySelectorAll('.custom-file-input');
    if (fileInputs.length > 0) {
        fileInputs.forEach(input => {
            input.addEventListener('change', function(e) {
                const fileName = e.target.files[0] ? e.target.files[0].name : 'Choose file';
                const nextSibling = e.target.nextElementSibling;
                nextSibling.innerText = fileName;
            });
        });
    }
});

// Job filter functionality
function filterJobs() {
    const jobType = document.getElementById('jobTypeFilter').value;
    const location = document.getElementById('locationFilter').value.toLowerCase();
    const salary = document.getElementById('salaryFilter').value;
    
    const jobCards = document.querySelectorAll('.job-card');
    
    jobCards.forEach(card => {
        let showCard = true;
        
        // Filter by job type
        if (jobType !== 'all') {
            const cardJobType = card.getAttribute('data-job-type');
            if (cardJobType !== jobType) {
                showCard = false;
            }
        }
        
        // Filter by location
        if (location !== '') {
            const cardLocation = card.getAttribute('data-location').toLowerCase();
            if (!cardLocation.includes(location)) {
                showCard = false;
            }
        }
        
        // Filter by salary (minimum)
        if (salary !== '') {
            const cardSalary = parseInt(card.getAttribute('data-salary') || '0');
            if (cardSalary < parseInt(salary)) {
                showCard = false;
            }
        }
        
        // Show/hide card based on filter results
        card.style.display = showCard ? '' : 'none';
    });
    
    // Show message if no results
    const visibleCards = document.querySelectorAll('.job-card[style=""]').length;
    const noResultsMessage = document.getElementById('no-results-message');
    
    if (visibleCards === 0 && noResultsMessage) {
        noResultsMessage.style.display = 'block';
    } else if (noResultsMessage) {
        noResultsMessage.style.display = 'none';
    }
}