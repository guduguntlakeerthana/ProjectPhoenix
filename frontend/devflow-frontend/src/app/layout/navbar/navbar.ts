import { Component, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth';
import { NotificationService, NotificationResponse } from '../../services/notification';

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

  // Notifications signals
  isNotificationsOpen = signal(false);
  notifications = signal<NotificationResponse[]>([]);
  unreadCount = computed(() => this.notifications().filter(n => !n.isRead).length);

  constructor(
    private authService: AuthService,
    private notificationService: NotificationService,
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

    this.loadNotifications();
  }

  loadNotifications(): void {
    this.notificationService.getNotifications().subscribe({
      next: (data) => {
        this.notifications.set(data);
      },
      error: (err) => {
        console.error('Failed to load notifications', err);
      }
    });
  }

  toggleNotifications(): void {
    this.isNotificationsOpen.update(v => !v);
    if (this.isNotificationsOpen()) {
      this.isProfileOpen.set(false); // close profile dropdown
      this.loadNotifications();
    }
  }

  toggleProfileDropdown(): void {
    this.isProfileOpen.update(v => !v);
    if (this.isProfileOpen()) {
      this.isNotificationsOpen.set(false); // close notifications dropdown
    }
  }

  markAsRead(id: number, event: Event): void {
    event.stopPropagation();
    this.notificationService.markAsRead(id).subscribe({
      next: () => {
        this.loadNotifications();
      },
      error: (err) => {
        console.error('Failed to mark notification as read', err);
      }
    });
  }

  markAllAsRead(): void {
    this.notificationService.markAllAsRead().subscribe({
      next: () => {
        this.loadNotifications();
      },
      error: (err) => {
        console.error('Failed to mark all read', err);
      }
    });
  }

  deleteNotification(id: number, event: Event): void {
    event.stopPropagation();
    this.notificationService.deleteNotification(id).subscribe({
      next: () => {
        this.loadNotifications();
      },
      error: (err) => {
        console.error('Failed to delete notification', err);
      }
    });
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
