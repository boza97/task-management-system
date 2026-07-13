import { Component, inject } from '@angular/core';
import { TokenStorageService } from '../../shared/services/token-storage.service';
import { Router } from '@angular/router';
import { NotificationBell } from '../../features/notifications/components/notification-bell/notification-bell';

@Component({
  selector: 'app-navbar',
  imports: [NotificationBell],
  templateUrl: './navbar.html',
  styleUrl: './navbar.scss',
})
export class Navbar {
  private readonly tokenStorage = inject(TokenStorageService);
  private readonly router = inject(Router);

  user = this.tokenStorage.getUser();

  get initials(): string {
    if (!this.user) {
      return '';
    }
    return this.user.firstName.charAt(0).toUpperCase() + this.user.lastName.charAt(0).toUpperCase();
  }

  logout() {
    this.tokenStorage.clear();
    this.router.navigate(['/login']);
  }
}
