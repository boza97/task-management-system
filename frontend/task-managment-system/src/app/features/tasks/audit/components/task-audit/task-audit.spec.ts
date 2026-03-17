import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TaskAudit } from './task-audit';

describe('TaskAudit', () => {
  let component: TaskAudit;
  let fixture: ComponentFixture<TaskAudit>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TaskAudit],
    }).compileComponents();

    fixture = TestBed.createComponent(TaskAudit);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
