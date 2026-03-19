import { Component, DestroyRef, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../data/auth.service';
import { TokenStorageService } from '../../../../shared/services/token-storage.service';
import { LoginRequest } from '../../data/models/login-request.model';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ApiErrorResponse } from '../../../../shared/models/api-error-response.model';
import { SessionService } from '../../../../shared/services/session.service';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.scss',
})
export class Login {
  private readonly fb = inject(FormBuilder);
  private readonly destroyRef = inject(DestroyRef);
  private readonly router = inject(Router);
  private readonly authService = inject(AuthService);
  private readonly tokenStorage = inject(TokenStorageService);
  private readonly sessionService = inject(SessionService);

  loginForm = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required]],
  });

  constructor() {
    this.loginForm.valueChanges.pipe(takeUntilDestroyed()).subscribe(() => {
      if (this.loginForm.errors?.['serverError']) {
        const { serverError: _serverError, ...rest } = this.loginForm.errors;
        this.loginForm.setErrors(Object.keys(rest).length ? rest : null);
      }
    });
  }

  submit() {
    if (this.loginForm.invalid) {
      return;
    }

    const loginRequest: LoginRequest = {
      email: this.loginForm.value.email!,
      password: this.loginForm.value.password!,
    };
    this.authService
      .login(loginRequest)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (response) => {
          this.tokenStorage.set(response.token);
          this.sessionService.startSessionTimer(response.token);
          this.router.navigate(['/']);
        },
        error: (err) => this.handleLoginError(err),
      });
  }

  private handleLoginError(err: HttpErrorResponse): void {
    const apiError = err.error as ApiErrorResponse | null;

    if (apiError?.globalErrors?.length) {
      this.loginForm.setErrors({
        serverError: apiError.globalErrors.join(', '),
      });
    }
  }
}
