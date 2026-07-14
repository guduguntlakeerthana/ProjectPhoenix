import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface AttachmentResponse {
  id: number;
  fileName: string;
  fileType: string;
  fileSize: number;
  projectId?: number;
  taskId?: number;
  createdAt: string;
}

import { API_BASE_URL } from '../config/api.config';

@Injectable({
  providedIn: 'root'
})
export class AttachmentService {

  private baseUrl = `${API_BASE_URL}/api/attachments`;

  constructor(private http: HttpClient) {}

  uploadFile(file: File, projectId?: number, taskId?: number): Observable<AttachmentResponse> {
    const formData = new FormData();
    formData.append('file', file);
    if (projectId) {
      formData.append('projectId', projectId.toString());
    }
    if (taskId) {
      formData.append('taskId', taskId.toString());
    }
    return this.http.post<AttachmentResponse>(`${this.baseUrl}/upload`, formData);
  }

  getProjectAttachments(projectId: number): Observable<AttachmentResponse[]> {
    return this.http.get<AttachmentResponse[]>(`${this.baseUrl}/project/${projectId}`);
  }

  getTaskAttachments(taskId: number): Observable<AttachmentResponse[]> {
    return this.http.get<AttachmentResponse[]>(`${this.baseUrl}/task/${taskId}`);
  }

  deleteAttachment(id: number): Observable<string> {
    return this.http.delete(`${this.baseUrl}/${id}`, { responseType: 'text' });
  }

  getDownloadUrl(id: number): string {
    return `${this.baseUrl}/download/${id}`;
  }
}
