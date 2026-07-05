import { Component, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProjectService, ProjectRequest, ProjectResponse } from '../../services/project';
import { ProjectMemberService, ProjectMemberResponse } from '../../services/project-member';
import { AttachmentService, AttachmentResponse } from '../../services/attachment';

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

  // Filter & Pagination signals
  searchQuery = signal('');
  statusFilter = signal('ALL');
  pageNumber = signal(0);
  pageSize = signal(6); // 6 cards fits nicely on desktop grids
  totalElements = signal(0);
  totalPages = signal(0);
  sortBy = signal('createdAt');
  sortDir = signal('desc');

  // Statistics signals
  totalCount = signal(0);
  completedCount = signal(0);
  inProgressCount = signal(0);
  pendingCount = signal(0);

  // Modal signals
  isModalOpen = signal(false);
  isEditing = signal(false);
  selectedProjectId = signal<number | null>(null);

  // Collaboration Member signals
  isMembersModalOpen = signal(false);
  projectMembers = signal<ProjectMemberResponse[]>([]);
  memberEmail = signal('');
  memberRole = signal('COLLABORATOR');
  memberError = signal('');

  // Attachment signals
  isAttachmentsModalOpen = signal(false);
  projectAttachments = signal<AttachmentResponse[]>([]);
  selectedFile = signal<File | null>(null);
  attachmentError = signal('');

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

  constructor(
    private projectService: ProjectService,
    private projectMemberService: ProjectMemberService,
    private attachmentService: AttachmentService
  ) {}

  ngOnInit(): void {
    this.loadProjects();
    this.loadStats();
  }

  loadProjects(): void {
    this.isLoading.set(true);
    this.projectService.getProjects(
      this.searchQuery(),
      this.statusFilter(),
      this.pageNumber(),
      this.pageSize(),
      this.sortBy(),
      this.sortDir()
    ).subscribe({
      next: (response) => {
        this.projects.set(response.content);
        this.totalElements.set(response.totalElements);
        this.totalPages.set(response.totalPages);
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('Failed to load projects', err);
        this.isLoading.set(false);
      }
    });
  }

  loadStats(): void {
    this.projectService.getProjectStats().subscribe({
      next: (stats) => {
        this.totalCount.set(stats.totalProjects);
        this.completedCount.set(stats.completedProjects);
        this.inProgressCount.set(stats.inProgressProjects);
        this.pendingCount.set(stats.pendingProjects);
      },
      error: (err) => {
        console.error('Failed to load stats', err);
      }
    });
  }

  onSearchChange(query: string): void {
    this.searchQuery.set(query);
    this.pageNumber.set(0);
    this.loadProjects();
  }

  onFilterChange(status: string): void {
    this.statusFilter.set(status);
    this.pageNumber.set(0);
    this.loadProjects();
  }

  onSortChange(field: string): void {
    if (this.sortBy() === field) {
      this.sortDir.set(this.sortDir() === 'asc' ? 'desc' : 'asc');
    } else {
      this.sortBy.set(field);
      this.sortDir.set('desc');
    }
    this.pageNumber.set(0);
    this.loadProjects();
  }

  onSortParamChange(sortParam: string): void {
    const parts = sortParam.split(',');
    this.sortBy.set(parts[0]);
    this.sortDir.set(parts[1]);
    this.pageNumber.set(0);
    this.loadProjects();
  }

  // Pagination Actions
  nextPage(): void {
    if (this.pageNumber() < this.totalPages() - 1) {
      this.pageNumber.update(p => p + 1);
      this.loadProjects();
    }
  }

  prevPage(): void {
    if (this.pageNumber() > 0) {
      this.pageNumber.update(p => p - 1);
      this.loadProjects();
    }
  }

  goToPage(p: number): void {
    if (p >= 0 && p < this.totalPages()) {
      this.pageNumber.set(p);
      this.loadProjects();
    }
  }

  // Modal Actions
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
            this.loadStats();
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
          this.loadStats();
          this.closeModal();
        },
        error: (err) => {
          console.error('Failed to create project', err);
          this.formError.set(err.error?.message || 'Error occurred creating project. Verify that date ranges are correct.');
        }
      });
    }
  }

  deleteProject(id: number): void {
    if (confirm('Are you sure you want to delete this project?')) {
      this.projectService.deleteProject(id).subscribe({
        next: () => {
          this.loadProjects();
          this.loadStats();
        },
        error: (err) => {
          console.error('Failed to delete project', err);
          alert('Error deleting project. Please try again.');
        }
      });
    }
  }

  // Collaboration Member Methods
  openMembersModal(projectId: number): void {
    this.selectedProjectId.set(projectId);
    this.memberEmail.set('');
    this.memberRole.set('COLLABORATOR');
    this.memberError.set('');
    this.loadMembers();
    this.isMembersModalOpen.set(true);
  }

  closeMembersModal(): void {
    this.isMembersModalOpen.set(false);
    this.selectedProjectId.set(null);
  }

  loadMembers(): void {
    const id = this.selectedProjectId();
    if (id !== null) {
      this.projectMemberService.getProjectMembers(id).subscribe({
        next: (data) => {
          this.projectMembers.set(data);
        },
        error: (err) => {
          console.error('Failed to load project members', err);
        }
      });
    }
  }

  inviteMember(): void {
    const id = this.selectedProjectId();
    const email = this.memberEmail().trim();
    if (!email) {
      this.memberError.set('Collaborator email is required');
      return;
    }
    if (id !== null) {
      this.projectMemberService.inviteMember({
        email: email,
        role: this.memberRole(),
        projectId: id
      }).subscribe({
        next: () => {
          this.memberEmail.set('');
          this.memberError.set('');
          this.loadMembers();
        },
        error: (err) => {
          console.error('Failed to invite member', err);
          this.memberError.set(err.error?.message || 'Error occurred inviting collaborator');
        }
      });
    }
  }

  removeMember(memberId: number): void {
    if (confirm('Are you sure you want to remove this collaborator?')) {
      this.projectMemberService.removeMember(memberId).subscribe({
        next: () => {
          this.loadMembers();
        },
        error: (err) => {
          console.error('Failed to remove member', err);
          alert('Error removing member. Please try again.');
        }
      });
    }
  }

  // Attachment methods
  openAttachmentsModal(projectId: number): void {
    this.selectedProjectId.set(projectId);
    this.selectedFile.set(null);
    this.attachmentError.set('');
    this.loadAttachments();
    this.isAttachmentsModalOpen.set(true);
  }

  closeAttachmentsModal(): void {
    this.isAttachmentsModalOpen.set(false);
    this.selectedProjectId.set(null);
  }

  loadAttachments(): void {
    const id = this.selectedProjectId();
    if (id !== null) {
      this.attachmentService.getProjectAttachments(id).subscribe({
        next: (data) => {
          this.projectAttachments.set(data);
        },
        error: (err) => {
          console.error('Failed to load project attachments', err);
        }
      });
    }
  }

  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
      this.selectedFile.set(file);
    }
  }

  uploadAttachment(): void {
    const id = this.selectedProjectId();
    const file = this.selectedFile();
    if (!file) {
      this.attachmentError.set('Please select a file to upload');
      return;
    }
    if (id !== null) {
      this.attachmentService.uploadFile(file, id).subscribe({
        next: () => {
          this.selectedFile.set(null);
          this.attachmentError.set('');
          this.loadAttachments();
        },
        error: (err) => {
          console.error('Failed to upload file', err);
          this.attachmentError.set(err.error?.message || 'Error uploading file');
        }
      });
    }
  }

  downloadAttachment(attachment: AttachmentResponse): void {
    window.open(this.attachmentService.getDownloadUrl(attachment.id), '_blank');
  }

  deleteAttachment(attachmentId: number): void {
    if (confirm('Are you sure you want to delete this file?')) {
      this.attachmentService.deleteAttachment(attachmentId).subscribe({
        next: () => {
          this.loadAttachments();
        },
        error: (err) => {
          console.error('Failed to delete file', err);
          alert('Error deleting file. Please try again.');
        }
      });
    }
  }
}
