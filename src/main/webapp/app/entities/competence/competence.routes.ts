import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import CompetenceResolve from './route/competence-routing-resolve.service';

const competenceRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/competence.component').then(m => m.CompetenceComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/competence-detail.component').then(m => m.CompetenceDetailComponent),
    resolve: {
      competence: CompetenceResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/competence-update.component').then(m => m.CompetenceUpdateComponent),
    resolve: {
      competence: CompetenceResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/competence-update.component').then(m => m.CompetenceUpdateComponent),
    resolve: {
      competence: CompetenceResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default competenceRoute;
