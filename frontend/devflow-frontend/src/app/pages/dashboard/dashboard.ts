import { Component, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ProjectService, ProjectResponse, ProjectStatsResponse } from '../../services/project';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css'
})
export class Dashboard implements OnInit {

  recentProjects = signal<ProjectResponse[]>([]);
  isLoading = signal(true);

  // Statistics signals
  totalProjects = signal(0);
  completedProjects = signal(0);
  inProgressProjects = signal(0);
  pendingProjects = signal(0);

  constructor(private projectService: ProjectService) {}

  ngOnInit(): void {
    this.loadDashboardData();
  }

  loadDashboardData(): void {
    this.isLoading.set(true);

    // Fetch stats
    this.projectService.getProjectStats().subscribe({
      next: (stats: ProjectStatsResponse) => {
        this.totalProjects.set(stats.totalProjects);
        this.completedProjects.set(stats.completedProjects);
        this.inProgressProjects.set(stats.inProgressProjects);
        this.pendingProjects.set(stats.pendingProjects);
      },
      error: (err) => {
        console.error('Failed to load project stats', err);
      }
    });

    // Fetch 3 most recent projects
    this.projectService.getProjects('', 'ALL', 0, 3, 'createdAt', 'desc').subscribe({
      next: (response) => {
        this.recentProjects.set(response.content);
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('Failed to load recent projects', err);
        this.isLoading.set(false);
      }
    });
  }
}
