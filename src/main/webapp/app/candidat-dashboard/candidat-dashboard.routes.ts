import { Routes } from '@angular/router';

import { Authority } from 'app/config/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { CandidatDashboardComponent } from './candidat-dashboard.component';

const candidatDashboardRoute: Routes = [
  {
    path: '',
    component: CandidatDashboardComponent,
    canActivate: [UserRouteAccessService],
    data: {
      authorities: ['ROLE_CANDIDAT'],
      pageTitle: 'candidatDashboard.title'
    }
  },
  {
    path: 'mes-candidatures',
    loadComponent: () => import('../entities/candidature/mes-candidatures/mes-candidatures.component').then(m => m.MesCandidaturesComponent),
    canActivate: [UserRouteAccessService],
    data: {
      authorities: ['ROLE_CANDIDAT'],
      pageTitle: 'Mes Candidatures'
    }
  }
];

export default candidatDashboardRoute;
