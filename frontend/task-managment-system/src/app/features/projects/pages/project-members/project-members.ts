import { Component, computed, DestroyRef, effect, inject, OnInit, signal } from '@angular/core';
import { ProjectMembersService } from '../../data/project-members.service';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ProjectMember, ProjectRole } from '../../data/models/project-member.model';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { UserService } from '../../../../shared/services/user.service';
import { User } from '../../../../shared/models/user.model';
import { Store } from '@ngrx/store';
import { selectProject } from '../../data/store/project.selectors';
import { distinctUntilChanged } from 'rxjs';
import { ToastService } from '../../../../shared/services/toast.service';

@Component({
  selector: 'app-project-members',
  imports: [ReactiveFormsModule],
  templateUrl: './project-members.html',
  styleUrl: './project-members.scss',
})
export class ProjectMembers implements OnInit {
  private readonly store = inject(Store);
  private readonly membersService = inject(ProjectMembersService);
  private readonly userService = inject(UserService);
  private readonly fb = inject(FormBuilder);
  private readonly toastService = inject(ToastService);
  private readonly destroyRef = inject(DestroyRef);

  projectRoleEnum = ProjectRole;
  addForm = this.fb.nonNullable.group({
    userId: ['', Validators.required],
    role: [ProjectRole.DEVELOPER, Validators.required],
  });
  project = toSignal(
    this.store.select(selectProject).pipe(distinctUntilChanged((a, b) => a?.id === b?.id)),
  );
  members = signal<ProjectMember[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);
  users = signal<User[]>([]);
  usersLoading = signal(false);
  availableUsers = computed(() => {
    const membersIds = new Set(this.members().map((member) => member.userId));
    return this.users().filter((user) => !membersIds.has(user.id));
  });

  constructor() {
    effect(() => {
      const project = this.project();
      if (project) {
        this.loadMembers(project.id);
      }
    });
  }

  ngOnInit() {
    this.loadUsers();
  }

  addMember() {
    if (this.addForm.invalid) {
      return;
    }

    this.membersService
      .addMember(this.project()!.id, this.addForm.getRawValue())
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((member) => {
        this.members.update((prev) => [...prev, member]);

        this.addForm.reset({
          userId: '',
          role: ProjectRole.DEVELOPER,
        });
        this.addForm.markAsPristine();
        this.addForm.markAsUntouched();
      });
  }

  removeMember(userId: string) {
    if (!confirm('Are you sure that you want to remove this member from the project?')) {
      return;
    }

    this.membersService
      .removeMember(this.project()!.id, userId)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          this.members.update((prev) => prev.filter((m) => m.userId !== userId));
        },
      });
  }

  changeRole(userId: string, role: ProjectRole) {
    const currentRole = this.members().find((m) => m.userId === userId)?.role;

    this.members.update((list) => list.map((m) => (m.userId === userId ? { ...m, role } : m)));

    this.membersService
      .changeRole(this.project()!.id, userId, role)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (updated) => {
          this.members.update((list) => list.map((m) => (m.userId === userId ? updated : m)));
          this.toastService.show('Role updated successfully', 'success');
        },
        error: () => {
          this.members.update((list) =>
            list.map((m) => (m.userId === userId ? { ...m, role: currentRole! } : m)),
          );
        },
      });
  }

  private loadMembers(projectId: string) {
    this.loading.set(true);

    this.membersService
      .listMembers(projectId)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (list) => {
          this.members.set(list);
          this.loading.set(false);
        },
        error: () => {
          this.error.set('Failed to load members');
          this.loading.set(false);
        },
      });
  }

  private loadUsers() {
    this.usersLoading.set(true);

    this.userService.getAllUsers().subscribe({
      next: (list) => {
        this.users.set(list);
        this.usersLoading.set(false);
      },
      error: () => this.usersLoading.set(false),
    });
  }
}
