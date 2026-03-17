import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProjectMembers } from './project-members';

describe('ProjectMembers', () => {
  let component: ProjectMembers;
  let fixture: ComponentFixture<ProjectMembers>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProjectMembers],
    }).compileComponents();

    fixture = TestBed.createComponent(ProjectMembers);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
