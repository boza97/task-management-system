import { Component, inject, signal } from '@angular/core';
import { ProjectService } from '../../data/project.service';
import { toSignal } from '@angular/core/rxjs-interop';
import { catchError, of, tap } from 'rxjs';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-project-list',
  imports: [DatePipe],
  templateUrl: './project-list.html',
  styleUrl: './project-list.scss',
})
export class ProjectList {
  private readonly projectService = inject(ProjectService);

  loading = signal(true);
  error = signal<string | null>(null);
  projects = toSignal(
    this.projectService.getProjects().pipe(
      tap(() => {
        this.loading.set(false);
        this.error.set(null);
      }),
      catchError(() => {
        this.loading.set(false);
        this.error.set('Failed to load projects');
        return of([]);
      }),
    ),
    { initialValue: [] },
  );
}
