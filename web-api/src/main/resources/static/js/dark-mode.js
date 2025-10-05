// ===== Dark Mode Functionality =====

class DarkModeManager {
    constructor() {
        this.theme = this.getStoredTheme() || this.getPreferredTheme();
        this.init();
    }

    init() {
        this.setTheme(this.theme);
        this.bindEvents();
        this.updateIcon();
    }

    getStoredTheme() {
        return localStorage.getItem('theme');
    }

    getPreferredTheme() {
        return window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light';
    }

    setTheme(theme) {
        if (theme === 'auto') {
            document.documentElement.removeAttribute('data-bs-theme');
        } else {
            document.documentElement.setAttribute('data-bs-theme', theme);
        }
        localStorage.setItem('theme', theme);
        this.theme = theme;
        this.updateIcon();
    }

    toggleTheme() {
        const currentTheme = this.getStoredTheme();
        const newTheme = currentTheme === 'dark' ? 'light' : 'dark';
        this.setTheme(newTheme);
        
        // Show toast notification
        this.showThemeChangeToast(newTheme);
    }

    updateIcon() {
        const icon = document.getElementById('darkModeIcon');
        const toggle = document.getElementById('darkModeToggle');
        
        if (icon && toggle) {
            const isDark = this.theme === 'dark';
            icon.className = isDark ? 'bi bi-sun-fill' : 'bi bi-moon-fill';
            toggle.title = isDark ? 'Switch to Light Mode' : 'Switch to Dark Mode';
        }
    }

    bindEvents() {
        const toggle = document.getElementById('darkModeToggle');
        if (toggle) {
            toggle.addEventListener('click', () => this.toggleTheme());
        }

        // Listen for system theme changes
        window.matchMedia('(prefers-color-scheme: dark)').addEventListener('change', (e) => {
            if (this.getStoredTheme() === 'auto') {
                this.setTheme('auto');
            }
        });
    }

    showThemeChangeToast(theme) {
        const message = theme === 'dark' ? 'Dark mode enabled' : 'Light mode enabled';
        const type = theme === 'dark' ? 'info' : 'success';
        
        // Use existing toast functionality if available
        if (typeof RBACUtils !== 'undefined' && RBACUtils.showToast) {
            RBACUtils.showToast(message, type);
        } else {
            // Fallback toast implementation
            this.createToast(message, type);
        }
    }

    createToast(message, type) {
        const toastHtml = `
            <div class="toast align-items-center text-white bg-${type} border-0 position-fixed top-0 end-0 m-3" 
                 role="alert" style="z-index: 9999;">
                <div class="d-flex">
                    <div class="toast-body">
                        <i class="bi bi-${type === 'dark' ? 'moon' : 'sun'}-fill me-2"></i>${message}
                    </div>
                    <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
                </div>
            </div>
        `;
        
        document.body.insertAdjacentHTML('beforeend', toastHtml);
        const toastElement = document.querySelector('.toast:last-child');
        const toast = new bootstrap.Toast(toastElement);
        toast.show();
        
        // Remove toast element after it's hidden
        toastElement.addEventListener('hidden.bs.toast', () => {
            toastElement.remove();
        });
    }

    // Public methods for external use
    getCurrentTheme() {
        return this.theme;
    }

    isDarkMode() {
        return this.theme === 'dark';
    }

    setAutoTheme() {
        this.setTheme('auto');
    }
}

// Initialize dark mode when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    window.darkMode = new DarkModeManager();
});

// Export for module systems
if (typeof module !== 'undefined' && module.exports) {
    module.exports = DarkModeManager;
}
