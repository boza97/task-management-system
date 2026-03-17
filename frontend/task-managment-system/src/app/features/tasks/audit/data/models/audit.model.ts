export enum ActionType {
  TASK_CREATED = 'TASK_CREATED',
  STATUS_CHANGED = 'STATUS_CHANGED',
  ASSIGNEE_CHANGED = 'ASSIGNEE_CHANGED',
  TITLE_CHANGED = 'TITLE_CHANGED',
  DESCRIPTION_CHANGED = 'DESCRIPTION_CHANGED',
  DUE_DATE_CHANGED = 'DUE_DATE_CHANGED',
  PRIORITY_CHANGED = 'PRIORITY_CHANGED',
  COMMENT_ADDED = 'COMMENT_ADDED',
  COMMENT_DELETED = 'COMMENT_DELETED',
}

export interface TaskAuditLog {
  id: string;
  actionType: ActionType;
  timestamp: string;
  oldValue: string | null;
  newValue: string | null;
  performedById: string;
  performedByName: string;
}
