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

import { API_BASE_URL } from '../config/api.config';

@Injectable({
  providedIn: 'root'
})
export class ReportService {

  private baseUrl = `${API_BASE_URL}/api/reports`;

  constructor(private http: HttpClient) {}

  getReportSummary(): Observable<ReportResponse> {
    return this.http.get<ReportResponse>(`${this.baseUrl}/summary`);
  }
}
