import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../services/auth';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  templateUrl: './sidebar.html',
  styleUrl: './sidebar.css',
})
export class Sidebar implements OnInit {
  isAdmin = signal(false);

  navItems = [
    { label: 'Dashboard', route: '/dashboard', icon: 'grid' },
    { label: 'Projects', route: '/projects', icon: 'folder' },
    { label: 'Tasks', route: '/tasks', icon: 'check-square' },
    { label: 'Kanban Board', route: '/kanban', icon: 'trello' },
    { label: 'Calendar', route: '/calendar', icon: 'calendar' },
    { label: 'Activity Timeline', route: '/timeline', icon: 'activity' },
    { label: 'Productivity Reports', route: '/reports', icon: 'bar-chart' },
    { label: 'Notes', route: '/notes', icon: 'file-text' },
    { label: 'Docs', route: '/docs', icon: 'book' },
    { label: 'GitHub Tracking', route: '/github', icon: 'github' }
  ];

  constructor(private authService: AuthService) {}

  ngOnInit(): void {
    this.authService.getMe().subscribe({
      next: (user) => {
        if (user && user.role === 'ADMIN') {
          this.isAdmin.set(true);
        }
      },
      error: () => this.isAdmin.set(false)
    });
  }
}
