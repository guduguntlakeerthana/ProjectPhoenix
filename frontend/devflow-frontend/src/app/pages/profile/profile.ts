import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './profile.html',
  styleUrl: './profile.css'
})
export class Profile implements OnInit {

  // Current profile details
  fullName = signal('');
  email = signal('');
  role = signal('');

  // Form signals
  editFullName = signal('');
  oldPassword = signal('');
  newPassword = signal('');
  confirmPassword = signal('');

  // Info alert signals
  profileSuccess = signal('');
  profileError = signal('');
  passwordSuccess = signal('');
  passwordError = signal('');

  // Theme Customization signals
  currentTheme = signal('dark');

  constructor(private authService: AuthService) {}

  ngOnInit(): void {
    this.loadProfile();
    this.detectTheme();
  }

  loadProfile(): void {
    this.authService.getMe().subscribe({
      next: (user) => {
        this.fullName.set(user.fullName);
        this.email.set(user.email);
        this.role.set(user.role);
        
        // Populate edit input
        this.editFullName.set(user.fullName);
      },
      error: (err) => {
        console.error('Failed to load user profile', err);
      }
    });
  }

  updateProfile(): void {
    const name = this.editFullName().trim();
    if (!name) {
      this.profileError.set('Name cannot be blank');
      return;
    }

    this.authService.updateProfile(name).subscribe({
      next: (res) => {
        this.profileSuccess.set('Profile details updated successfully');
        this.profileError.set('');
        this.loadProfile();
      },
      error: (err) => {
        console.error('Failed to update profile', err);
        this.profileError.set(err.error?.message || 'Error occurred updating profile');
        this.profileSuccess.set('');
      }
    });
  }

  changePassword(): void {
    const oldPass = this.oldPassword().trim();
    const newPass = this.newPassword().trim();
    const confirmPass = this.confirmPassword().trim();

    if (!oldPass || !newPass || !confirmPass) {
      this.passwordError.set('All fields are required');
      return;
    }

    if (newPass !== confirmPass) {
      this.passwordError.set('New passwords do not match');
      return;
    }

    this.authService.changePassword({
      oldPassword: oldPass,
      newPassword: newPass
    }).subscribe({
      next: () => {
        this.passwordSuccess.set('Password updated successfully');
        this.passwordError.set('');
        
        // Reset form fields
        this.oldPassword.set('');
        this.newPassword.set('');
        this.confirmPassword.set('');
      },
      error: (err) => {
        console.error('Failed to update password', err);
        this.passwordError.set(err.error?.message || 'Current password verification failed');
        this.passwordSuccess.set('');
      }
    });
  }

  // Theme Customization settings
  detectTheme(): void {
    const savedTheme = localStorage.getItem('df_theme') || 'dark';
    this.currentTheme.set(savedTheme);
    this.applyTheme(savedTheme);
  }

  toggleTheme(theme: 'light' | 'dark'): void {
    this.currentTheme.set(theme);
    localStorage.setItem('df_theme', theme);
    this.applyTheme(theme);
  }

  applyTheme(theme: string): void {
    if (theme === 'light') {
      document.documentElement.classList.add('light-mode');
    } else {
      document.documentElement.classList.remove('light-mode');
    }
  }
}
