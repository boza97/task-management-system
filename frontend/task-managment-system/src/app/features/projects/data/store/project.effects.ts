import { inject, Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import * as ProjectActions from './project.actions';
import { catchError, map, of, switchMap } from 'rxjs';
import { ProjectService } from '../project.service';

@Injectable()
export class ProjectEffects {
  private readonly actions$ = inject(Actions);
  private readonly projectService = inject(ProjectService);

  loadProject$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ProjectActions.loadProject),
      switchMap(({ projectId }) =>
        this.projectService.getProjectById(projectId).pipe(
          map((project) => ProjectActions.loadProjectSuccess({ project })),
          catchError(() =>
            of(
              ProjectActions.loadProjectFailure({
                error: 'Failed to load project',
              }),
            ),
          ),
        ),
      ),
    ),
  );
}
