import { ProjectRole } from './project-member.model';

export interface AddProjectMemberRequest {
  userId: string;
  role: ProjectRole;
}
