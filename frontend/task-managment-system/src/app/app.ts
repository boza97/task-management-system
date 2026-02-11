import { Component, inject, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { SessionService } from './shared/services/session.service';
import { TokenStorageService } from './shared/services/token-storage.service';
import { ToastContainer } from './shared/components/toast-container/toast-container';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, ToastContainer],
  templateUrl: './app.html',
  styleUrl: './app.scss',
})
export class App implements OnInit {
  private readonly sessionService = inject(SessionService);
  private readonly tokenStorage = inject(TokenStorageService);

  ngOnInit(): void {
    const token = this.tokenStorage.get();
    if (token) {
      this.sessionService.startSessionTimer(token);
    }
  }
}
