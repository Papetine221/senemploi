import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { ICompetence } from '../competence.model';
import { CompetenceService } from '../service/competence.service';

@Component({
  templateUrl: './competence-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class CompetenceDeleteDialogComponent {
  competence?: ICompetence;

  protected competenceService = inject(CompetenceService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.competenceService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
