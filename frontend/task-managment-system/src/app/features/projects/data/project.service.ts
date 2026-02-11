import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Project } from './models/project.model';
import { environment } from '../../../../environments/environment';
import { CreateProjectRequest } from './models/project-create-request.model';
import { ProjectUpdateRequest } from './models/project-update-request.model';

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

  createProject(data: CreateProjectRequest) {
    return this.http.post<Project>(`${environment.apiUrl}/projects`, data);
  }

  updateProject(projectId: string, data: ProjectUpdateRequest) {
    return this.http.patch<Project>(`${environment.apiUrl}/projects/${projectId}`, data);
  }

  delete(projectId: string) {
    return this.http.delete(`${environment.apiUrl}/projects/${projectId}`);
  }
}
