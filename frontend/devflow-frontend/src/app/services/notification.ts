import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface NotificationResponse {
  id: number;
  message: string;
  isRead: boolean;
  createdAt: string;
}

import { API_BASE_URL } from '../config/api.config';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {

  private baseUrl = `${API_BASE_URL}/api/notifications`;

  constructor(private http: HttpClient) {}

  getNotifications(): Observable<NotificationResponse[]> {
    return this.http.get<NotificationResponse[]>(this.baseUrl);
  }

  markAsRead(id: number): Observable<NotificationResponse> {
    return this.http.put<NotificationResponse>(`${this.baseUrl}/${id}/read`, {});
  }

  markAllAsRead(): Observable<string> {
    return this.http.put(`${this.baseUrl}/read-all`, {}, { responseType: 'text' });
  }

  deleteNotification(id: number): Observable<string> {
    return this.http.delete(`${this.baseUrl}/${id}`, { responseType: 'text' });
  }
}
