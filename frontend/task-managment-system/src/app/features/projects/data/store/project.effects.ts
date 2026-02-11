import { inject, Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { catchError, map, of, switchMap, tap } from 'rxjs';
import { ProjectService } from '../project.service';
import {
  loadProject,
  loadProjectFailure,
  loadProjectSuccess,
  updateProject,
  updateProjectFailure,
  updateProjectSuccess,
} from './project.actions';
import { ToastService } from '../../../../shared/services/toast.service';

@Injectable()
export class ProjectEffects {
  private readonly actions$ = inject(Actions);
  private readonly projectService = inject(ProjectService);
  private readonly toastService = inject(ToastService);

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
}
