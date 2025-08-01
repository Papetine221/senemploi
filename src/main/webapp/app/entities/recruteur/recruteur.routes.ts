import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import RecruteurResolve from './route/recruteur-routing-resolve.service';

const recruteurRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/recruteur.component').then(m => m.RecruteurComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/recruteur-detail.component').then(m => m.RecruteurDetailComponent),
    resolve: {
      recruteur: RecruteurResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/recruteur-update.component').then(m => m.RecruteurUpdateComponent),
    resolve: {
      recruteur: RecruteurResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/recruteur-update.component').then(m => m.RecruteurUpdateComponent),
    resolve: {
      recruteur: RecruteurResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default recruteurRoute;
