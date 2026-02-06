import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { RegisterRequest } from './models/register-request.model';
import { environment } from '../../../../environments/environment';
import { Observable } from 'rxjs';
import { LoginRequest } from './models/login-request.model';
import { LoginResponse } from './models/login-response.model';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly http = inject(HttpClient);

  public login(data: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(environment.apiUrl + '/auth/login', data);
  }

  public register(registerRequest: RegisterRequest): Observable<void> {
    return this.http.post<void>(environment.apiUrl + '/users/register', registerRequest);
  }
}
