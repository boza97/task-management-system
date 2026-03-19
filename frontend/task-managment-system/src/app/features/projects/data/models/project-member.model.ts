export enum ProjectRole {
  DEVELOPER = 'DEVELOPER',
  QA = 'QA',
  PROJECT_MANAGER = 'PROJECT_MANAGER',
}

export interface ProjectMember {
  userId: string;
  email: string;
  firstName: string;
  lastName: string;
  role: ProjectRole;
  joinedAt: string;
}
