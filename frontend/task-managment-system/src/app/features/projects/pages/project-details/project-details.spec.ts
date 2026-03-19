import { TestBed } from '@angular/core/testing';
import { ProjectDetails } from './project-details';
import { Store } from '@ngrx/store';
import { of } from 'rxjs';

describe('ProjectDetails', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProjectDetails],
      providers: [
        {
          provide: Store,
          useValue: {
            select: () => of(null),
            dispatch: () => vi.fn(),
          },
        },
      ],
    }).compileComponents();
  });

  it('should create', () => {
    const fixture = TestBed.createComponent(ProjectDetails);
    const component = fixture.componentInstance;
    expect(component).toBeTruthy();
  });
});
