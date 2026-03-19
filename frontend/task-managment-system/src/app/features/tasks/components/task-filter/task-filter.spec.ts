import { TestBed } from '@angular/core/testing';
import { TaskFilter } from './task-filter';

describe('TaskFilter', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TaskFilter],
    }).compileComponents();
  });

  it('should create', () => {
    const fixture = TestBed.createComponent(TaskFilter);
    const component = fixture.componentInstance;

    fixture.componentRef.setInput('statuses', []);
    fixture.componentRef.setInput('members', []);

    fixture.detectChanges();

    expect(component).toBeTruthy();
  });
});
