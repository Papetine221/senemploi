import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import CandidatureResolve from './route/candidature-routing-resolve.service';

const candidatureRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/candidature.component').then(m => m.CandidatureComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/candidature-detail.component').then(m => m.CandidatureDetailComponent),
    resolve: {
      candidature: CandidatureResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/candidature-update.component').then(m => m.CandidatureUpdateComponent),
    resolve: {
      candidature: CandidatureResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'postuler',
    loadComponent: () => import('./postuler/candidature-postuler.component').then(m => m.CandidaturePostulerComponent),
    canActivate: [UserRouteAccessService],
    data: {
      authorities: ['ROLE_CANDIDAT'],
    },
  },
  {
    path: 'mes-candidatures',
    loadComponent: () => import('./mes-candidatures/mes-candidatures.component').then(m => m.MesCandidaturesComponent),
    canActivate: [UserRouteAccessService],
    data: {
      authorities: ['ROLE_CANDIDAT'],
    },
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/candidature-update.component').then(m => m.CandidatureUpdateComponent),
    resolve: {
      candidature: CandidatureResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default candidatureRoute;
