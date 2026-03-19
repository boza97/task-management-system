import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { ProjectHeader } from '../../components/project-header/project-header';
import { Store } from '@ngrx/store';
import { loadProject } from '../../data/store/project.actions';

@Component({
  selector: 'app-project-shell',
  imports: [RouterOutlet, RouterLink, RouterLinkActive, ProjectHeader],
  templateUrl: './project-shell.html',
  styleUrl: './project-shell.scss',
})
export class ProjectShell implements OnInit {
  private readonly store = inject(Store);
  private readonly route = inject(ActivatedRoute);

  ngOnInit(): void {
    const projectId = this.route.snapshot.paramMap.get('projectId');
    if (projectId) {
      this.store.dispatch(loadProject({ projectId }));
    }
  }
}
