import { Component, DestroyRef, inject } from '@angular/core';
import {
  AbstractControl,
  FormBuilder,
  ReactiveFormsModule,
  ValidationErrors,
  ValidatorFn,
  Validators,
} from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../data/auth.service';
import { RegisterRequest } from '../../data/models/register-request.model';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { HttpErrorResponse } from '@angular/common/http';
import { ApiErrorResponse } from '../../../../shared/models/api-error-response.model';

@Component({
  selector: 'app-register',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './register.html',
  styleUrl: './register.scss',
})
export class Register {
  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly router = inject(Router);

  private readonly passwordMatchValidator = (): ValidatorFn => {
    return (form: AbstractControl): ValidationErrors | null => {
      const password = form.get('password')?.value;
      const confirmPassword = form.get('confirmPassword')?.value;

      if (!password || !confirmPassword) {
        return null;
      }

      return password === confirmPassword ? null : { passwordMismatch: true };
    };
  };

  registerForm = this.fb.nonNullable.group(
    {
      firstName: ['', [Validators.required]],
      lastName: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', [Validators.required]],
    },
    { validators: this.passwordMatchValidator() },
  );

  constructor() {
    this.registerForm.valueChanges.pipe(takeUntilDestroyed(this.destroyRef)).subscribe(() => {
      if (this.registerForm.errors?.['serverError']) {
        const { serverError, ...rest } = this.registerForm.errors;
        this.registerForm.setErrors(Object.keys(rest).length ? rest : null);
      }

      Object.values(this.registerForm.controls).forEach((control) => {
        this.removeServerError(control);
      });
    });
  }

  submit() {
    if (this.registerForm.invalid) {
      return;
    }

    const { firstName, lastName, email, password } = this.registerForm.getRawValue();
    const registerRequest: RegisterRequest = { firstName, lastName, email, password };

    this.authService
      .register(registerRequest)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          this.router.navigate(['/login']);
        },
        error: (error) => {
          this.handleRegisterError(error);
        },
      });
  }

  private handleRegisterError(err: HttpErrorResponse) {
    const apiError = err.error as ApiErrorResponse | null;
    if (apiError?.errors) {
      Object.entries(apiError.errors).forEach(([field, message]) => {
        this.registerForm.get(field)?.setErrors({
          serverError: message,
        });
      });
    }

    this.registerForm.setErrors({ serverError: 'Registration failed.' });
  }

  private removeServerError(control: AbstractControl) {
    if (!control.errors?.['serverError']) {
      return;
    }

    const { serverError, ...rest } = control.errors;
    control.setErrors(Object.keys(rest).length ? rest : null);
  }
}
