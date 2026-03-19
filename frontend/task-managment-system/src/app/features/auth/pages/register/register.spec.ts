import { TestBed } from '@angular/core/testing';
import { Register } from './register';
import { AuthService } from '../../data/auth.service';
import { Router } from '@angular/router';
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

describe('Register', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Register],
      providers: [
        {
          provide: AuthService,
          useValue: {
            register: () => ({ subscribe: (callback: (response: object) => void) => callback({}) }),
          },
        },
        {
          provide: Router,
          useValue: { navigate: () => Promise.resolve(true) },
        },
      ],
    })
      .overrideComponent(Register, {
        set: {
          imports: [ReactiveFormsModule, RouterLinkStub],
        },
      })
      .compileComponents();
  });

  it('should create', () => {
    const fixture = TestBed.createComponent(Register);
    const component = fixture.componentInstance;
    expect(component).toBeTruthy();
  });
});
