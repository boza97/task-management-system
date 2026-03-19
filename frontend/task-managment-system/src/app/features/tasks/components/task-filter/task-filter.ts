import { Component, inject, input, output } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { TaskPriority } from '../../data/models/create-task-request.model';
import { ProjectMember } from '../../../projects/data/models/project-member.model';
import { TaskStatus } from '../../data/models/task-status.model';
import { TaskFilters } from '../../data/models/task-filters.model';
import { debounceTime, distinctUntilChanged } from 'rxjs';

@Component({
  selector: 'app-task-filter',
  imports: [ReactiveFormsModule],
  templateUrl: './task-filter.html',
  styleUrl: './task-filter.scss',
})
export class TaskFilter {
  statuses = input.required<TaskStatus[]>();
  members = input.required<ProjectMember[]>();
  filtersChanged = output<TaskFilters>();

  private readonly fb = inject(FormBuilder);

  taskPriorityEnum = TaskPriority;
  filters = this.fb.nonNullable.group({
    search: ['' as string],
    priority: [null as TaskPriority | null],
    statusCode: [null as string | null],
    assigneeId: [null as string | null],
    dueDateFrom: [null as string | null],
    dueDateTo: [null as string | null],
  });

  constructor() {
    this.filters.valueChanges.pipe(debounceTime(300), distinctUntilChanged()).subscribe(() => {
      this.filtersChanged.emit(this.filters.getRawValue());
    });
  }

  resetFilters() {
    this.filters.reset({
      search: '',
      priority: null,
      statusCode: null,
      assigneeId: null,
      dueDateFrom: null,
      dueDateTo: null,
    });
  }
}
