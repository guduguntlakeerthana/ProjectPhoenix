import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
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

export interface PaginatedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
  empty: boolean;
}

export interface ProjectStatsResponse {
  totalProjects: number;
  completedProjects: number;
  inProgressProjects: number;
  pendingProjects: number;
}

@Injectable({
  providedIn: 'root'
})
export class ProjectService {

  private baseUrl = 'http://localhost:9091/api/projects';

  constructor(private http: HttpClient) {}

  getProjects(
    search?: string,
    status?: string,
    page: number = 0,
    size: number = 10,
    sortBy: string = 'createdAt',
    direction: string = 'desc'
  ): Observable<PaginatedResponse<ProjectResponse>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sortBy)
      .set('direction', direction);

    if (search) {
      params = params.set('search', search);
    }
    if (status && status !== 'ALL') {
      params = params.set('status', status);
    }

    return this.http.get<PaginatedResponse<ProjectResponse>>(this.baseUrl, { params });
  }

  getProjectStats(): Observable<ProjectStatsResponse> {
    return this.http.get<ProjectStatsResponse>(`${this.baseUrl}/stats`);
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

  getAllProjects(): Observable<ProjectResponse[]> {
    return this.http.get<ProjectResponse[]>(`${this.baseUrl}/all`);
  }
}
