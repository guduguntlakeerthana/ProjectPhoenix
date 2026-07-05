import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ReportResponse {
  totalProjects: number;
  totalTasks: number;
  completedTasks: number;
  inProgressTasks: number;
  reviewTasks: number;
  todoTasks: number;
  averageTaskProgress: number;
  highPriorityTasks: number;
  mediumPriorityTasks: number;
  lowPriorityTasks: number;
}

@Injectable({
  providedIn: 'root'
})
export class ReportService {

  private baseUrl = 'http://localhost:9091/api/reports';

  constructor(private http: HttpClient) {}

  getReportSummary(): Observable<ReportResponse> {
    return this.http.get<ReportResponse>(`${this.baseUrl}/summary`);
  }
}
