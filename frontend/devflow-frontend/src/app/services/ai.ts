import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface AiChatResponse {
  response: string;
  simulated: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class AiServiceClient {

  private baseUrl = 'http://localhost:9091/api/ai';

  constructor(private http: HttpClient) {}

  askAi(prompt: string): Observable<AiChatResponse> {
    return this.http.post<AiChatResponse>(`${this.baseUrl}/chat`, { prompt });
  }
}
