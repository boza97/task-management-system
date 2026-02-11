import { Component, effect, inject, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { DatePipe } from '@angular/common';
import { Store } from '@ngrx/store';
import {
  selectProject,
  selectProjectError,
  selectProjectLoading,
} from '../../data/store/project.selectors';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { deleteProject, updateProject } from '../../data/store/project.actions';

@Component({
  selector: 'app-project-details',
  imports: [DatePipe, ReactiveFormsModule],
  templateUrl: './project-details.html',
  styleUrl: './project-details.scss',
})
export class ProjectDetails {
  private readonly store = inject(Store);
  private readonly fb = inject(FormBuilder);

  loading = toSignal<boolean>(this.store.select(selectProjectLoading));
  error = toSignal(this.store.select(selectProjectError));
  project = toSignal(this.store.select(selectProject));

  editMode = signal(false);
  form = this.fb.nonNullable.group({
    name: ['' as string, [Validators.required, Validators.maxLength(100)]],
    description: [null as string | null, [Validators.maxLength(1000)]],
  });

  constructor() {
    effect(() => {
      const project = this.project();
      if (project) {
        this.form.patchValue(
          {
            name: project.name,
            description: project.description,
          },
          { emitEvent: false },
        );
      }
    });
  }

  save() {
    const project = this.project();
    if (!project || this.form.invalid) {
      return;
    }

    this.store.dispatch(
      updateProject({
        projectId: project.id,
        data: { name: this.form.value.name!, description: this.form.value.description },
      }),
    );

    this.editMode.set(false);
  }

  cancelEdit() {
    const project = this.project();
    if (!project) {
      return;
    }

    this.form.reset({
      name: project.name,
      description: project.description,
    });

    this.editMode.set(false);
  }

  deleteProject() {
    const project = this.project();
    if (!project) {
      return;
    }

    if (!confirm('Are you sure you want to delete this project?')) {
      return;
    }

    this.store.dispatch(deleteProject({ projectId: project.id }));
  }
}
