import { TestBed } from '@angular/core/testing';
import { TaskCreate } from './task-create';
import { Store } from '@ngrx/store';
import { TaskService } from '../../data/task.service';
import { ProjectMembersService } from '../../../projects/data/project-members.service';
import { Router, ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';
import { Directive, Input } from '@angular/core';
import { vi } from 'vitest';
import { ReactiveFormsModule } from '@angular/forms';

@Directive({
  // eslint-disable-next-line @angular-eslint/directive-selector
  selector: '[routerLink]',
  standalone: true,
})
class RouterLinkStub {
  @Input() routerLink!: string;
}

describe('TaskCreate', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TaskCreate],
      providers: [
        {
          provide: Store,
          useValue: {
            select: () => of(null),
          },
        },
        {
          provide: TaskService,
          useValue: {
            createTask: () => of({}),
          },
        },
        {
          provide: ProjectMembersService,
          useValue: {
            listMembers: () => of([]),
          },
        },
        {
          provide: Router,
          useValue: {
            navigate: vi.fn(),
          },
        },
        {
          provide: ActivatedRoute,
          useValue: {},
        },
      ],
    })
      .overrideComponent(TaskCreate, {
        set: {
          imports: [ReactiveFormsModule, RouterLinkStub],
        },
      })
      .compileComponents();
  });

  it('should create', () => {
    const fixture = TestBed.createComponent(TaskCreate);
    const component = fixture.componentInstance;
    expect(component).toBeTruthy();
  });
});
