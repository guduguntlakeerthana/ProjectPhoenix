import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface TaskRequest {
  title: string;
  description: string;
  status: string; // 'TODO' | 'IN_PROGRESS' | 'DONE'
  priority: string; // 'LOW' | 'MEDIUM' | 'HIGH'
  dueDate: string; // 'YYYY-MM-DD'
  progress: number;
  projectId: number;
}

export interface TaskResponse {
  id: number;
  title: string;
  description: string;
  status: string;
  priority: string;
  dueDate: string;
  progress: number;
  projectId: number;
  projectTitle: string;
  createdAt: string;
  updatedAt: string;
}

@Injectable({
  providedIn: 'root'
})
export class TaskService {

  private baseUrl = 'http://localhost:9091/api/tasks';

  constructor(private http: HttpClient) {}

  getTasks(): Observable<TaskResponse[]> {
    return this.http.get<TaskResponse[]>(this.baseUrl);
  }

  getTasksByProject(projectId: number): Observable<TaskResponse[]> {
    return this.http.get<TaskResponse[]>(`${this.baseUrl}/project/${projectId}`);
  }

  getTaskById(id: number): Observable<TaskResponse> {
    return this.http.get<TaskResponse>(`${this.baseUrl}/${id}`);
  }

  createTask(data: TaskRequest): Observable<TaskResponse> {
    return this.http.post<TaskResponse>(this.baseUrl, data);
  }

  updateTask(id: number, data: TaskRequest): Observable<TaskResponse> {
    return this.http.put<TaskResponse>(`${this.baseUrl}/${id}`, data);
  }

  deleteTask(id: number): Observable<string> {
    return this.http.delete(`${this.baseUrl}/${id}`, { responseType: 'text' });
  }
}
