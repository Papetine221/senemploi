import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import LocalisationResolve from './route/localisation-routing-resolve.service';

const localisationRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/localisation.component').then(m => m.LocalisationComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/localisation-detail.component').then(m => m.LocalisationDetailComponent),
    resolve: {
      localisation: LocalisationResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/localisation-update.component').then(m => m.LocalisationUpdateComponent),
    resolve: {
      localisation: LocalisationResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/localisation-update.component').then(m => m.LocalisationUpdateComponent),
    resolve: {
      localisation: LocalisationResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default localisationRoute;
