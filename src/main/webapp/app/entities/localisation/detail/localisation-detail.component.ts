import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { ILocalisation } from '../localisation.model';

@Component({
  selector: 'jhi-localisation-detail',
  templateUrl: './localisation-detail.component.html',
  imports: [SharedModule, RouterModule],
})
export class LocalisationDetailComponent {
  localisation = input<ILocalisation | null>(null);

  previousState(): void {
    window.history.back();
  }
}
