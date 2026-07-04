import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  email: string;
  fullName: string;
  role: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private baseUrl = 'http://localhost:9091/api/auth';

  constructor(private http: HttpClient) {}

  login(data: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.baseUrl}/login`, data);
  }

  getMe(): Observable<LoginResponse> {
    return this.http.get<LoginResponse>(`${this.baseUrl}/me`);
  }

  saveToken(token: string): void {
    localStorage.setItem('devflow_token', token);
  }

  getToken(): string | null {
    return localStorage.getItem('devflow_token');
  }

  logout(): void {
    localStorage.removeItem('devflow_token');
  }

  isLoggedIn(): boolean {
    return this.getToken() !== null;
  }
}
