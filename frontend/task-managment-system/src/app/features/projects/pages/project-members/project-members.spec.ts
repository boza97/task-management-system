import { TestBed } from '@angular/core/testing';
import { ProjectMembers } from './project-members';
import { Store } from '@ngrx/store';
import { of } from 'rxjs';
import { vi } from 'vitest';

describe('ProjectMembers', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProjectMembers],
      providers: [
        {
          provide: Store,
          useValue: {
            select: () => of(null),
          },
        },
        {
          provide: 'ProjectMembersService',
          useValue: {
            listMembers: () => of([]),
            addMember: () => of({}),
            removeMember: () => of({}),
            changeRole: () => of({}),
          },
        },
        {
          provide: 'UserService',
          useValue: {
            getAllUsers: () => of([]),
          },
        },
        {
          provide: 'ToastService',
          useValue: {
            show: vi.fn(),
          },
        },
        {
          provide: 'TokenStorageService',
          useValue: {
            getUser: () => null,
          },
        },
      ],
    }).compileComponents();
  });

  it('should create', () => {
    const fixture = TestBed.createComponent(ProjectMembers);
    const component = fixture.componentInstance;
    expect(component).toBeTruthy();
  });
});
