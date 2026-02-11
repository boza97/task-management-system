import { createReducer, on } from '@ngrx/store';
import * as ProjectActions from './project.actions';
import { Project } from '../models/project.model';
import { updateProjectSuccess } from './project.actions';

export interface ProjectState {
  project: Project | null;
  loading: boolean;
  error: string | null;
}

export const initialState: ProjectState = {
  project: null,
  loading: false,
  error: null,
};

export const projectReducer = createReducer(
  initialState,
  on(ProjectActions.loadProject, (state) => ({
    ...state,
    loading: true,
    error: null,
  })),

  on(ProjectActions.loadProjectSuccess, (state, { project }) => ({
    ...state,
    project,
    loading: false,
  })),

  on(ProjectActions.loadProjectFailure, (state, { error }) => ({
    ...state,
    loading: false,
    error,
  })),
  on(updateProjectSuccess, (state, { project }) => ({
    ...state,
    project,
  })),
);
