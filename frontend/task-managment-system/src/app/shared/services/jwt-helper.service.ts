import { Injectable } from '@angular/core';
import { jwtDecode } from 'jwt-decode';
import { JwtPayload } from '../models/jwt-payload.model';

@Injectable({ providedIn: 'root' })
export class JwtHelperService {
  decode(token: string): JwtPayload {
    return jwtDecode<JwtPayload>(token);
  }

  isExpired(token: string): boolean {
    const decoded = this.decode(token);
    return Date.now() >= decoded.exp * 1000;
  }

  getExpirationDate(token: string): number {
    const decoded = this.decode(token);
    return decoded.exp * 1000;
  }
}
