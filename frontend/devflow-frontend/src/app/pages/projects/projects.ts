import { Component, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProjectService, ProjectRequest, ProjectResponse } from '../../services/project';

@Component({
  selector: 'app-projects',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './projects.html',
  styleUrl: './projects.css'
})
export class Projects implements OnInit {

  projects = signal<ProjectResponse[]>([]);
  isLoading = signal(true);
  searchQuery = signal('');
  statusFilter = signal('ALL');

  // Modal signals
  isModalOpen = signal(false);
  isEditing = signal(false);
  selectedProjectId = signal<number | null>(null);

  // Form signals
  title = signal('');
  description = signal('');
  techStack = signal('');
  status = signal('PENDING');
  startDate = signal('');
  endDate = signal('');
  githubLink = signal('');
  liveDemoLink = signal('');
  formError = signal('');

  constructor(private projectService: ProjectService) {}

  ngOnInit(): void {
    this.loadProjects();
  }

  loadProjects(): void {
    this.isLoading.set(true);
    this.projectService.getProjects().subscribe({
      next: (data) => {
        this.projects.set(data);
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('Failed to load projects', err);
        this.isLoading.set(false);
      }
    });
  }

  // Computed signal for filtered list of projects
  filteredProjects = computed(() => {
    let list = this.projects();

    // Filter by search query
    const query = this.searchQuery().trim().toLowerCase();
    if (query) {
      list = list.filter(p => 
        p.title.toLowerCase().includes(query) || 
        (p.description && p.description.toLowerCase().includes(query)) ||
        (p.techStack && p.techStack.toLowerCase().includes(query))
      );
    }

    // Filter by status
    const status = this.statusFilter();
    if (status !== 'ALL') {
      list = list.filter(p => p.status === status);
    }

    return list;
  });

  // Project statistics computed signals
  totalProjects = computed(() => this.projects().length);
  completedProjects = computed(() => this.projects().filter(p => p.status === 'COMPLETED').length);
  inProgressProjects = computed(() => this.projects().filter(p => p.status === 'IN_PROGRESS').length);
  pendingProjects = computed(() => this.projects().filter(p => p.status === 'PENDING').length);

  openCreateModal(): void {
    this.isEditing.set(false);
    this.selectedProjectId.set(null);
    this.resetForm();
    this.isModalOpen.set(true);
  }

  openEditModal(project: ProjectResponse): void {
    this.isEditing.set(true);
    this.selectedProjectId.set(project.id);
    
    this.title.set(project.title);
    this.description.set(project.description || '');
    this.techStack.set(project.techStack || '');
    this.status.set(project.status || 'PENDING');
    this.startDate.set(project.startDate || '');
    this.endDate.set(project.endDate || '');
    this.githubLink.set(project.githubLink || '');
    this.liveDemoLink.set(project.liveDemoLink || '');
    this.formError.set('');
    
    this.isModalOpen.set(true);
  }

  closeModal(): void {
    this.isModalOpen.set(false);
    this.resetForm();
  }

  resetForm(): void {
    this.title.set('');
    this.description.set('');
    this.techStack.set('');
    this.status.set('PENDING');
    this.startDate.set('');
    this.endDate.set('');
    this.githubLink.set('');
    this.liveDemoLink.set('');
    this.formError.set('');
  }

  saveProject(): void {
    if (!this.title().trim()) {
      this.formError.set('Project title is required');
      return;
    }

    const projectData: ProjectRequest = {
      title: this.title().trim(),
      description: this.description().trim(),
      techStack: this.techStack().trim(),
      status: this.status(),
      startDate: this.startDate() || '',
      endDate: this.endDate() || '',
      githubLink: this.githubLink().trim(),
      liveDemoLink: this.liveDemoLink().trim()
    };

    if (this.isEditing()) {
      const id = this.selectedProjectId();
      if (id !== null) {
        this.projectService.updateProject(id, projectData).subscribe({
          next: () => {
            this.loadProjects();
            this.closeModal();
          },
          error: (err) => {
            console.error('Failed to update project', err);
            this.formError.set(err.error?.message || 'Error occurred updating project');
          }
        });
      }
    } else {
      this.projectService.createProject(projectData).subscribe({
        next: () => {
          this.loadProjects();
          this.closeModal();
        },
        error: (err) => {
          console.error('Failed to create project', err);
          this.formError.set(err.error?.message || 'Error occurred creating project');
        }
      });
    }
  }

  deleteProject(id: number): void {
    if (confirm('Are you sure you want to delete this project?')) {
      this.projectService.deleteProject(id).subscribe({
        next: () => {
          this.loadProjects();
        },
        error: (err) => {
          console.error('Failed to delete project', err);
          alert('Error deleting project. Please try again.');
        }
      });
    }
  }
}
