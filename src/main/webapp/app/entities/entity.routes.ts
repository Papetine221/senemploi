import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'authority',
    data: { pageTitle: 'senemploiV4App.adminAuthority.home.title' },
    loadChildren: () => import('./admin/authority/authority.routes'),
  },
  {
    path: 'candidat',
    data: { pageTitle: 'senemploiV4App.candidat.home.title' },
    loadChildren: () => import('./candidat/candidat.routes'),
  },
  {
    path: 'recruteur',
    data: { pageTitle: 'senemploiV4App.recruteur.home.title' },
    loadChildren: () => import('./recruteur/recruteur.routes'),
  },
  {
    path: 'offre-emploi',
    data: { pageTitle: 'senemploiV4App.offreEmploi.home.title' },
    loadChildren: () => import('./offre-emploi/offre-emploi.routes'),
  },
  {
    path: 'candidature',
    data: { pageTitle: 'senemploiV4App.candidature.home.title' },
    loadChildren: () => import('./candidature/candidature.routes'),
  },
  {
    path: 'type-contrat',
    data: { pageTitle: 'senemploiV4App.typeContrat.home.title' },
    loadChildren: () => import('./type-contrat/type-contrat.routes'),
  },
  {
    path: 'localisation',
    data: { pageTitle: 'senemploiV4App.localisation.home.title' },
    loadChildren: () => import('./localisation/localisation.routes'),
  },
  {
    path: 'competence',
    data: { pageTitle: 'senemploiV4App.competence.home.title' },
    loadChildren: () => import('./competence/competence.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
