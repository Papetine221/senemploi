import { Routes } from '@angular/router';
import { errorRoute } from './layouts/error/error.route';

const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./home/home.component'),
    title: 'home.title',
  },
  {
    path: '',
    loadComponent: () => import('./layouts/navbar/navbar.component'),
    outlet: 'navbar',
  },
  {
    path: 'login',
    loadComponent: () => import('./login/login.component'),
    title: 'login.title',
  },
  
  {
    path: 'account',
    loadChildren: () => import('./account/account.route'),
  },
  {
    path: 'admin',
    loadChildren: () => import('./admin/admin.routes'),
    data: {
      authorities: ['ROLE_ADMIN'],
    },
  },
  {
    path: 'candidat-dashboard',
    data: {
      authorities: ['ROLE_CANDIDAT'],
    },
    canActivate: [UserRouteAccessService],
    loadChildren: () => import('./candidat-dashboard/candidat-dashboard.routes'),
    title: 'candidatDashboard.title',
  },
  {
    path: '',
    loadChildren: () => import('./entities/entity.routes'),
  },

  // ✅ Route libre d'accès (sans authentification)
  {
    path: 'recruteur-dashboard',
    loadComponent: () =>
      import('./recruteur-dashboard/recruteur-dashboard.component').then(m => m.RecruteurDashboardComponent),
    title: 'Tableau de bord recruteur',
  },

  ...errorRoute,
];

export default routes;
