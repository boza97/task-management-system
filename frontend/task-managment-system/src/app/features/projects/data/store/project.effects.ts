import { inject, Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { catchError, map, of, switchMap, tap } from 'rxjs';
import { ProjectService } from '../project.service';
import {
  deleteProject,
  deleteProjectFailure,
  deleteProjectSuccess,
  loadProject,
  loadProjectFailure,
  loadProjectSuccess,
  updateProject,
  updateProjectFailure,
  updateProjectSuccess,
} from './project.actions';
import { ToastService } from '../../../../shared/services/toast.service';
import { Router } from '@angular/router';

@Injectable()
export class ProjectEffects {
  private readonly actions$ = inject(Actions);
  private readonly projectService = inject(ProjectService);
  private readonly toastService = inject(ToastService);
  private readonly router = inject(Router);

  loadProject$ = createEffect(() =>
    this.actions$.pipe(
      ofType(loadProject),
      switchMap(({ projectId }) =>
        this.projectService.getProjectById(projectId).pipe(
          map((project) => loadProjectSuccess({ project })),
          catchError(() =>
            of(
              loadProjectFailure({
                error: 'Failed to load project',
              }),
            ),
          ),
        ),
      ),
    ),
  );

  updateProject$ = createEffect(() =>
    this.actions$.pipe(
      ofType(updateProject),
      switchMap(({ projectId, data }) =>
        this.projectService.updateProject(projectId, data).pipe(
          map((project) => updateProjectSuccess({ project })),
          catchError((error) => of(updateProjectFailure({ error }))),
        ),
      ),
    ),
  );

  updateProjectSuccessToast$ = createEffect(
    () =>
      this.actions$.pipe(
        ofType(updateProjectSuccess),
        tap(() => {
          this.toastService.show('Project updated successfully', 'success');
        }),
      ),
    { dispatch: false },
  );

  deleteProject$ = createEffect(() =>
    this.actions$.pipe(
      ofType(deleteProject),
      switchMap(({ projectId }) =>
        this.projectService.delete(projectId).pipe(
          map(() => deleteProjectSuccess()),
          catchError((error) => of(deleteProjectFailure({ error }))),
        ),
      ),
    ),
  );

  deleteProjectSuccessToast$ = createEffect(
    () =>
      this.actions$.pipe(
        ofType(deleteProjectSuccess),
        tap(() => {
          this.toastService.show('Project deleted successfully', 'success');
          this.router.navigate(['/projects']);
        }),
      ),
    { dispatch: false },
  );
}
