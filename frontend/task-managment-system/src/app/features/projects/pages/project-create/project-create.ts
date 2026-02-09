import { Component, DestroyRef, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { ProjectService } from '../../data/project.service';
import { CreateProjectRequest } from '../../data/models/project-create-request.model';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

@Component({
  selector: 'app-project-create',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './project-create.html',
  styleUrl: './project-create.scss',
})
export class ProjectCreate {
  private readonly destroyRef = inject(DestroyRef);
  private readonly fb = inject(FormBuilder);
  private readonly projectService = inject(ProjectService);
  private readonly router = inject(Router);

  submitting = signal(false);
  error = signal<string | null>(null);

  form = this.fb.nonNullable.group({
    key: ['', [Validators.required, Validators.maxLength(20)]],
    name: ['', [Validators.required, Validators.maxLength(100)]],
    description: ['', [Validators.maxLength(1000)]],
  });

  constructor() {
    this.form.valueChanges
      .pipe(takeUntilDestroyed())
      .subscribe(() => (this.error() ? this.error.set(null) : null));
  }

  submit() {
    if (this.form.invalid) {
      return;
    }

    this.submitting.set(true);
    this.error.set(null);

    const payload: CreateProjectRequest = this.form.getRawValue();

    this.projectService
      .createProject(payload)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (project) => {
          this.router.navigate(['/projects', project.id]);
        },
        error: () => {
          this.error.set('Failed to create project');
          this.submitting.set(false);
        },
      });
  }
}
