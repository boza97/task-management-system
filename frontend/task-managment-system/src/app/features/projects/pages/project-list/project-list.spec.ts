import { TestBed } from '@angular/core/testing';
import { ProjectList } from './project-list';
import { ProjectService } from '../../data/project.service';
import { of } from 'rxjs';
import { Directive, Input } from '@angular/core';

@Directive({
  // eslint-disable-next-line @angular-eslint/directive-selector
  selector: '[routerLink]',
  standalone: true,
})
class RouterLinkStub {
  @Input() routerLink!: string;
}

describe('ProjectList', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProjectList],
      providers: [
        {
          provide: ProjectService,
          useValue: {
            getProjects: () => of([]),
          },
        },
      ],
    })
      .overrideComponent(ProjectList, {
        set: {
          imports: [RouterLinkStub],
        },
      })
      .compileComponents();
  });

  it('should create', () => {
    const fixture = TestBed.createComponent(ProjectList);
    const component = fixture.componentInstance;
    expect(component).toBeTruthy();
  });
});
