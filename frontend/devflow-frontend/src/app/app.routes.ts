import { Routes } from '@angular/router';
import { Login } from './pages/login/login';
import { MainLayout } from './layout/main-layout/main-layout';
import { Dashboard } from './pages/dashboard/dashboard';
import { Projects } from './pages/projects/projects';
import { authGuard } from './guards/auth-guard';
import { adminGuard } from './guards/admin-guard';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: Login },
  {
    path: '',
    component: MainLayout,
    canActivate: [authGuard],
    children: [
      { path: 'dashboard', component: Dashboard },
      { path: 'projects', component: Projects },
      { path: 'tasks', loadComponent: () => import('./pages/tasks/tasks').then(m => m.Tasks) },
      { path: 'notes', loadComponent: () => import('./pages/notes/notes').then(m => m.Notes) },
      { path: 'docs', loadComponent: () => import('./pages/docs/docs').then(m => m.Docs) },
      { path: 'profile', loadComponent: () => import('./pages/profile/profile').then(m => m.Profile) },
      { path: 'calendar', loadComponent: () => import('./pages/calendar/calendar').then(m => m.Calendar) },
      { path: 'kanban', loadComponent: () => import('./pages/kanban/kanban').then(m => m.Kanban) },
      { path: 'timeline', loadComponent: () => import('./pages/timeline/timeline').then(m => m.Timeline) },
      { path: 'reports', loadComponent: () => import('./pages/reports/reports').then(m => m.Reports) },
      { path: 'admin', loadComponent: () => import('./pages/admin/admin').then(m => m.Admin), canActivate: [adminGuard] },
      { path: 'ai', loadComponent: () => import('./pages/ai/ai').then(m => m.AiAssistant) }
    ]
  },
  { path: '**', redirectTo: 'login' }
];
