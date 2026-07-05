import { Component, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReportService, ReportResponse } from '../../services/report';

@Component({
  selector: 'app-reports',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './reports.html',
  styleUrl: './reports.css'
})
export class Reports implements OnInit {

  summary = signal<ReportResponse | null>(null);
  isLoading = signal(true);

  constructor(private reportService: ReportService) {}

  ngOnInit(): void {
    this.loadReport();
  }

  loadReport(): void {
    this.reportService.getReportSummary().subscribe({
      next: (data) => {
        this.summary.set(data);
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('Failed to load productivity report', err);
        this.isLoading.set(false);
      }
    });
  }

  // Percentage calculations
  taskCompletionRate = computed(() => {
    const s = this.summary();
    if (!s || s.totalTasks === 0) return 0;
    return Math.round((s.completedTasks / s.totalTasks) * 100);
  });

  taskProgressRate = computed(() => {
    const s = this.summary();
    if (!s) return 0;
    return Math.round(s.averageTaskProgress);
  });

  // Calculate percentages for priorities
  priorityPercentages = computed(() => {
    const s = this.summary();
    if (!s || s.totalTasks === 0) return { high: 0, medium: 0, low: 0 };
    return {
      high: Math.round((s.highPriorityTasks / s.totalTasks) * 100),
      medium: Math.round((s.mediumPriorityTasks / s.totalTasks) * 100),
      low: Math.round((s.lowPriorityTasks / s.totalTasks) * 100)
    };
  });

  // Export report trigger
  printReport(): void {
    window.print();
  }
}
