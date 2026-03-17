export interface Project {
  id: string;
  key: string;
  name: string;
  description: string | null;
  ownerId: string;
  ownerFullName: string;
  createdAt: string;
  updatedAt: string;
}
