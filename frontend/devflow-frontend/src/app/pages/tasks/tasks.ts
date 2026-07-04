import { Component, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TaskService, TaskRequest, TaskResponse } from '../../services/task';
import { ProjectService, ProjectResponse } from '../../services/project';

@Component({
  selector: 'app-tasks',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './tasks.html',
  styleUrl: './tasks.css'
})
export class Tasks implements OnInit {

  tasks = signal<TaskResponse[]>([]);
  projects = signal<ProjectResponse[]>([]);
  isLoading = signal(true);

  // Client side filters
  statusFilter = signal('ALL');
  priorityFilter = signal('ALL');
  projectFilter = signal('ALL');

  // Modal signals
  isModalOpen = signal(false);
  isEditing = signal(false);
  selectedTaskId = signal<number | null>(null);

  // Form signals
  title = signal('');
  description = signal('');
  status = signal('TODO');
  priority = signal('MEDIUM');
  dueDate = signal('');
  progress = signal(0);
  projectId = signal<number | null>(null);
  formError = signal('');

  constructor(
    private taskService: TaskService,
    private projectService: ProjectService
  ) {}

  ngOnInit(): void {
    this.loadTasks();
    this.loadProjects();
  }

  loadTasks(): void {
    this.isLoading.set(true);
    this.taskService.getTasks().subscribe({
      next: (data) => {
        this.tasks.set(data);
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('Failed to load tasks', err);
        this.isLoading.set(false);
      }
    });
  }

  loadProjects(): void {
    // Load a large list of projects for selectors (e.g. page = 0, size = 100)
    this.projectService.getProjects('', 'ALL', 0, 100, 'title', 'asc').subscribe({
      next: (response) => {
        this.projects.set(response.content);
      },
      error: (err) => {
        console.error('Failed to load projects list for selector', err);
      }
    });
  }

  // Reactive computed filter list
  filteredTasks = computed(() => {
    let list = this.tasks();

    // Filter by status
    const status = this.statusFilter();
    if (status !== 'ALL') {
      list = list.filter(t => t.status === status);
    }

    // Filter by priority
    const priority = this.priorityFilter();
    if (priority !== 'ALL') {
      list = list.filter(t => t.priority === priority);
    }

    // Filter by project
    const pFilter = this.projectFilter();
    if (pFilter !== 'ALL') {
      const pId = parseInt(pFilter, 10);
      list = list.filter(t => t.projectId === pId);
    }

    return list;
  });

  // Task metrics
  totalTasks = computed(() => this.filteredTasks().length);
  todoTasks = computed(() => this.filteredTasks().filter(t => t.status === 'TODO').length);
  inProgressTasks = computed(() => this.filteredTasks().filter(t => t.status === 'IN_PROGRESS').length);
  completedTasks = computed(() => this.filteredTasks().filter(t => t.status === 'DONE').length);

  openCreateModal(): void {
    this.isEditing.set(false);
    this.selectedTaskId.set(null);
    this.resetForm();
    
    // Auto-select first project if available
    if (this.projects().length > 0) {
      this.projectId.set(this.projects()[0].id);
    }
    
    this.isModalOpen.set(true);
  }

  openEditModal(task: TaskResponse): void {
    this.isEditing.set(true);
    this.selectedTaskId.set(task.id);
    
    this.title.set(task.title);
    this.description.set(task.description || '');
    this.status.set(task.status || 'TODO');
    this.priority.set(task.priority || 'MEDIUM');
    this.dueDate.set(task.dueDate || '');
    this.progress.set(task.progress || 0);
    this.projectId.set(task.projectId);
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
    this.status.set('TODO');
    this.priority.set('MEDIUM');
    this.dueDate.set('');
    this.progress.set(0);
    this.projectId.set(null);
    this.formError.set('');
  }

  saveTask(): void {
    if (!this.title().trim()) {
      this.formError.set('Task title is required');
      return;
    }
    if (this.projectId() === null) {
      this.formError.set('Associated project is required');
      return;
    }

    const taskData: TaskRequest = {
      title: this.title().trim(),
      description: this.description().trim(),
      status: this.status(),
      priority: this.priority(),
      dueDate: this.dueDate() || '',
      progress: this.progress(),
      projectId: this.projectId()!
    };

    if (this.isEditing()) {
      const id = this.selectedTaskId();
      if (id !== null) {
        this.taskService.updateTask(id, taskData).subscribe({
          next: () => {
            this.loadTasks();
            this.closeModal();
          },
          error: (err) => {
            console.error('Failed to update task', err);
            this.formError.set(err.error?.message || 'Error occurred updating task');
          }
        });
      }
    } else {
      this.taskService.createTask(taskData).subscribe({
        next: () => {
          this.loadTasks();
          this.closeModal();
        },
        error: (err) => {
          console.error('Failed to create task', err);
          this.formError.set(err.error?.message || 'Error occurred creating task');
        }
      });
    }
  }

  deleteTask(id: number): void {
    if (confirm('Are you sure you want to delete this task?')) {
      this.taskService.deleteTask(id).subscribe({
        next: () => {
          this.loadTasks();
        },
        error: (err) => {
          console.error('Failed to delete task', err);
          alert('Error deleting task. Please try again.');
        }
      });
    }
  }
}
