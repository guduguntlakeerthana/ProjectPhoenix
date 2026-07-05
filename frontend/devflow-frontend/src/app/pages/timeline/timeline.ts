import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TimelineService, AuditLogResponse } from '../../services/timeline';

@Component({
  selector: 'app-timeline',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './timeline.html',
  styleUrl: './timeline.css'
})
export class Timeline implements OnInit {

  logs = signal<AuditLogResponse[]>([]);
  isLoading = signal(true);

  constructor(private timelineService: TimelineService) {}

  ngOnInit(): void {
    this.loadLogs();
  }

  loadLogs(): void {
    this.timelineService.getMyLogs().subscribe({
      next: (data) => {
        this.logs.set(data);
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('Failed to load activity logs', err);
        this.isLoading.set(false);
      }
    });
  }

  // Get dynamic background and text colors for actions to look premium
  getActionClass(action: string): string {
    if (action.includes('CREATED')) return 'act-create';
    if (action.includes('UPDATED')) return 'act-update';
    if (action.includes('DELETED')) return 'act-delete';
    return 'act-default';
  }
}
