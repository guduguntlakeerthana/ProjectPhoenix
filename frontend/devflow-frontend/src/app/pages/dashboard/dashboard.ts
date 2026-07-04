import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AnalyticsService, AnalyticsResponse } from '../../services/analytics';
import { ProjectResponse } from '../../services/project';
import { TaskResponse } from '../../services/task';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css'
})
export class Dashboard implements OnInit {

  isLoading = signal(true);

  // Statistics signals (Projects)
  totalProjects = signal(0);
  completedProjects = signal(0);
  inProgressProjects = signal(0);
  pendingProjects = signal(0);

  // Statistics signals (Tasks)
  totalTasks = signal(0);
  todoTasks = signal(0);
  inProgressTasks = signal(0);
  completedTasks = signal(0);

  // Priority breakdown signals
  lowPriorityTasks = signal(0);
  mediumPriorityTasks = signal(0);
  highPriorityTasks = signal(0);

  // Recent records lists
  recentProjects = signal<ProjectResponse[]>([]);
  recentTasks = signal<TaskResponse[]>([]);

  constructor(private analyticsService: AnalyticsService) {}

  ngOnInit(): void {
    this.loadAnalytics();
  }

  loadAnalytics(): void {
    this.isLoading.set(true);
    this.analyticsService.getAnalytics().subscribe({
      next: (data: AnalyticsResponse) => {
        // Map project stats
        this.totalProjects.set(data.totalProjects);
        this.completedProjects.set(data.completedProjects);
        this.inProgressProjects.set(data.inProgressProjects);
        this.pendingProjects.set(data.pendingProjects);

        // Map task stats
        this.totalTasks.set(data.totalTasks);
        this.todoTasks.set(data.todoTasks);
        this.inProgressTasks.set(data.inProgressTasks);
        this.completedTasks.set(data.completedTasks);

        // Map task priorities
        this.lowPriorityTasks.set(data.lowPriorityTasks);
        this.mediumPriorityTasks.set(data.mediumPriorityTasks);
        this.highPriorityTasks.set(data.highPriorityTasks);

        // Map recents
        this.recentProjects.set(data.recentProjects);
        this.recentTasks.set(data.recentTasks);

        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('Failed to load dashboard analytics', err);
        this.isLoading.set(false);
      }
    });
  }
}
