import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { toSignal } from '@angular/core/rxjs-interop';
import { DatePipe } from '@angular/common';
import { Store } from '@ngrx/store';
import {
  selectProject,
  selectProjectError,
  selectProjectLoading,
} from '../../data/store/project.selectors';

@Component({
  selector: 'app-project-header',
  imports: [DatePipe],
  templateUrl: './project-header.html',
  styleUrl: './project-header.scss',
})
export class ProjectHeader {
  private readonly store = inject(Store);
  private readonly router = inject(Router);

  loading = toSignal<boolean>(this.store.select(selectProjectLoading));
  error = toSignal(this.store.select(selectProjectError));
  project = toSignal(this.store.select(selectProject));

  goBack() {
    this.router.navigate(['/projects']);
  }
}
