import { HttpInterceptorFn } from '@angular/common/http';
import { TokenStorageService } from '../services/token-storage.service';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { Router } from '@angular/router';
import { ToastService } from '../services/toast.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const tokenStorage = inject(TokenStorageService);
  const router = inject(Router);
  const toastService = inject(ToastService);

  const token = tokenStorage.get();
  let authReq = req;
  const isAuthCall = req.url.endsWith('/auth/login') || req.url.endsWith('/users/register');

  if (token && !isAuthCall) {
    authReq = req.clone({
      headers: req.headers.set('Authorization', `Bearer ${token}`),
    });
  }

  return next(authReq).pipe(
    catchError((err) => {
      if (err.status === 401 && !isAuthCall) {
        tokenStorage.clear();

        router.navigate(['/login'], {
          queryParams: { sessionExpired: true },
        });
      }

      if (err.status === 403) {
        toastService.show("You don't have permission", 'error');
      }

      return throwError(() => err);
    }),
  );
};
