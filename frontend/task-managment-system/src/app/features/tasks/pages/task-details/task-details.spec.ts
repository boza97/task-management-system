import { TestBed } from '@angular/core/testing';
import { TaskDetails } from './task-details';
import { Store } from '@ngrx/store';
import { ActivatedRoute } from '@angular/router';
import { ProjectMembersService } from '../../../projects/data/project-members.service';
import { TaskService } from '../../data/task.service';
import { of } from 'rxjs';
import { Directive, Input } from '@angular/core';
import { vi } from 'vitest';
import { TaskComments } from '../../comments/components/task-comments/task-comments';
import { TaskAudit } from '../../audit/components/task-audit/task-audit';

@Directive({
  // eslint-disable-next-line @angular-eslint/directive-selector
  selector: '[routerLink]',
  standalone: true,
})
class RouterLinkStub {
  @Input() routerLink!: string;
}

describe('TaskDetails', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TaskDetails],
      providers: [
        {
          provide: Store,
          useValue: {
            select: () => of(null),
            dispatch: vi.fn(),
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
        {
          provide: ProjectMembersService,
          useValue: {
            listMembers: () => of([]),
          },
        },
        {
          provide: TaskService,
          useValue: {
            getTaskStatuses: () => of([]),
          },
        },
      ],
    })
      .overrideComponent(TaskDetails, {
        set: {
          imports: [RouterLinkStub, TaskComments, TaskAudit],
        },
      })
      .compileComponents();
  });

  it('should create', () => {
    const fixture = TestBed.createComponent(TaskDetails);
    const component = fixture.componentInstance;
    expect(component).toBeTruthy();
  });
});
