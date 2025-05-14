import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { LangService } from '../../../services/lang/lang.service';
import { AuthenticationService } from '../../../services/authentication/authentication.service';
import { DiscountCodeResponse } from '../../../responses/responses';
import { DiscountCodesService } from '../../../services/discount-codes/discount-codes.service';
import { NewDiscountCodeRequest, RemovedDiscountCodeRequest, UpdatedDiscountCodeRequest } from '../../../requests/requests';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import Swal from 'sweetalert2';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-discount-code-management',
  standalone: true,
  imports: [CommonModule, TranslateModule, ReactiveFormsModule, FormsModule],
  templateUrl: './discount-code-management.component.html',
  styleUrl: './discount-code-management.component.scss'
})
export class DiscountCodeManagementComponent implements OnInit {

  newDiscountCode: NewDiscountCodeRequest = {
    code: '',
    discount_percentage: 0,
    expiration_date: null,
    remaining_uses: 0
  };
  updatedDiscountCode: UpdatedDiscountCodeRequest | null = null;

  errorMessages: { [key: string]: string } = {};
  languageChangeSubscription: Subscription;

  isAdding = false;
  isEditing = false;

  discountCodes: DiscountCodeResponse[] = [];

  constructor(
      private langService: LangService,
      private authenticationService: AuthenticationService,
      private translate: TranslateService,
      private discountCodesService: DiscountCodesService
  ) {
    this.languageChangeSubscription = this.langService.languageChanged$.subscribe(() => {
      this.hideErrorMessages();
    })
  }

  ngOnInit(): void {
    if (this.isManager()) {
      this.loadDiscountCodes();
    }
  }

  loadDiscountCodes(): void {
    this.discountCodesService.getDiscountCodes().subscribe(
      (data: DiscountCodeResponse[]) => {
        this.discountCodes = data.sort((a, b) => a.code.localeCompare(b.code));
      },
      (error) => {
        console.log('Error loading discount codes', error);
      }
    );
  }

  isManager(): boolean {
    return this.authenticationService.isManager();
  }

  isEmployee(): boolean {
    return this.authenticationService.isEmployee();
  }

  getDateClass(date: Date): string {
    const today = new Date();
    const compareDate = new Date(date);

    today.setHours(0, 0, 0, 0);
    compareDate.setHours(0, 0, 0, 0);

    if (compareDate < today) {
      return 'expired';
    } else if (compareDate.getTime() === today.getTime()) {
      return 'today';
    } else {
      return 'future';
    }
  }

  showAddDiscountCodeTable(): void {
    this.hideErrorMessages();
    this.isAdding = true;
  }

  hideAddDiscountCodeTable(): void {
    this.hideErrorMessages();
    this.isAdding = false;
    this.resetNewDiscountCode();
  }

  resetNewDiscountCode(): void {
    this.newDiscountCode = {
      code: '',
      discount_percentage: 0,
      expiration_date: null,
      remaining_uses: 0
    };
  }

  addDiscountCode(): void {

    if (this.newDiscountCode.code === '') {
      this.newDiscountCode.code = null;
    }

    this.discountCodesService.addDiscountCode(this.newDiscountCode).subscribe({
      next: (response) => {
        Swal.fire({
          text: this.langService.currentLang === 'pl' ? `Pomyslnie dodano nowy kod rabatowy'!` : `Successfully added new discount code!`,
          icon: 'success',
          iconColor: 'green',
          confirmButtonColor: 'green',
          background: 'black',
          color: 'white',
          confirmButtonText: 'Ok',
        });

        this.loadDiscountCodes();
        this.resetNewDiscountCode();
        this.hideAddDiscountCodeTable();
        this.hideErrorMessages();
      },
      error: (error) => {
        this.handleError(error);
      },
    });
  }

  editDiscountCodeRow(discountCode: DiscountCodeResponse) {
    if (this.isEditing) {
      return;
    }

    this.hideErrorMessages();
    this.isEditing = true;

    this.updatedDiscountCode = {
      code: discountCode.code,
      updated_code: discountCode.code,
      updated_discount_percentage: discountCode.discount_percentage,
      updated_expiration_date: discountCode.expiration_date,
      updated_remaining_uses: discountCode.remaining_uses
    };
  }

  updateDiscountCode(discountCode: UpdatedDiscountCodeRequest) {

    this.discountCodesService.updateDiscountCode(discountCode).subscribe({
      next: (response) => {
        Swal.fire({
          text: this.langService.currentLang === 'pl' ? 'Pomyslnie zaktualizowano kod rabatowy!' : 'Successfully updated discount code!',
          icon: 'success',
          iconColor: 'green',
          confirmButtonColor: 'green',
          background: 'black',
          color: 'white',
          confirmButtonText: 'Ok',
        });
  
        this.loadDiscountCodes();
        this.hideUpdateDiscountCodeTable();
      },
      error: (error) => {
        this.handleError(error);
      }
    });
  }

  removeDiscountCode(discountCode: DiscountCodeResponse): void {
      const confirmationMessage =
        this.langService.currentLang === 'pl'
        ? `Czy na pewno chcesz usunac ten kod rabatowy?`
        : `Are you sure you want to remove this discount code?`;
  
      Swal.fire({
        title: this.langService.currentLang === 'pl' ? 'Potwierdzenie' : 'Confirmation',
        text: confirmationMessage,
        icon: 'warning',
        iconColor: 'red',
        showCancelButton: true,
        confirmButtonColor: '#0077ff',
        cancelButtonColor: 'red',
        background: 'black',
        color: 'white',
        confirmButtonText: this.langService.currentLang === 'pl' ? 'Tak' : 'Yes',
        cancelButtonText: this.langService.currentLang === 'pl' ? 'Anuluj' : 'Cancel',
      }).then((result) => {
        if (result.isConfirmed) {
          this.discountCodesService.removeDiscountCode({ code: discountCode.code } as RemovedDiscountCodeRequest).subscribe(() => {
            Swal.fire({
              text: this.langService.currentLang === 'pl' ? `Pomyslnie usunieto kod rabatowy!` : `Successfully removed discount code!`,
              icon: 'success',
              iconColor: 'green',
              confirmButtonColor: 'green',
              background: 'black',
              color: 'white',
              confirmButtonText: 'Ok',
            });
            this.loadDiscountCodes();
          });
        }
      });
    }

  hideUpdateDiscountCodeTable(): void {
    this.isEditing = false;
    this.hideErrorMessages();
  }

  hideErrorMessages(): void {
    this.errorMessages = {};
  }

  handleError(error: any) {
    if (error.errorMessages) {
      this.errorMessages = error.errorMessages;
      console.log(this.errorMessages);
    } else {
      this.errorMessages = { general: 'An unexpected error occurred' };
    }
  }
}
