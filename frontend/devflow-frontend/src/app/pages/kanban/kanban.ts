import { Component, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProjectService, ProjectResponse } from '../../services/project';
import { TaskService, TaskResponse } from '../../services/task';

@Component({
  selector: 'app-kanban',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './kanban.html',
  styleUrl: './kanban.css'
})
export class Kanban implements OnInit {

  projects = signal<ProjectResponse[]>([]);
  selectedProjectId = signal<number | null>(null);
  tasks = signal<TaskResponse[]>([]);

  columns = [
    { key: 'TODO', label: 'Todo', class: 'col-todo' },
    { key: 'IN_PROGRESS', label: 'In Progress', class: 'col-progress' },
    { key: 'REVIEW', label: 'Review', class: 'col-review' },
    { key: 'DONE', label: 'Completed', class: 'col-completed' }
  ];

  constructor(
    private projectService: ProjectService,
    private taskService: TaskService
  ) {}

  ngOnInit(): void {
    this.loadProjects();
  }

  loadProjects(): void {
    this.projectService.getAllProjects().subscribe({
      next: (data) => {
        this.projects.set(data);
        if (data.length > 0) {
          // Select first project by default
          this.selectedProjectId.set(data[0].id);
          this.loadTasks(data[0].id);
        }
      },
      error: (err) => console.error('Failed to load projects for Kanban', err)
    });
  }

  loadTasks(projectId: number): void {
    this.taskService.getTasksByProject(projectId).subscribe({
      next: (data) => this.tasks.set(data),
      error: (err) => console.error('Failed to load tasks for Kanban', err)
    });
  }

  onProjectChange(projectId: number): void {
    this.selectedProjectId.set(projectId);
    this.loadTasks(projectId);
  }

  // Get tasks matching a specific column status
  getTasksForColumn(statusKey: string): TaskResponse[] {
    return this.tasks().filter(t => t.status === statusKey);
  }

  moveTask(task: TaskResponse, newStatus: string): void {
    if (task.status === newStatus) return;

    this.taskService.updateTask(task.id, {
      title: task.title,
      description: task.description,
      status: newStatus,
      priority: task.priority,
      dueDate: task.dueDate,
      progress: task.progress,
      projectId: task.projectId
    }).subscribe({
      next: () => {
        const id = this.selectedProjectId();
        if (id) this.loadTasks(id);
      },
      error: (err) => {
        console.error('Failed to move task', err);
        alert(err.error?.message || 'Error moving task status');
      }
    });
  }
}
