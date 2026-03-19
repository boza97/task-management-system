import { createAction, props } from '@ngrx/store';
import { Task } from '../models/task.model';
import { UpdateTaskRequest } from '../models/update-task-request.model';
import { TaskFilters } from '../models/task-filters.model';

export const loadTask = createAction('[Task] Load Task', props<{ taskId: string }>());
export const loadTaskSuccess = createAction('[Task] Load Task Success', props<{ task: Task }>());
export const loadTaskFailure = createAction('[Task] Load Task Failure', props<{ error: string }>());

export const updateTask = createAction(
  '[Task] Update Task',
  props<{ taskId: string; data: UpdateTaskRequest }>(),
);
export const updateTaskSuccess = createAction(
  '[Task] Update Task Success',
  props<{ task: Task }>(),
);
export const updateTaskFailure = createAction(
  '[Task] Update Task Failure',
  props<{ error: string }>(),
);

export const changeTaskStatus = createAction(
  '[Task] Change Status',
  props<{ taskId: string; statusCode: string }>(),
);
export const changeTaskStatusSuccess = createAction(
  '[Task] Change Status Success',
  props<{ task: Task }>(),
);
export const changeTaskStatusFailure = createAction(
  '[Task] Change Status Failure',
  props<{ error: string }>(),
);

export const changeTaskAssignee = createAction(
  '[Task] Change Assignee',
  props<{ taskId: string; assigneeId: string | null }>(),
);
export const changeTaskAssigneeSuccess = createAction(
  '[Task] Change Assignee Success',
  props<{ task: Task }>(),
);
export const changeTaskAssigneeFailure = createAction(
  '[Task] Change Assignee Failure',
  props<{ error: string }>(),
);

export const loadTasks = createAction('[Task List] Load Tasks', props<{ projectId: string }>());
export const loadTasksSuccess = createAction(
  '[Task List] Load Tasks Success',
  props<{ tasks: Task[] }>(),
);
export const loadTasksFailure = createAction(
  '[Task List] Load Tasks Failure',
  props<{ error: string }>(),
);

export const setTaskFilters = createAction(
  '[Task List] Set Filters',
  props<{ filters: Partial<TaskFilters> }>(),
);
export const resetTaskFilters = createAction('[Task List] Reset Filters');

export const deleteTask = createAction('[Task] Delete Task', props<{ taskId: string }>());
export const deleteTaskSuccess = createAction(
  '[Task] Delete Task Success',
  props<{ taskId: string }>(),
);
export const deleteTaskFailure = createAction(
  '[Task] Delete Task Failure',
  props<{ error: string }>(),
);
