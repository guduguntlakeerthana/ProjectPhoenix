import { Component, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProjectService, ProjectResponse } from '../../services/project';
import { TaskService, TaskResponse } from '../../services/task';

export interface CalendarDay {
  date: Date;
  dayNumber: number;
  isCurrentMonth: boolean;
  isToday: boolean;
  tasks: TaskResponse[];
  projects: ProjectResponse[];
}

@Component({
  selector: 'app-calendar',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './calendar.html',
  styleUrl: './calendar.css'
})
export class Calendar implements OnInit {

  projects = signal<ProjectResponse[]>([]);
  tasks = signal<TaskResponse[]>([]);
  
  // Date signals
  currentYear = signal<number>(new Date().getFullYear());
  currentMonth = signal<number>(new Date().getMonth()); // 0-indexed
  selectedDate = signal<Date>(new Date());

  monthNames = [
    'January', 'February', 'March', 'April', 'May', 'June',
    'July', 'August', 'September', 'October', 'November', 'December'
  ];

  dayNames = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];

  constructor(
    private projectService: ProjectService,
    private taskService: TaskService
  ) {}

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    // Fetch all user projects & tasks unpaginated
    this.projectService.getAllProjects().subscribe({
      next: (projs) => this.projects.set(projs),
      error: (err) => console.error('Failed to load projects for calendar', err)
    });

    this.taskService.getTasks().subscribe({
      next: (tsks) => this.tasks.set(tsks),
      error: (err) => console.error('Failed to load tasks for calendar', err)
    });
  }

  // Generate grid days
  calendarDays = computed<CalendarDay[]>(() => {
    const year = this.currentYear();
    const month = this.currentMonth();
    const firstDayIndex = new Date(year, month, 1).getDay();
    const totalDays = new Date(year, month + 1, 0).getDate();
    const prevTotalDays = new Date(year, month, 0).getDate();

    const days: CalendarDay[] = [];
    const today = new Date();

    // Previous month padding
    for (let i = firstDayIndex - 1; i >= 0; i--) {
      const d = new Date(year, month - 1, prevTotalDays - i);
      days.push(this.buildCalendarDay(d, false, today));
    }

    // Current month days
    for (let i = 1; i <= totalDays; i++) {
      const d = new Date(year, month, i);
      days.push(this.buildCalendarDay(d, true, today));
    }

    // Next month padding to fill 42 cells (6 rows * 7 days)
    const totalCells = 42;
    const remainingCells = totalCells - days.length;
    for (let i = 1; i <= remainingCells; i++) {
      const d = new Date(year, month + 1, i);
      days.push(this.buildCalendarDay(d, false, today));
    }

    return days;
  });

  private buildCalendarDay(d: Date, isCurrentMonth: boolean, today: Date): CalendarDay {
    const dString = this.formatDateString(d);

    // Filter tasks due on this date
    const dayTasks = this.tasks().filter(t => t.dueDate === dString);

    // Filter projects ending on this date
    const dayProjects = this.projects().filter(p => p.endDate === dString);

    const isToday = d.getDate() === today.getDate() &&
                    d.getMonth() === today.getMonth() &&
                    d.getFullYear() === today.getFullYear();

    return {
      date: d,
      dayNumber: d.getDate(),
      isCurrentMonth,
      isToday,
      tasks: dayTasks,
      projects: dayProjects
    };
  }

  // Helper date format: YYYY-MM-DD
  private formatDateString(d: Date): string {
    const y = d.getFullYear();
    const m = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    return `${y}-${m}-${day}`;
  }

  // Selected date events
  selectedDayEvents = computed(() => {
    const selected = this.selectedDate();
    const dString = this.formatDateString(selected);
    
    return {
      date: selected,
      tasks: this.tasks().filter(t => t.dueDate === dString),
      projects: this.projects().filter(p => p.endDate === dString)
    };
  });

  selectDay(day: CalendarDay): void {
    this.selectedDate.set(day.date);
  }

  prevMonth(): void {
    if (this.currentMonth() === 0) {
      this.currentMonth.set(11);
      this.currentYear.update(y => y - 1);
    } else {
      this.currentMonth.update(m => m - 1);
    }
  }

  nextMonth(): void {
    if (this.currentMonth() === 11) {
      this.currentMonth.set(0);
      this.currentYear.update(y => y + 1);
    } else {
      this.currentMonth.update(m => m + 1);
    }
  }

  // Jump to today
  goToday(): void {
    const today = new Date();
    this.currentYear.set(today.getFullYear());
    this.currentMonth.set(today.getMonth());
    this.selectedDate.set(today);
  }
}
