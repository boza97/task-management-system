import { TestBed } from '@angular/core/testing';
import { TaskAudit } from './task-audit';
import { Store } from '@ngrx/store';
import { TaskAuditService } from '../../data/audit.service';
import { of } from 'rxjs';

describe('TaskAudit', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TaskAudit],
      providers: [
        {
          provide: Store,
          useValue: {
            select: () => of(null),
          },
        },
        {
          provide: TaskAuditService,
          useValue: {
            getTaskAuditLogs: () => of([]),
          },
        },
      ],
    }).compileComponents();
  });

  it('should create', () => {
    const fixture = TestBed.createComponent(TaskAudit);
    const component = fixture.componentInstance;
    expect(component).toBeTruthy();
  });
});
