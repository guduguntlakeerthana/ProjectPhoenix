import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface NoteRequest {
  title: string;
  content: string;
  projectId?: number | null;
}

export interface NoteResponse {
  id: number;
  title: string;
  content: string;
  projectId?: number | null;
  projectTitle?: string | null;
  createdAt: string;
  updatedAt: string;
}

import { API_BASE_URL } from '../config/api.config';

@Injectable({
  providedIn: 'root'
})
export class NoteService {

  private baseUrl = `${API_BASE_URL}/api/notes`;

  constructor(private http: HttpClient) {}

  getNotes(): Observable<NoteResponse[]> {
    return this.http.get<NoteResponse[]>(this.baseUrl);
  }

  getNotesByProject(projectId: number): Observable<NoteResponse[]> {
    return this.http.get<NoteResponse[]>(`${this.baseUrl}/project/${projectId}`);
  }

  getNoteById(id: number): Observable<NoteResponse> {
    return this.http.get<NoteResponse>(`${this.baseUrl}/${id}`);
  }

  createNote(data: NoteRequest): Observable<NoteResponse> {
    return this.http.post<NoteResponse>(this.baseUrl, data);
  }

  updateNote(id: number, data: NoteRequest): Observable<NoteResponse> {
    return this.http.put<NoteResponse>(`${this.baseUrl}/${id}`, data);
  }

  deleteNote(id: number): Observable<string> {
    return this.http.delete(`${this.baseUrl}/${id}`, { responseType: 'text' });
  }
}
