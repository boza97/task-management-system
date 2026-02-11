import { createAction, props } from '@ngrx/store';
import { Project } from '../models/project.model';
import { ProjectUpdateRequest } from '../models/project-update-request.model';

export const loadProject = createAction('[Project] Load Project', props<{ projectId: string }>());
export const loadProjectSuccess = createAction(
  '[Project] Load Project Success',
  props<{ project: Project }>(),
);
export const loadProjectFailure = createAction(
  '[Project] Load Project Failure',
  props<{ error: string }>(),
);

export const updateProject = createAction(
  '[Project] Update',
  props<{ projectId: string; data: ProjectUpdateRequest }>(),
);
export const updateProjectSuccess = createAction(
  '[Project] Update Success',
  props<{ project: Project }>(),
);
export const updateProjectFailure = createAction(
  '[Project] Update Failure',
  props<{ error: any }>(),
);

export const deleteProject = createAction('[Project] Delete', props<{ projectId: string }>());
export const deleteProjectSuccess = createAction('[Project] Delete Success');
export const deleteProjectFailure = createAction(
  '[Project] Delete Failure',
  props<{ error: any }>(),
);
