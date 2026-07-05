import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive } from '@angular/router';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  templateUrl: './sidebar.html',
  styleUrl: './sidebar.css',
})
export class Sidebar {
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
}
