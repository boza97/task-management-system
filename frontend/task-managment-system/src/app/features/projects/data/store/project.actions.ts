import { createAction, props } from '@ngrx/store';
import { Project } from '../models/project.model';

export const loadProject = createAction('[Project] Load Project', props<{ projectId: string }>());

export const loadProjectSuccess = createAction(
  '[Project] Load Project Success',
  props<{ project: Project }>(),
);

export const loadProjectFailure = createAction(
  '[Project] Load Project Failure',
  props<{ error: string }>(),
);
