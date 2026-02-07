import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { ProjectService } from '../../data/project.service';
import { toSignal } from '@angular/core/rxjs-interop';
import { Project } from '../../data/models/project.model';
import { catchError, of, tap } from 'rxjs';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-project-details',
  imports: [DatePipe, RouterLink],
  templateUrl: './project-details.html',
  styleUrl: './project-details.scss',
})
export class ProjectDetails {
  private readonly route = inject(ActivatedRoute);
  private readonly projectService = inject(ProjectService);

  projectId = this.route.snapshot.paramMap.get('projectId')!;

  loading = signal(true);
  error = signal<string | null>(null);

  project = toSignal<Project | null>(
    this.projectService.getProjectById(this.projectId).pipe(
      tap(() => {
        this.loading.set(false);
        this.error.set(null);
      }),
      catchError(() => {
        this.loading.set(false);
        this.error.set('Failed to load project');
        return of(null);
      }),
    ),
    { initialValue: null },
  );
}
