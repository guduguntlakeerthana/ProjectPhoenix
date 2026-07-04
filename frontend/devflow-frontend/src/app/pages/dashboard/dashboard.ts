import { Component, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ProjectService, ProjectResponse } from '../../services/project';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css'
})
export class Dashboard implements OnInit {

  projects = signal<ProjectResponse[]>([]);
  isLoading = signal(true);

  constructor(private projectService: ProjectService) {}

  ngOnInit(): void {
    this.loadProjects();
  }

  loadProjects() {
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

  totalProjects = computed(() => this.projects().length);
  
  completedProjects = computed(() => 
    this.projects().filter(p => p.status === 'COMPLETED').length
  );
  
  inProgressProjects = computed(() => 
    this.projects().filter(p => p.status === 'IN_PROGRESS').length
  );
  
  pendingProjects = computed(() => 
    this.projects().filter(p => p.status === 'PENDING').length
  );

  recentProjects = computed(() => {
    return [...this.projects()]
      .sort((a, b) => {
        const dateA = a.createdAt ? new Date(a.createdAt).getTime() : 0;
        const dateB = b.createdAt ? new Date(b.createdAt).getTime() : 0;
        return dateB - dateA;
      })
      .slice(0, 3);
  });
}
