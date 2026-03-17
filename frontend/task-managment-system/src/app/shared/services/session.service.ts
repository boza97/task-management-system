import { Injectable, inject } from '@angular/core';
import { Router } from '@angular/router';
import { TokenStorageService } from './token-storage.service';
import { JwtHelperService } from './jwt-helper.service';

@Injectable({ providedIn: 'root' })
export class SessionService {
  private readonly tokenStorage = inject(TokenStorageService);
  private readonly jwtHelper = inject(JwtHelperService);
  private readonly router = inject(Router);

  private logoutTimer?: ReturnType<typeof setTimeout>;

  startSessionTimer(token: string) {
    if (this.logoutTimer) {
      clearTimeout(this.logoutTimer);
    }

    const expirationTime = this.jwtHelper.getExpirationDate(token);
    const timeout = expirationTime - Date.now();

    if (timeout <= 0) {
      this.logout();
      return;
    }

    this.logoutTimer = setTimeout(() => {
      this.logout();
    }, timeout);
  }

  logout() {
    this.tokenStorage.clear();

    this.router.navigate(['/login'], {
      queryParams: { sessionExpired: true },
    });
  }
}
