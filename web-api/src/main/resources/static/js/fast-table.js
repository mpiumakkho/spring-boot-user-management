/**
 * Fast Table Library - Optimized for RBAC System
 * Alternative to DataTable with better performance
 */

class FastTable {
    constructor(elementId, options = {}) {
        this.elementId = elementId;
        this.element = document.getElementById(elementId);
        this.options = {
            pageSize: 10,
            pageSizes: [5, 10, 25, 50, 100],
            searchable: true,
            sortable: true,
            responsive: true,
            language: 'th',
            ...options
        };
        
        this.data = [];
        this.filteredData = [];
        this.currentPage = 1;
        this.totalPages = 1;
        this.sortColumn = null;
        this.sortDirection = 'asc';
        this.searchQuery = '';
        
        this.init();
    }
    
    init() {
        if (!this.element) return;
        
        // Get data from existing table
        this.extractDataFromTable();
        
        // Create fast table structure
        this.createFastTable();
        
        // Render data
        this.render();
        
        // Show table with fade effect
        setTimeout(() => {
            this.element.style.display = 'block';
            this.element.style.opacity = '0';
            this.fadeIn(this.element, 300);
        }, 100);
    }
    
    extractDataFromTable() {
        const originalTable = this.element.querySelector('table');
        if (!originalTable) return;
        
        const headers = Array.from(originalTable.querySelectorAll('thead th')).map(th => ({
            title: th.textContent.trim(),
            field: th.textContent.trim().toLowerCase().replace(/\s+/g, '_'),
            sortable: !th.classList.contains('no-sort')
        }));
        
        const rows = Array.from(originalTable.querySelectorAll('tbody tr'));
        this.data = rows.map(row => {
            const cells = Array.from(row.querySelectorAll('td'));
            const rowData = {};
            
            headers.forEach((header, index) => {
                if (cells[index]) {
                    rowData[header.field] = cells[index].innerHTML;
                }
            });
            
            return rowData;
        });
        
        this.headers = headers;
        this.filteredData = [...this.data];
        this.calculatePagination();
    }
    
    createFastTable() {
        // Create DataTable-like wrapper
        const wrapper = document.createElement('div');
        wrapper.className = 'fast-table-wrapper';
        
        // Create controls section
        const controls = document.createElement('div');
        controls.className = 'fast-table-controls';
        
        if (this.options.searchable) {
            controls.appendChild(this.createSearchBar());
        }
        
        wrapper.appendChild(controls);
        
        // Create table container
        const tableContainer = document.createElement('div');
        tableContainer.className = 'fast-table-container';
        tableContainer.appendChild(this.createTable());
        
        wrapper.appendChild(tableContainer);
        
        // Create footer
        const footer = document.createElement('div');
        footer.className = 'fast-table-footer';
        
        const infoDiv = document.createElement('div');
        infoDiv.className = 'fast-table-info';
        infoDiv.id = 'fast-table-info';
        footer.appendChild(infoDiv);
        
        const paginationDiv = document.createElement('div');
        paginationDiv.className = 'fast-table-pagination';
        paginationDiv.appendChild(this.createPagination());
        footer.appendChild(paginationDiv);
        
        wrapper.appendChild(footer);
        
        // Replace original content
        this.element.innerHTML = '';
        this.element.appendChild(wrapper);
    }
    
    createSearchBar() {
        const searchContainer = document.createElement('div');
        searchContainer.className = 'd-flex justify-content-between align-items-center';
        
        // Left side - Show entries
        const entriesContainer = document.createElement('div');
        entriesContainer.className = 'entries-label';
        
        const entriesLabel = document.createElement('span');
        entriesLabel.textContent = 'Show';
        
        const pageSizeSelect = document.createElement('select');
        pageSizeSelect.className = 'form-select form-select-sm';
        pageSizeSelect.style.width = 'auto';
        pageSizeSelect.style.display = 'inline-block';
        
        this.options.pageSizes.forEach(size => {
            const option = document.createElement('option');
            option.value = size;
            option.textContent = size;
            option.selected = size === this.options.pageSize;
            pageSizeSelect.appendChild(option);
        });
        
        pageSizeSelect.addEventListener('change', (e) => {
            this.options.pageSize = parseInt(e.target.value);
            this.currentPage = 1;
            this.calculatePagination();
            this.render();
        });
        
        const entriesLabelEnd = document.createElement('span');
        entriesLabelEnd.textContent = 'entries per page';
        
        entriesContainer.appendChild(entriesLabel);
        entriesContainer.appendChild(pageSizeSelect);
        entriesContainer.appendChild(entriesLabelEnd);
        
        // Right side - Search input
        const searchGroup = document.createElement('div');
        searchGroup.className = 'input-group';
        searchGroup.style.width = '250px';
        
        const searchLabel = document.createElement('span');
        searchLabel.className = 'input-group-text';
        searchLabel.innerHTML = '<i class="bi bi-search"></i>';
        
        const searchInput = document.createElement('input');
        searchInput.type = 'text';
        searchInput.className = 'form-control';
        searchInput.placeholder = 'Search...';
        searchInput.addEventListener('input', (e) => {
            this.search(e.target.value);
        });
        
        searchGroup.appendChild(searchLabel);
        searchGroup.appendChild(searchInput);
        
        searchContainer.appendChild(entriesContainer);
        searchContainer.appendChild(searchGroup);
        
        return searchContainer;
    }
    
    createTable() {
        const tableContainer = document.createElement('div');
        tableContainer.className = 'table-responsive';
        
        const table = document.createElement('table');
        table.className = 'table table-bordered table-hover';
        
        // Header
        const thead = document.createElement('thead');
        thead.className = 'table-dark';
        const headerRow = document.createElement('tr');
        
        this.headers.forEach(header => {
            const th = document.createElement('th');
            th.textContent = header.title;
            
            if (header.sortable) {
                th.className = 'sortable';
                th.addEventListener('click', () => {
                    this.sort(header.field);
                });
                
                const sortIcon = document.createElement('span');
                sortIcon.className = 'sort-icon';
                sortIcon.innerHTML = '<i class="bi bi-arrow-down-up"></i>';
                th.appendChild(sortIcon);
            }
            
            headerRow.appendChild(th);
        });
        
        thead.appendChild(headerRow);
        table.appendChild(thead);
        
        // Body
        const tbody = document.createElement('tbody');
        tbody.id = 'fast-table-body';
        table.appendChild(tbody);
        
        tableContainer.appendChild(table);
        return tableContainer;
    }
    
    createPagination() {
        const paginationContainer = document.createElement('nav');
        paginationContainer.setAttribute('aria-label', 'Table pagination');
        
        const pagination = document.createElement('ul');
        pagination.className = 'pagination mb-0';
        pagination.id = 'fast-table-pagination';
        
        paginationContainer.appendChild(pagination);
        return paginationContainer;
    }
    
    search(query) {
        this.searchQuery = query.toLowerCase();
        
        if (this.searchQuery === '') {
            this.filteredData = [...this.data];
        } else {
            this.filteredData = this.data.filter(row => {
                return Object.values(row).some(value => {
                    const textValue = this.stripHtml(value.toString()).toLowerCase();
                    return textValue.includes(this.searchQuery);
                });
            });
        }
        
        this.currentPage = 1;
        this.calculatePagination();
        this.render();
    }
    
    sort(field) {
        if (this.sortColumn === field) {
            this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
        } else {
            this.sortColumn = field;
            this.sortDirection = 'asc';
        }
        
        this.filteredData.sort((a, b) => {
            const aValue = this.stripHtml(a[field] || '').toLowerCase();
            const bValue = this.stripHtml(b[field] || '').toLowerCase();
            
            let result = 0;
            if (aValue < bValue) result = -1;
            if (aValue > bValue) result = 1;
            
            return this.sortDirection === 'desc' ? -result : result;
        });
        
        this.currentPage = 1;
        this.updateSortIcons();
        this.render();
    }
    
    updateSortIcons() {
        // Reset all sort icons
        const sortableHeaders = this.element.querySelectorAll('th.sortable');
        sortableHeaders.forEach(th => {
            th.classList.remove('sorted');
            const icon = th.querySelector('.sort-icon i');
            if (icon) {
                icon.className = 'bi bi-arrow-down-up';
            }
        });
        
        // Update current sort column icon
        const currentHeader = Array.from(sortableHeaders).find(th => {
            const headerText = th.textContent.trim();
            const headerField = headerText.toLowerCase().replace(/\s+/g, '_');
            return headerField === this.sortColumn;
        });
        
        if (currentHeader) {
            currentHeader.classList.add('sorted');
            const icon = currentHeader.querySelector('.sort-icon i');
            if (icon) {
                if (this.sortDirection === 'asc') {
                    icon.className = 'bi bi-arrow-up';
                } else {
                    icon.className = 'bi bi-arrow-down';
                }
            }
        }
    }
    
    calculatePagination() {
        this.totalPages = Math.ceil(this.filteredData.length / this.options.pageSize);
        if (this.currentPage > this.totalPages) {
            this.currentPage = Math.max(1, this.totalPages);
        }
    }
    
    render() {
        this.renderTable();
        this.renderPagination();
        this.renderInfo();
    }
    
    renderTable() {
        const tbody = document.getElementById('fast-table-body');
        if (!tbody) return;
        
        tbody.innerHTML = '';
        
        const start = (this.currentPage - 1) * this.options.pageSize;
        const end = Math.min(start + this.options.pageSize, this.filteredData.length);
        const pageData = this.filteredData.slice(start, end);
        
        if (pageData.length === 0) {
            const emptyRow = document.createElement('tr');
            const emptyCell = document.createElement('td');
            emptyCell.colSpan = this.headers.length;
            emptyCell.className = 'text-center text-muted py-4';
            emptyCell.innerHTML = '<i class="bi bi-inbox me-2"></i>No data found';
            emptyRow.appendChild(emptyCell);
            tbody.appendChild(emptyRow);
            return;
        }
        
        pageData.forEach(rowData => {
            const row = document.createElement('tr');
            
            this.headers.forEach(header => {
                const cell = document.createElement('td');
                cell.innerHTML = rowData[header.field] || '';
                row.appendChild(cell);
            });
            
            tbody.appendChild(row);
        });
    }
    
    renderPagination() {
        const pagination = document.getElementById('fast-table-pagination');
        if (!pagination) return;
        
        pagination.innerHTML = '';
        
        if (this.totalPages <= 1) return;
        
        // Previous button
        const prevLi = document.createElement('li');
        prevLi.className = `page-item ${this.currentPage === 1 ? 'disabled' : ''}`;
        const prevLink = document.createElement('a');
        prevLink.className = 'page-link';
        prevLink.href = '#';
        prevLink.textContent = 'Previous';
        prevLink.addEventListener('click', (e) => {
            e.preventDefault();
            if (this.currentPage > 1) {
                this.currentPage--;
                this.render();
            }
        });
        prevLi.appendChild(prevLink);
        pagination.appendChild(prevLi);
        
        // Page numbers
        const startPage = Math.max(1, this.currentPage - 2);
        const endPage = Math.min(this.totalPages, this.currentPage + 2);
        
        for (let i = startPage; i <= endPage; i++) {
            const li = document.createElement('li');
            li.className = `page-item ${i === this.currentPage ? 'active' : ''}`;
            const link = document.createElement('a');
            link.className = 'page-link';
            link.href = '#';
            link.textContent = i;
            link.addEventListener('click', (e) => {
                e.preventDefault();
                this.currentPage = i;
                this.render();
            });
            li.appendChild(link);
            pagination.appendChild(li);
        }
        
        // Next button
        const nextLi = document.createElement('li');
        nextLi.className = `page-item ${this.currentPage === this.totalPages ? 'disabled' : ''}`;
        const nextLink = document.createElement('a');
        nextLink.className = 'page-link';
        nextLink.href = '#';
        nextLink.textContent = 'Next';
        nextLink.addEventListener('click', (e) => {
            e.preventDefault();
            if (this.currentPage < this.totalPages) {
                this.currentPage++;
                this.render();
            }
        });
        nextLi.appendChild(nextLink);
        pagination.appendChild(nextLi);
    }
    
    renderInfo() {
        const info = document.getElementById('fast-table-info');
        if (!info) return;
        
        const start = (this.currentPage - 1) * this.options.pageSize + 1;
        const end = Math.min(this.currentPage * this.options.pageSize, this.filteredData.length);
        
        info.textContent = `Showing ${start} to ${end} of ${this.filteredData.length} entries`;
        
        if (this.searchQuery) {
            info.textContent += ` (filtered from ${this.data.length} total entries)`;
        }
    }
    
    stripHtml(html) {
        const div = document.createElement('div');
        div.innerHTML = html;
        return div.textContent || div.innerText || '';
    }
    
    fadeIn(element, duration) {
        let opacity = 0;
        element.style.opacity = opacity;
        
        const timer = setInterval(() => {
            opacity += 50 / duration;
            if (opacity >= 1) {
                clearInterval(timer);
                opacity = 1;
            }
            element.style.opacity = opacity;
        }, 50);
    }
}

// Auto-initialize tables
document.addEventListener('DOMContentLoaded', function() {
    // Find tables with fast-table class
    const fastTables = document.querySelectorAll('.fast-table');
    
    fastTables.forEach(table => {
        if (table.querySelector('table')) {
            new FastTable(table.id, {
                pageSize: 10,
                searchable: true,
                sortable: true
            });
        }
    });
});

// Export for manual initialization
window.FastTable = FastTable; 