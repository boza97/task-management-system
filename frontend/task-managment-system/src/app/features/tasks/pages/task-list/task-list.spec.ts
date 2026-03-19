import { TestBed } from '@angular/core/testing';
import { TaskList } from './task-list';
import { Store } from '@ngrx/store';
import { TokenStorageService } from '../../../../shared/services/token-storage.service';
import { TaskService } from '../../data/task.service';
import { ProjectMembersService } from '../../../projects/data/project-members.service';
import { of } from 'rxjs';
import { Directive, Input } from '@angular/core';
import { vi } from 'vitest';
import { TaskFilter } from '../../components/task-filter/task-filter';

@Directive({
  // eslint-disable-next-line @angular-eslint/directive-selector
  selector: '[routerLink]',
  standalone: true,
})
class RouterLinkStub {
  @Input() routerLink!: string;
}

describe('TaskList', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TaskList],
      providers: [
        {
          provide: Store,
          useValue: {
            select: () => of(null),
            dispatch: vi.fn(),
          },
        },
        {
          provide: TokenStorageService,
          useValue: {
            getUser: () => ({
              sub: 'user1',
              roles: ['USER'],
            }),
          },
        },
        {
          provide: TaskService,
          useValue: {
            getTaskStatuses: () => of([]),
          },
        },
        {
          provide: ProjectMembersService,
          useValue: {
            listMembers: () => of([]),
          },
        },
      ],
    })
      .overrideComponent(TaskList, {
        set: {
          imports: [RouterLinkStub, TaskFilter],
        },
      })
      .compileComponents();
  });

  it('should create', () => {
    const fixture = TestBed.createComponent(TaskList);
    const component = fixture.componentInstance;
    expect(component).toBeTruthy();
  });
});
