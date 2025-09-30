// ===== RBAC System Custom JavaScript =====

$(document).ready(function() {
    
    // Initialize common functionality
    initializeCommon();
    
    // Initialize DataTables
    initializeDataTables();
    
    // Initialize form validation
    initializeFormValidation();
    
    // Initialize tooltips and popovers
    initializeBootstrapComponents();
    
});

// Common initialization
function initializeCommon() {
    
    // Auto-hide alerts after 5 seconds
    $('.alert[data-auto-dismiss!="false"]').delay(5000).fadeOut('slow');
    
    // Loading state for buttons
    $('form').on('submit', function() {
        const submitBtn = $(this).find('button[type="submit"]');
        if (!submitBtn.hasClass('no-loading')) {
            submitBtn.addClass('loading').prop('disabled', true);
        }
    });
    
    // Confirm dialogs for delete actions
    $('form[action*="delete"]').on('submit', function(e) {
        if (!confirm('Are you sure you want to delete this item? This action cannot be undone.')) {
            e.preventDefault();
            return false;
        }
    });
    
    // Back button functionality
    $('.btn-back').on('click', function(e) {
        e.preventDefault();
        if (window.history.length > 1) {
            window.history.back();
        } else {
            window.location.href = '/dashboard';
        }
    });
    
}

// DataTables initialization
function initializeDataTables() {
    
    // Check if DataTables is available
    if (typeof $.fn.DataTable === 'undefined') {
        // Load DataTables CSS and JS if not already loaded
        loadDataTables();
        return;
    }
    
    // Initialize all tables with class 'data-table'
    $('.data-table, #usersTable, #rolesTable, #permissionsTable').each(function() {
        if (!$.fn.DataTable.isDataTable(this)) {
            $(this).DataTable({
                responsive: true,
                pageLength: 10,
                lengthMenu: [[10, 25, 50, -1], [10, 25, 50, "All"]],
                order: [[0, "desc"]],
                columnDefs: [
                    { orderable: false, targets: 'no-sort' },
                    { searchable: false, targets: 'no-search' }
                ],
                language: {
                    search: "Search:",
                    lengthMenu: "Show _MENU_ entries",
                    info: "Showing _START_ to _END_ of _TOTAL_ entries",
                    paginate: {
                        first: "First",
                        last: "Last",
                        next: "Next",
                        previous: "Previous"
                    },
                    emptyTable: "No data available",
                    zeroRecords: "No matching records found"
                }
            });
        }
    });
}

// Load DataTables dynamically
function loadDataTables() {
    // Load DataTables CSS
    if (!$('link[href*="datatables"]').length) {
        $('<link>')
            .attr('rel', 'stylesheet')
            .attr('href', 'https://cdn.datatables.net/1.13.7/css/dataTables.bootstrap5.min.css')
            .appendTo('head');
    }
    
    // Load DataTables JS
    if (typeof $.fn.DataTable === 'undefined') {
        $.getScript('https://cdn.datatables.net/1.13.7/js/jquery.dataTables.min.js')
            .done(function() {
                $.getScript('https://cdn.datatables.net/1.13.7/js/dataTables.bootstrap5.min.js')
                    .done(function() {
                        initializeDataTables();
                    });
            });
    }
}

// Form validation
function initializeFormValidation() {
    
    // Real-time validation
    $('input[required], select[required], textarea[required]').on('blur', function() {
        validateField($(this));
    });
    
    // Form submission validation
    $('form[data-validate="true"]').on('submit', function(e) {
        if (!validateForm($(this))) {
            e.preventDefault();
            return false;
        }
    });
    
}

// Validate individual field
function validateField($field) {
    const value = $field.val().trim();
    const fieldType = $field.attr('type');
    const isRequired = $field.prop('required');
    
    // Clear previous validation states
    $field.removeClass('is-valid is-invalid');
    $field.siblings('.invalid-feedback, .valid-feedback').remove();
    
    // Required field validation
    if (isRequired && !value) {
        showFieldError($field, 'This field is required');
        return false;
    }
    
    // Email validation
    if (fieldType === 'email' && value) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(value)) {
            showFieldError($field, 'Please enter a valid email address');
            return false;
        }
    }
    
    // Password validation
    if (fieldType === 'password' && value) {
        if (value.length < 6) {
            showFieldError($field, 'Password must be at least 6 characters long');
            return false;
        }
    }
    
    // If we get here, field is valid
    if (value) {
        showFieldSuccess($field);
    }
    
    return true;
}

// Validate entire form
function validateForm($form) {
    let isValid = true;
    
    $form.find('input[required], select[required], textarea[required]').each(function() {
        if (!validateField($(this))) {
            isValid = false;
        }
    });
    
    return isValid;
}

// Show field error
function showFieldError($field, message) {
    $field.addClass('is-invalid');
    $field.after('<div class="invalid-feedback">' + message + '</div>');
}

// Show field success
function showFieldSuccess($field) {
    $field.addClass('is-valid');
    $field.after('<div class="valid-feedback">Looks good!</div>');
}

// Initialize Bootstrap components
function initializeBootstrapComponents() {
    
    // Initialize tooltips
    $('[data-bs-toggle="tooltip"]').each(function() {
        new bootstrap.Tooltip(this);
    });
    
    // Initialize popovers
    $('[data-bs-toggle="popover"]').each(function() {
        new bootstrap.Popover(this);
    });
    
    // Initialize toasts
    $('.toast').each(function() {
        new bootstrap.Toast(this).show();
    });
    
}

// Utility functions
const RBACUtils = {
    
    // Show loading spinner
    showLoading: function(message = 'Loading...') {
        const loadingHtml = `
            <div id="loadingOverlay" class="position-fixed top-0 start-0 w-100 h-100 d-flex align-items-center justify-content-center" 
                 style="background: rgba(0,0,0,0.5); z-index: 9999;">
                <div class="bg-white p-4 rounded shadow text-center">
                    <div class="spinner-border text-primary mb-3" role="status">
                        <span class="visually-hidden">Loading...</span>
                    </div>
                    <div>${message}</div>
                </div>
            </div>
        `;
        $('body').append(loadingHtml);
    },
    
    // Hide loading spinner
    hideLoading: function() {
        $('#loadingOverlay').remove();
    },
    
    // Show toast notification
    showToast: function(message, type = 'info') {
        const toastHtml = `
            <div class="toast align-items-center text-white bg-${type} border-0 position-fixed top-0 end-0 m-3" 
                 role="alert" style="z-index: 9999;">
                <div class="d-flex">
                    <div class="toast-body">${message}</div>
                    <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
                </div>
            </div>
        `;
        $('body').append(toastHtml);
        const toast = new bootstrap.Toast($('.toast').last()[0]);
        toast.show();
        
        // Remove toast element after it's hidden
        $('.toast').last().on('hidden.bs.toast', function() {
            $(this).remove();
        });
    },
    
    // Format date
    formatDate: function(dateString) {
        if (!dateString) return '-';
        const date = new Date(dateString);
        return date.toLocaleDateString('th-TH', {
            year: 'numeric',
            month: 'short',
            day: 'numeric'
        });
    },
    
    // Format status badge
    formatStatus: function(status) {
        const statusMap = {
            'active': '<span class="badge bg-success">Active</span>',
            'inactive': '<span class="badge bg-secondary">Inactive</span>',
            'pending': '<span class="badge bg-warning">Pending</span>',
            'suspended': '<span class="badge bg-danger">Suspended</span>'
        };
        return statusMap[status] || `<span class="badge bg-secondary">${status}</span>`;
    },
    
    // Confirm dialog
    confirm: function(message, callback) {
        if (confirm(message)) {
            if (typeof callback === 'function') {
                callback();
            }
            return true;
        }
        return false;
    },
    
    // Debounce function
    debounce: function(func, wait) {
        let timeout;
        return function executedFunction(...args) {
            const later = () => {
                clearTimeout(timeout);
                func(...args);
            };
            clearTimeout(timeout);
            timeout = setTimeout(later, wait);
        };
    }
    
};

// Search functionality
function initializeSearch() {
    
    const searchInput = $('#globalSearch');
    if (searchInput.length) {
        
        const debouncedSearch = RBACUtils.debounce(function(query) {
            performSearch(query);
        }, 300);
        
        searchInput.on('input', function() {
            const query = $(this).val().trim();
            if (query.length >= 2) {
                debouncedSearch(query);
            }
        });
        
    }
    
}

// Perform search
function performSearch(query) {
    // Implementation depends on search requirements
    console.log('Searching for:', query);
}

// Page-specific initializations
$(document).ready(function() {
    
    // Dashboard specific
    if ($('body').hasClass('dashboard-page')) {
        initializeDashboard();
    }
    
    // Users page specific
    if ($('body').hasClass('users-page')) {
        initializeUsersPage();
    }
    
    // Roles page specific
    if ($('body').hasClass('roles-page')) {
        initializeRolesPage();
    }
    
    // Permissions page specific
    if ($('body').hasClass('permissions-page')) {
        initializePermissionsPage();
    }
    
});

// Dashboard initialization
function initializeDashboard() {
    // Refresh stats every 30 seconds
    setInterval(function() {
        // Optional: Auto-refresh dashboard stats
    }, 30000);
}

// Users page initialization
function initializeUsersPage() {
    // User-specific functionality
}

// Roles page initialization
function initializeRolesPage() {
    // Role-specific functionality
}

// Permissions page initialization
function initializePermissionsPage() {
    // Permission-specific functionality
}

// Global error handler
window.addEventListener('error', function(e) {
    console.error('JavaScript Error:', e.error);
    // Optional: Send error to server for logging
});

// Global unhandled promise rejection handler
window.addEventListener('unhandledrejection', function(e) {
    console.error('Unhandled Promise Rejection:', e.reason);
    // Optional: Send error to server for logging
}); 