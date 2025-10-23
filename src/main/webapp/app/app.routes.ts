import { Routes } from '@angular/router';
import { errorRoute } from './layouts/error/error.route';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

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
    loadComponent: () =>
      import('./candidat-dashboard/candidat-dashboard.component').then(m => m.CandidatDashboardComponent),
    canActivate: [UserRouteAccessService],
    data: {
      authorities: ['ROLE_CANDIDAT'],
    },
    title: 'candidatDashboard.title',
  },
  {
    path: '',
    loadChildren: () => import('./entities/entity.routes'),
  },

  {
    path: 'recruteur-dashboard',
    loadComponent: () =>
      import('./recruteur-dashboard/recruteur-dashboard.component').then(m => m.RecruteurDashboardComponent),
    canActivate: [UserRouteAccessService],
    data: {
      authorities: ['ROLE_RECRUTEUR'],
    },
    title: 'Tableau de bord recruteur',
  },

  ...errorRoute,
];

export default routes;
