import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ProjectResponse } from './project';
import { TaskResponse } from './task';
import { NoteResponse } from './note';
import { DocResponse } from './doc';

export interface GlobalSearchResponse {
  projects: ProjectResponse[];
  tasks: TaskResponse[];
  notes: NoteResponse[];
  docs: DocResponse[];
}

@Injectable({
  providedIn: 'root'
})
export class GlobalSearchService {

  private baseUrl = 'http://localhost:9091/api/search';

  constructor(private http: HttpClient) {}

  search(q: string): Observable<GlobalSearchResponse> {
    const params = new HttpParams().set('q', q);
    return this.http.get<GlobalSearchResponse>(this.baseUrl, { params });
  }
}
