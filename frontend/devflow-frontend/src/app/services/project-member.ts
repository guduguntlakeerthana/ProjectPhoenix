import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ProjectMemberRequest {
  email: string;
  role: string; // 'COLLABORATOR' | 'VIEWER'
  projectId: number;
}

export interface ProjectMemberResponse {
  id: number;
  email: string;
  role: string;
  projectId: number;
  projectTitle: string;
  invitedAt: string;
}

import { API_BASE_URL } from '../config/api.config';

@Injectable({
  providedIn: 'root'
})
export class ProjectMemberService {

  private baseUrl = `${API_BASE_URL}/api/project-members`;

  constructor(private http: HttpClient) {}

  inviteMember(data: ProjectMemberRequest): Observable<ProjectMemberResponse> {
    return this.http.post<ProjectMemberResponse>(this.baseUrl, data);
  }

  getProjectMembers(projectId: number): Observable<ProjectMemberResponse[]> {
    return this.http.get<ProjectMemberResponse[]>(`${this.baseUrl}/project/${projectId}`);
  }

  removeMember(id: number): Observable<string> {
    return this.http.delete(`${this.baseUrl}/${id}`, { responseType: 'text' });
  }
}
