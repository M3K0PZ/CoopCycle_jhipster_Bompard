import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ICooperative } from '../cooperative.model';
import { CooperativeService } from '../service/cooperative.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './cooperative-delete-dialog.component.html',
})
export class CooperativeDeleteDialogComponent {
  cooperative?: ICooperative;

  constructor(protected cooperativeService: CooperativeService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.cooperativeService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
