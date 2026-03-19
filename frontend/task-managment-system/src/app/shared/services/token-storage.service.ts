import { inject, Injectable } from '@angular/core';
import { JwtHelperService } from './jwt-helper.service';
import { JwtPayload } from '../models/jwt-payload.model';

@Injectable({ providedIn: 'root' })
export class TokenStorageService {
  private readonly KEY = 'token';
  private readonly jwtHelper = inject(JwtHelperService);

  set(token: string) {
    localStorage.setItem(this.KEY, token);
  }

  get(): string | null {
    return localStorage.getItem(this.KEY);
  }

  clear() {
    localStorage.removeItem(this.KEY);
  }

  getUser(): JwtPayload | null {
    const token = this.get();
    if (!token) {
      return null;
    }

    return this.jwtHelper.decode(token);
  }
}
