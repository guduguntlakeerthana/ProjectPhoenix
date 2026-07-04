import { Component, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DocService, DocRequest, DocResponse } from '../../services/doc';
import { ProjectService, ProjectResponse } from '../../services/project';

@Component({
  selector: 'app-docs',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './docs.html',
  styleUrl: './docs.css'
})
export class Docs implements OnInit {

  docs = signal<DocResponse[]>([]);
  projects = signal<ProjectResponse[]>([]);
  isLoading = signal(true);

  // Filters
  projectFilter = signal('ALL');
  categoryFilter = signal('ALL');
  searchQuery = signal('');

  // Modal signals
  isModalOpen = signal(false);
  isEditing = signal(false);
  selectedDocId = signal<number | null>(null);

  // Selection signal for docs viewer panel
  selectedDoc = signal<DocResponse | null>(null);

  // Form inputs
  title = signal('');
  content = signal('');
  category = signal('SETUP');
  projectId = signal<number | null>(null);
  formError = signal('');

  constructor(
    private docService: DocService,
    private projectService: ProjectService
  ) {}

  ngOnInit(): void {
    this.loadDocs();
    this.loadProjects();
  }

  loadDocs(): void {
    this.isLoading.set(true);
    this.docService.getDocs().subscribe({
      next: (data) => {
        this.docs.set(data);
        if (data.length > 0) {
          this.selectedDoc.set(data[0]);
        } else {
          this.selectedDoc.set(null);
        }
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('Failed to load docs', err);
        this.isLoading.set(false);
      }
    });
  }

  loadProjects(): void {
    this.projectService.getProjects('', 'ALL', 0, 100, 'title', 'asc').subscribe({
      next: (response) => {
        this.projects.set(response.content);
      },
      error: (err) => {
        console.error('Failed to load projects for selector', err);
      }
    });
  }

  // Reactive computed documentation search & filter list
  filteredDocs = computed(() => {
    let list = this.docs();

    // Text search
    const query = this.searchQuery().trim().toLowerCase();
    if (query) {
      list = list.filter(d => 
        d.title.toLowerCase().includes(query) || 
        (d.content && d.content.toLowerCase().includes(query))
      );
    }

    // Project filtering
    const pFilter = this.projectFilter();
    if (pFilter !== 'ALL') {
      const pId = parseInt(pFilter, 10);
      list = list.filter(d => d.projectId === pId);
    }

    // Category filtering
    const cat = this.categoryFilter();
    if (cat !== 'ALL') {
      list = list.filter(d => d.category === cat);
    }

    return list;
  });

  openCreateModal(): void {
    this.isEditing.set(false);
    this.selectedDocId.set(null);
    this.resetForm();

    // Auto-select first project if available
    if (this.projects().length > 0) {
      this.projectId.set(this.projects()[0].id);
    }

    this.isModalOpen.set(true);
  }

  openEditModal(doc: DocResponse): void {
    this.isEditing.set(true);
    this.selectedDocId.set(doc.id);
    this.title.set(doc.title);
    this.content.set(doc.content || '');
    this.category.set(doc.category || 'SETUP');
    this.projectId.set(doc.projectId);
    this.formError.set('');
    this.isModalOpen.set(true);
  }

  closeModal(): void {
    this.isModalOpen.set(false);
    this.resetForm();
  }

  resetForm(): void {
    this.title.set('');
    this.content.set('');
    this.category.set('SETUP');
    this.projectId.set(null);
    this.formError.set('');
  }

  saveDoc(): void {
    if (!this.title().trim()) {
      this.formError.set('Title is required');
      return;
    }
    if (this.projectId() === null) {
      this.formError.set('Project is required');
      return;
    }

    const docData: DocRequest = {
      title: this.title().trim(),
      content: this.content().trim(),
      category: this.category(),
      projectId: this.projectId()!
    };

    if (this.isEditing()) {
      const id = this.selectedDocId();
      if (id !== null) {
        this.docService.updateDoc(id, docData).subscribe({
          next: () => {
            this.loadDocs();
            this.closeModal();
          },
          error: (err) => {
            console.error('Failed to update doc', err);
            this.formError.set(err.error?.message || 'Error updating documentation');
          }
        });
      }
    } else {
      this.docService.createDoc(docData).subscribe({
        next: () => {
          this.loadDocs();
          this.closeModal();
        },
        error: (err) => {
          console.error('Failed to create doc', err);
          this.formError.set(err.error?.message || 'Error creating documentation');
        }
      });
    }
  }

  deleteDoc(id: number): void {
    if (confirm('Are you sure you want to delete this documentation page?')) {
      this.docService.deleteDoc(id).subscribe({
        next: () => {
          this.loadDocs();
        },
        error: (err) => {
          console.error('Failed to delete doc', err);
          alert('Error deleting documentation. Please try again.');
        }
      });
    }
  }
}
