import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuditLogResponse } from './timeline';
import { API_BASE_URL } from '../config/api.config';

export interface AdminUserResponse {
  id: number;
  fullName: string;
  email: string;
  role: string;
  createdAt: string;
}

export interface AdminStatsResponse {
  totalUsers: number;
  totalProjects: number;
  totalTasks: number;
  totalAttachments: number;
}

@Injectable({
  providedIn: 'root'
})
export class AdminService {

  private baseUrl = `${API_BASE_URL}/api/admin`;

  constructor(private http: HttpClient) {}

  getUsers(): Observable<AdminUserResponse[]> {
    return this.http.get<AdminUserResponse[]>(`${this.baseUrl}/users`);
  }

  updateUserRole(userId: number, role: string): Observable<AdminUserResponse> {
    const params = new HttpParams().set('role', role);
    return this.http.put<AdminUserResponse>(`${this.baseUrl}/users/${userId}/role`, null, { params });
  }

  getGlobalStats(): Observable<AdminStatsResponse> {
    return this.http.get<AdminStatsResponse>(`${this.baseUrl}/stats`);
  }

  getGlobalAuditLogs(): Observable<AuditLogResponse[]> {
    return this.http.get<AuditLogResponse[]>(`${this.baseUrl}/audit-logs`);
  }
}
