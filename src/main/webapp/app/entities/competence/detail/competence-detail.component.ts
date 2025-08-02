import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { ICompetence } from '../competence.model';

@Component({
  selector: 'jhi-competence-detail',
  templateUrl: './competence-detail.component.html',
  imports: [SharedModule, RouterModule],
})
export class CompetenceDetailComponent {
  competence = input<ICompetence | null>(null);

  previousState(): void {
    window.history.back();
  }
}
