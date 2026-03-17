import { Component, DestroyRef, effect, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { filter, map } from 'rxjs';
import { DatePipe } from '@angular/common';
import { Store } from '@ngrx/store';
import { selectProject } from '../../../projects/data/store/project.selectors';
import { Task } from '../../data/models/task.model';
import { TokenStorageService } from '../../../../shared/services/token-storage.service';
import {
  selectTaskListError,
  selectTaskListLoading,
  selectTasks,
} from '../../data/store/task.selectors';
import { deleteTask, loadTasks, setTaskFilters } from '../../data/store/task.actions';
import { TaskFilter } from '../../components/task-filter/task-filter';
import { TaskFilters } from '../../data/models/task-filters.model';
import { TaskService } from '../../data/task.service';
import { ProjectMembersService } from '../../../projects/data/project-members.service';
import { ProjectMember } from '../../../projects/data/models/project-member.model';

@Component({
  selector: 'app-task-list',
  imports: [DatePipe, RouterLink, TaskFilter],
  templateUrl: './task-list.html',
  styleUrl: './task-list.scss',
})
export class TaskList {
  private readonly store = inject(Store);
  private readonly tokenStorageService = inject(TokenStorageService);
  private readonly taskService = inject(TaskService);
  private readonly projectMembersService = inject(ProjectMembersService);
  private readonly destroyRef = inject(DestroyRef);

  loading = toSignal(this.store.select(selectTaskListLoading));
  error = toSignal(this.store.select(selectTaskListError));
  tasks = toSignal(this.store.select(selectTasks));
  projectId = toSignal(
    this.store.select(selectProject).pipe(
      filter((x) => !!x),
      map((x) => x.id),
    ),
  );
  statuses = toSignal(this.taskService.getTaskStatuses(), { initialValue: [] });
  members = signal<ProjectMember[]>([]);

  constructor() {
    effect(() => {
      const projectId = this.projectId();
      if (projectId) {
        this.store.dispatch(loadTasks({ projectId }));
        this.loadProjectMembers(projectId);
      }
    });
  }

  onDeleteClick(event: MouseEvent, taskId: string) {
    event.stopPropagation();
    if (!confirm('Delete this task?')) {
      return;
    }

    this.store.dispatch(deleteTask({ taskId }));
  }

  onFiltersChanged(filters: TaskFilters) {
    this.store.dispatch(setTaskFilters({ filters }));
  }

  canDeleteTask(task: Task) {
    const currentUser = this.tokenStorageService.getUser();
    return currentUser?.roles.includes('ADMIN') || currentUser?.sub === task.createdById;
  }

  private loadProjectMembers(projectId: string) {
    this.projectMembersService
      .listMembers(projectId)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (list) => {
          this.members.set(list);
        },
      });
  }
}
