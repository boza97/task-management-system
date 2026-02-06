import { HttpInterceptorFn } from '@angular/common/http';
import { TokenStorageService } from '../services/token-storage.service';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { Router } from '@angular/router';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const tokenStorage = inject(TokenStorageService);
  const token = tokenStorage.get();
  const router = inject(Router);

  if (!token || req.url.endsWith('/auth/login') || req.url.endsWith('/users/register')) {
    return next(req);
  }

  const authReq = req.clone({
    headers: req.headers.set('Authorization', `Bearer ${token}`),
  });

  return next(authReq).pipe(
    catchError((err) => {
      if (err.status === 401) {
        tokenStorage.clear();
        router.navigate(['/login'], {
          queryParams: { sessionExpired: true },
        });
      }

      return throwError(() => err);
    }),
  );
};
