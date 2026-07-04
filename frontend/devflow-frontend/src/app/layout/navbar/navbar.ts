import { Component, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth';
import { NotificationService, NotificationResponse } from '../../services/notification';
import { GlobalSearchService, GlobalSearchResponse } from '../../services/search';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
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

  // Global Search signals
  searchQuery = signal('');
  isSearchOpen = signal(false);
  searchResults = signal<GlobalSearchResponse>({ projects: [], tasks: [], notes: [], docs: [] });

  constructor(
    private authService: AuthService,
    private notificationService: NotificationService,
    private searchService: GlobalSearchService,
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

  // Global Search logic
  onSearch(query: string): void {
    this.searchQuery.set(query);
    if (!query.trim()) {
      this.isSearchOpen.set(false);
      return;
    }

    this.searchService.search(query).subscribe({
      next: (res) => {
        this.searchResults.set(res);
        this.isSearchOpen.set(true);
      },
      error: (err) => {
        console.error('Search query failed', err);
      }
    });
  }

  closeSearch(): void {
    this.searchQuery.set('');
    this.isSearchOpen.set(false);
  }

  // Notifications logic
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
      this.isProfileOpen.set(false);
      this.isSearchOpen.set(false);
      this.loadNotifications();
    }
  }

  toggleProfileDropdown(): void {
    this.isProfileOpen.update(v => !v);
    if (this.isProfileOpen()) {
      this.isNotificationsOpen.set(false);
      this.isSearchOpen.set(false);
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
