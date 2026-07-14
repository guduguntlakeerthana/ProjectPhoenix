import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface DocRequest {
  title: string;
  content: string;
  category: string; // 'SETUP' | 'API' | 'ARCH' | 'OTHER'
  projectId: number;
}

export interface DocResponse {
  id: number;
  title: string;
  content: string;
  category: string;
  projectId: number;
  projectTitle: string;
  createdAt: string;
  updatedAt: string;
}

import { API_BASE_URL } from '../config/api.config';

@Injectable({
  providedIn: 'root'
})
export class DocService {

  private baseUrl = `${API_BASE_URL}/api/docs`;

  constructor(private http: HttpClient) {}

  getDocs(): Observable<DocResponse[]> {
    return this.http.get<DocResponse[]>(this.baseUrl);
  }

  getDocsByProject(projectId: number): Observable<DocResponse[]> {
    return this.http.get<DocResponse[]>(`${this.baseUrl}/project/${projectId}`);
  }

  getDocById(id: number): Observable<DocResponse> {
    return this.http.get<DocResponse>(`${this.baseUrl}/${id}`);
  }

  createDoc(data: DocRequest): Observable<DocResponse> {
    return this.http.post<DocResponse>(this.baseUrl, data);
  }

  updateDoc(id: number, data: DocRequest): Observable<DocResponse> {
    return this.http.put<DocResponse>(`${this.baseUrl}/${id}`, data);
  }

  deleteDoc(id: number): Observable<string> {
    return this.http.delete(`${this.baseUrl}/${id}`, { responseType: 'text' });
  }
}
