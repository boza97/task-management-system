import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { ProjectMember, ProjectRole } from './models/project-member.model';
import { environment } from '../../../../environments/environment';
import { AddProjectMemberRequest } from './models/add-project-member-request.model';

@Injectable({ providedIn: 'root' })
export class ProjectMembersService {
  private readonly http = inject(HttpClient);

  listMembers(projectId: string) {
    return this.http.get<ProjectMember[]>(`${environment.apiUrl}/projects/${projectId}/members`);
  }

  addMember(projectId: string, request: AddProjectMemberRequest) {
    return this.http.post<ProjectMember>(
      `${environment.apiUrl}/projects/${projectId}/members`,
      request,
    );
  }

  changeRole(projectId: string, userId: string, role: ProjectRole) {
    return this.http.patch<ProjectMember>(
      `${environment.apiUrl}/projects/${projectId}/members/${userId}/role`,
      { role },
    );
  }

  removeMember(projectId: string, userId: string) {
    return this.http.delete<void>(`${environment.apiUrl}/projects/${projectId}/members/${userId}`);
  }
}
