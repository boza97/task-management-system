/* eslint-disable @angular-eslint/directive-selector */
import { TestBed } from '@angular/core/testing';
import { ProjectShell } from './project-shell';
import { Store } from '@ngrx/store';
import { ActivatedRoute } from '@angular/router';
import { Directive, Input } from '@angular/core';
import { vi } from 'vitest';
import { ProjectHeader } from '../../components/project-header/project-header';
import { of } from 'rxjs';

@Directive({ selector: '[routerLink]', standalone: true })
class RouterLinkStub {
  @Input() routerLink!: string;
}

@Directive({ selector: '[routerLinkActive]', standalone: true })
class RouterLinkActiveStub {
  @Input() routerLinkActive!: boolean;
}

@Directive({ selector: 'router-outlet', standalone: true })
class RouterOutletStub {}

describe('ProjectShell', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProjectShell],
      providers: [
        {
          provide: Store,
          useValue: {
            dispatch: vi.fn(),
            select: () => of(null),
          },
        },
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: {
                get: () => null,
              },
            },
          },
        },
      ],
    })
      .overrideComponent(ProjectShell, {
        set: {
          imports: [RouterOutletStub, RouterLinkStub, RouterLinkActiveStub, ProjectHeader],
        },
      })
      .compileComponents();
  });

  it('should create', () => {
    const fixture = TestBed.createComponent(ProjectShell);
    const component = fixture.componentInstance;
    expect(component).toBeTruthy();
  });
});
