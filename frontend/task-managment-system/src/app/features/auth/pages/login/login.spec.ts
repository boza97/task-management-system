import { TestBed } from '@angular/core/testing';
import { Login } from './login';
import { AuthService } from '../../data/auth.service';
import { TokenStorageService } from '../../../../shared/services/token-storage.service';
import { SessionService } from '../../../../shared/services/session.service';
import { provideRouter, Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { HttpErrorResponse } from '@angular/common/http';
import { vi } from 'vitest';
import { Directive, Input } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';

@Directive({
  // eslint-disable-next-line @angular-eslint/directive-selector
  selector: '[routerLink]',
  standalone: true,
})
class RouterLinkStub {
  @Input() routerLink!: string;
}

const TEST_PW = 'password123';

function createComponent() {
  const fixture = TestBed.createComponent(Login);
  const component = fixture.componentInstance;
  fixture.detectChanges();
  return { fixture, component };
}

describe('Login', () => {
  let authService: { login: ReturnType<typeof vi.fn> };
  let tokenStorage: { set: ReturnType<typeof vi.fn> };
  let sessionService: { startSessionTimer: ReturnType<typeof vi.fn> };
  let router: { navigate: ReturnType<typeof vi.fn> };

  beforeEach(async () => {
    authService = {
      login: vi.fn(),
    };

    tokenStorage = {
      set: vi.fn(),
    };

    sessionService = {
      startSessionTimer: vi.fn(),
    };

    router = {
      navigate: vi.fn(),
    };

    await TestBed.configureTestingModule({
      imports: [Login],
      providers: [
        provideRouter([]),
        { provide: AuthService, useValue: authService },
        { provide: TokenStorageService, useValue: tokenStorage },
        { provide: SessionService, useValue: sessionService },
        { provide: Router, useValue: router },
      ],
    })
      .overrideComponent(Login, {
        set: {
          imports: [ReactiveFormsModule, RouterLinkStub],
        },
      })
      .compileComponents();
  });

  it('should create', () => {
    const { component } = createComponent();
    expect(component).toBeTruthy();
  });

  it('should not submit if form is invalid', () => {
    const { component } = createComponent();

    component.submit();

    expect(authService.login).not.toHaveBeenCalled();
  });

  it('should login and navigate on success', () => {
    const { component } = createComponent();

    authService.login.mockReturnValue(of({ token: '123' }));

    component.loginForm.setValue({
      email: 'test@test.com',
      password: TEST_PW,
    });

    component.submit();

    expect(authService.login).toHaveBeenCalled();
    expect(tokenStorage.set).toHaveBeenCalledWith('123');
    expect(sessionService.startSessionTimer).toHaveBeenCalledWith('123');
    expect(router.navigate).toHaveBeenCalledWith(['/']);
  });

  it('should set server error on login failure', () => {
    const { component } = createComponent();

    const errorResponse = new HttpErrorResponse({
      error: {
        globalErrors: ['Invalid credentials'],
      },
    });

    authService.login.mockReturnValue(throwError(() => errorResponse));

    component.loginForm.setValue({
      email: 'test@test.com',
      password: TEST_PW,
    });

    component.submit();

    expect(component.loginForm.errors?.['serverError']).toContain('Invalid credentials');
  });

  it('should clear server error on form change', () => {
    const { component } = createComponent();

    component.loginForm.setErrors({
      serverError: 'Error',
    });

    component.loginForm.patchValue({
      email: 'new@test.com',
    });

    expect(component.loginForm.errors?.['serverError']).toBeFalsy();
  });
});
