import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css',
})
export class Navbar implements OnInit {
  fullName = signal('');
  email = signal('');
  role = signal('');
  isProfileOpen = signal(false);

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.authService.getMe().subscribe({
      next: (user) => {
        this.fullName.set(user.fullName);
        this.email.set(user.email);
        this.role.set(user.role);
      },
      error: (err) => {
        console.error('Failed to retrieve user profile', err);
        this.logout();
      }
    });
  }

  toggleProfileDropdown(): void {
    this.isProfileOpen.update(v => !v);
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
