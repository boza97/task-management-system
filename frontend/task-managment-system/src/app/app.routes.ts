import { Routes } from '@angular/router';

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
            (m) => m.ProjectCreate
          ),
      },
      {
        path: 'projects/:projectId',
        loadComponent: () =>
          import('./features/projects/pages/project-details/project-details').then(
            (m) => m.ProjectDetails
          ),
      },
    ],
  },

  { path: '**', redirectTo: '' },
];
