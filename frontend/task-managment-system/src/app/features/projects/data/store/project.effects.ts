import { inject, Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { catchError, map, of, switchMap } from 'rxjs';
import { ProjectService } from '../project.service';
import {
  loadProject,
  loadProjectFailure,
  loadProjectSuccess,
  updateProject,
  updateProjectFailure,
  updateProjectSuccess,
} from './project.actions';

@Injectable()
export class ProjectEffects {
  private readonly actions$ = inject(Actions);
  private readonly projectService = inject(ProjectService);

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
}
