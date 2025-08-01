import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { IRecruteur } from '../recruteur.model';

@Component({
  selector: 'jhi-recruteur-detail',
  templateUrl: './recruteur-detail.component.html',
  imports: [SharedModule, RouterModule],
})
export class RecruteurDetailComponent {
  recruteur = input<IRecruteur | null>(null);

  previousState(): void {
    window.history.back();
  }
}
