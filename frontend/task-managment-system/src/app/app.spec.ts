import { TestBed } from '@angular/core/testing';
import { App } from './app';
import { TokenStorageService } from './shared/services/token-storage.service';
import { SessionService } from './shared/services/session.service';

describe('App', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [App],
      providers: [
        {
          provide: TokenStorageService,
          useValue: {
            get: () => null,
          },
        },
        {
          provide: SessionService,
          useValue: {
            startSessionTimer: vi.fn(),
          },
        },
      ],
    }).compileComponents();
  });

  it('should create the app', () => {
    const fixture = TestBed.createComponent(App);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });
});
