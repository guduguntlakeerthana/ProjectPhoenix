import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ProjectResponse } from './project';
import { TaskResponse } from './task';

export interface AnalyticsResponse {
  totalProjects: number;
  completedProjects: number;
  inProgressProjects: number;
  pendingProjects: number;

  totalTasks: number;
  todoTasks: number;
  inProgressTasks: number;
  completedTasks: number;

  lowPriorityTasks: number;
  mediumPriorityTasks: number;
  highPriorityTasks: number;

  recentProjects: ProjectResponse[];
  recentTasks: TaskResponse[];
}

import { API_BASE_URL } from '../config/api.config';

@Injectable({
  providedIn: 'root'
})
export class AnalyticsService {

  private baseUrl = `${API_BASE_URL}/api/analytics`;

  constructor(private http: HttpClient) {}

  getAnalytics(): Observable<AnalyticsResponse> {
    return this.http.get<AnalyticsResponse>(this.baseUrl);
  }
}
