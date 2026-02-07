import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Project } from './models/project.model';
import { environment } from '../../../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class ProjectService {
  private readonly http = inject(HttpClient);

  getProjects() {
    return this.http.get<Project[]>(`${environment.apiUrl}/projects/my`);
  }

  getProjectById(id: string) {
    return this.http.get<Project>(`${environment.apiUrl}/projects/${id}`);
  }
}
