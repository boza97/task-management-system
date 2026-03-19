import { TestBed } from '@angular/core/testing';
import { ProjectHeader } from './project-header';
import { Store } from '@ngrx/store';
import { Router } from '@angular/router';
import { of } from 'rxjs';

describe('ProjectHeader', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProjectHeader],
      providers: [
        {
          provide: Store,
          useValue: {
            select: () => of(null),
          },
        },
        {
          provide: Router,
          useValue: {
            navigate: () => Promise.resolve(true),
          },
        },
      ],
    }).compileComponents();
  });

  it('should create', () => {
    const fixture = TestBed.createComponent(ProjectHeader);
    const component = fixture.componentInstance;
    expect(component).toBeTruthy();
  });
});
