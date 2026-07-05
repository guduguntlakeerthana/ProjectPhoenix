import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminService, AdminUserResponse, AdminStatsResponse } from '../../services/admin';
import { AuditLogResponse } from '../../services/timeline';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin.html',
  styleUrl: './admin.css'
})
export class Admin implements OnInit {

  users = signal<AdminUserResponse[]>([]);
  stats = signal<AdminStatsResponse | null>(null);
  logs = signal<AuditLogResponse[]>([]);
  
  isLoading = signal(true);
  isSubmitting = signal(false);

  constructor(private adminService: AdminService) {}

  ngOnInit(): void {
    this.loadAdminData();
  }

  loadAdminData(): void {
    // Parallel loading
    this.adminService.getGlobalStats().subscribe({
      next: (s) => this.stats.set(s),
      error: (e) => console.error('Failed to load global admin stats', e)
    });

    this.adminService.getUsers().subscribe({
      next: (u) => this.users.set(u),
      error: (e) => console.error('Failed to load admin users', e)
    });

    this.adminService.getGlobalAuditLogs().subscribe({
      next: (l) => {
        this.logs.set(l);
        this.isLoading.set(false);
      },
      error: (e) => {
        console.error('Failed to load global audit logs', e);
        this.isLoading.set(false);
      }
    });
  }

  changeRole(user: AdminUserResponse, newRole: string): void {
    this.isSubmitting.set(true);
    this.adminService.updateUserRole(user.id, newRole).subscribe({
      next: (updatedUser) => {
        // Update user state locally
        this.users.update(list => list.map(u => u.id === updatedUser.id ? updatedUser : u));
        this.isSubmitting.set(false);
        
        // Reload global logs to show new entry
        this.adminService.getGlobalAuditLogs().subscribe(l => this.logs.set(l));
      },
      error: (err) => {
        console.error('Failed to update user role', err);
        alert(err.error?.message || 'Error updating user role');
        this.isSubmitting.set(false);
      }
    });
  }

  getLogActionClass(action: string): string {
    if (action.includes('CREATED')) return 'lbl-create';
    if (action.includes('UPDATED')) return 'lbl-update';
    if (action.includes('DELETED')) return 'lbl-delete';
    return 'lbl-default';
  }
}
