import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import OffreEmploiResolve from './route/offre-emploi-routing-resolve.service';

const offreEmploiRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/offre-emploi.component').then(m => m.OffreEmploiComponent),
    data: {
      defaultSort: `id,${ASC}`,
      authorities: ['ROLE_CANDIDAT', 'ROLE_RECRUTEUR', 'ROLE_ADMIN'],
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/offre-emploi-detail.component').then(m => m.OffreEmploiDetailComponent),
    resolve: {
      offreEmploi: OffreEmploiResolve,
    },
    canActivate: [UserRouteAccessService],
    data: {
      authorities: ['ROLE_CANDIDAT', 'ROLE_RECRUTEUR', 'ROLE_ADMIN'],
    },
  },
  {
    path: 'new',
    loadComponent: () => import('./update/offre-emploi-update.component').then(m => m.OffreEmploiUpdateComponent),
    resolve: {
      offreEmploi: OffreEmploiResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/offre-emploi-update.component').then(m => m.OffreEmploiUpdateComponent),
    resolve: {
      offreEmploi: OffreEmploiResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default offreEmploiRoute;
