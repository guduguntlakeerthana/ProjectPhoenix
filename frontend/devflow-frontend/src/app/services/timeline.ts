import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface AuditLogResponse {
  id: number;
  action: string;
  details: string;
  userEmail: string;
  createdAt: string;
}

import { API_BASE_URL } from '../config/api.config';

@Injectable({
  providedIn: 'root'
})
export class TimelineService {

  private baseUrl = `${API_BASE_URL}/api/audit-logs`;

  constructor(private http: HttpClient) {}

  getMyLogs(): Observable<AuditLogResponse[]> {
    return this.http.get<AuditLogResponse[]>(this.baseUrl);
  }
}
