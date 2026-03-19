import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideMockStore } from '@ngrx/store/testing';
import { TaskComments } from './task-comments';
import { selectComments, selectCommentsLoading } from '../../data/store/comments.selectors';
import { selectTask } from '../../../data/store/task.selectors';
import { TokenStorageService } from '../../../../../shared/services/token-storage.service';

describe('TaskComments', () => {
  let component: TaskComments;
  let fixture: ComponentFixture<TaskComments>;

  const mockTokenStorage = {
    getUser: () => ({
      sub: 'user1',
      roles: ['USER'],
    }),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TaskComments],
      providers: [
        provideMockStore({
          selectors: [
            { selector: selectComments, value: [] },
            { selector: selectCommentsLoading, value: false },
            { selector: selectTask, value: { id: 'task1' } },
          ],
        }),
        {
          provide: TokenStorageService,
          useValue: mockTokenStorage,
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(TaskComments);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
