import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { API_BASE_URL } from '../config/api.config';

export interface AiChatResponse {
  response: string;
  simulated: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class AiServiceClient {

  private baseUrl = `${API_BASE_URL}/api/ai`;

  constructor(private http: HttpClient) {}

  askAi(prompt: string): Observable<AiChatResponse> {
    return this.http.post<AiChatResponse>(`${this.baseUrl}/chat`, { prompt });
  }
}
