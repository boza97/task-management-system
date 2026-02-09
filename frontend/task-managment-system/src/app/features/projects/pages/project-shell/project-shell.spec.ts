import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProjectShell } from './project-shell';

describe('ProjectShell', () => {
  let component: ProjectShell;
  let fixture: ComponentFixture<ProjectShell>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProjectShell]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ProjectShell);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
