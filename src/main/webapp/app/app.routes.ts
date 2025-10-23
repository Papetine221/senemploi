import { Routes } from '@angular/router';
import { errorRoute } from './layouts/error/error.route';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./home/home.component'),
    title: 'home.title',
    data: {
      pageRibbon: false, // Pas de navbar sur la page d'accueil
    },
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
    data: {
      pageRibbon: false, // Pas de navbar sur la page de connexion
    },
  },
  
  {
    path: 'account',
    loadChildren: () => import('./account/account.route'),
    data: {
      pageRibbon: false, // Pas de navbar sur les pages account
    },
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
      pageRibbon: false, // Désactive la navbar JHipster
    },
    title: 'candidatDashboard.title',
  },
  {
    path: '',
    loadChildren: () => import('./entities/entity.routes'),
    data: {
      pageRibbon: false, // Pas de navbar sur les pages entities
    },
  },

  {
    path: 'recruteur-dashboard',
    loadComponent: () =>
      import('./recruteur-dashboard/recruteur-dashboard.component').then(m => m.RecruteurDashboardComponent),
    canActivate: [UserRouteAccessService],
    data: {
      authorities: ['ROLE_RECRUTEUR'],
      pageRibbon: false, // Désactive la navbar JHipster
    },
    title: 'Tableau de bord recruteur',
  },

  ...errorRoute,
];

export default routes;
