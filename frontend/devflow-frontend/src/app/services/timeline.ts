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

@Injectable({
  providedIn: 'root'
})
export class TimelineService {

  private baseUrl = 'http://localhost:9091/api/audit-logs';

  constructor(private http: HttpClient) {}

  getMyLogs(): Observable<AuditLogResponse[]> {
    return this.http.get<AuditLogResponse[]>(this.baseUrl);
  }
}
