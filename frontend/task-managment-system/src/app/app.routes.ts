import { Routes } from '@angular/router';
import { provideState } from '@ngrx/store';
import { TASK_FEATURE_KEY, taskReducer } from './features/tasks/data/store/task.reducer';
import { provideEffects } from '@ngrx/effects';
import { TaskEffects } from './features/tasks/data/store/task.effects';
import {
  COMMENTS_FEATURE_KEY,
  commentsReducer,
} from './features/tasks/comments/data/store/comments.reducer';
import { CommentsEffects } from './features/tasks/comments/data/store/comments.effects';

export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () => import('./features/auth/pages/login/login').then((m) => m.Login),
  },
  {
    path: 'register',
    loadComponent: () => import('./features/auth/pages/register/register').then((m) => m.Register),
  },

  {
    path: '',
    loadComponent: () => import('./layout/main-layout/main-layout').then((m) => m.MainLayout),
    children: [
      {
        path: '',
        redirectTo: 'projects',
        pathMatch: 'full',
      },
      {
        path: 'projects',
        loadComponent: () =>
          import('./features/projects/pages/project-list/project-list').then((m) => m.ProjectList),
      },
      {
        path: 'projects/new',
        loadComponent: () =>
          import('./features/projects/pages/project-create/project-create').then(
            (m) => m.ProjectCreate,
          ),
      },
      {
        path: 'projects/:projectId',
        loadComponent: () =>
          import('./features/projects/pages/project-shell/project-shell').then(
            (m) => m.ProjectShell,
          ),
        children: [
          {
            path: '',
            redirectTo: 'overview',
            pathMatch: 'full',
          },
          {
            path: 'overview',
            loadComponent: () =>
              import('./features/projects/pages/project-details/project-details').then(
                (m) => m.ProjectDetails,
              ),
          },
          {
            path: 'tasks',
            providers: [provideState(TASK_FEATURE_KEY, taskReducer), provideEffects(TaskEffects)],
            children: [
              {
                path: '',
                loadComponent: () =>
                  import('./features/tasks/pages/task-list/task-list').then((m) => m.TaskList),
              },
              {
                path: 'new',
                loadComponent: () =>
                  import('./features/tasks/pages/task-create/task-create').then(
                    (m) => m.TaskCreate,
                  ),
              },
              {
                path: ':taskId',
                providers: [
                  provideState(COMMENTS_FEATURE_KEY, commentsReducer),
                  provideEffects(CommentsEffects),
                ],
                loadComponent: () =>
                  import('./features/tasks/pages/task-details/task-details').then(
                    (m) => m.TaskDetails,
                  ),
              },
            ],
          },
          {
            path: 'members',
            loadComponent: () =>
              import('./features/projects/pages/project-members/project-members').then(
                (m) => m.ProjectMembers,
              ),
          },
        ],
      },
    ],
  },

  { path: '**', redirectTo: '' },
];
