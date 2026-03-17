import { ApplicationConfig, provideBrowserGlobalErrorListeners } from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { authInterceptor } from './shared/interceptors/auth.interceptor';
import { provideStore } from '@ngrx/store';
import { projectReducer } from './features/projects/data/store/project.reducer';
import { provideEffects } from '@ngrx/effects';
import { ProjectEffects } from './features/projects/data/store/project.effects';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes),
    provideHttpClient(withInterceptors([authInterceptor])),
    provideStore({
      project: projectReducer,
    }),
    provideEffects([ProjectEffects]),
  ],
};
