import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ProjectRequest {
  title: string;
  description: string;
  techStack: string;
  status: string;
  startDate: string;
  endDate: string;
  githubLink: string;
  liveDemoLink: string;
}

export interface ProjectResponse {
  id: number;
  title: string;
  description: string;
  techStack: string;
  status: string;
  startDate: string;
  endDate: string;
  githubLink: string;
  liveDemoLink: string;
  createdAt: string;
  updatedAt: string;
}

@Injectable({
  providedIn: 'root'
})
export class ProjectService {

  private baseUrl = 'http://localhost:9091/api/projects';

  constructor(private http: HttpClient) {}

  getProjects(): Observable<ProjectResponse[]> {
    return this.http.get<ProjectResponse[]>(this.baseUrl);
  }

  createProject(data: ProjectRequest): Observable<ProjectResponse> {
    return this.http.post<ProjectResponse>(this.baseUrl, data);
  }

  updateProject(id: number, data: ProjectRequest): Observable<ProjectResponse> {
    return this.http.put<ProjectResponse>(`${this.baseUrl}/${id}`, data);
  }

  deleteProject(id: number): Observable<string> {
    return this.http.delete(`${this.baseUrl}/${id}`, { responseType: 'text' });
  }
}
