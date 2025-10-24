import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { TranslateModule } from '@ngx-translate/core';
import { LangService } from '../../../services/lang/lang.service';
import { AuthenticationService } from '../../../services/authentication/authentication.service';
import { DiscountCodeResponse } from '../../../responses/responses';
import { DiscountCodesService } from '../../../services/discount-codes/discount-codes.service';
import {
  NewDiscountCodeRequest,
  RemovedDiscountCodeRequest,
  UpdatedDiscountCodeRequest,
} from '../../../requests/requests';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { Subscription } from 'rxjs';
import { DateClassPipe } from '../../../pipes/date-class.pipe';
import { AlertService } from '../../../services/alert/alert.service';

@Component({
  selector: 'app-discount-code-management',
  standalone: true,
  imports: [
    CommonModule,
    TranslateModule,
    ReactiveFormsModule,
    FormsModule,
    DateClassPipe,
  ],
  templateUrl: './discount-code-management.component.html',
  styleUrl: './discount-code-management.component.scss',
})
export class DiscountCodeManagementComponent implements OnInit {
  discountCodes: DiscountCodeResponse[] = [];
  newDiscountCode: NewDiscountCodeRequest = {
    code: '',
    discount_percentage: 0,
    expiration_date: null,
    remaining_uses: 0,
  };
  updatedDiscountCode: UpdatedDiscountCodeRequest | null = null;

  errorMessages: { [key: string]: string } = {};

  isAdding: boolean = false;
  isEditing: boolean = false;

  languageChangeSubscription: Subscription;

  constructor(
    private langService: LangService,
    private authenticationService: AuthenticationService,
    private discountCodesService: DiscountCodesService,
    private alertService: AlertService
  ) {
    this.languageChangeSubscription =
      this.langService.languageChanged$.subscribe(() => {
        this.hideErrorMessages();
      });
  }

  ngOnInit(): void {
    if (this.isManager()) this.loadDiscountCodes();
  }

  private loadDiscountCodes(): void {
    this.discountCodesService.getDiscountCodes().subscribe({
      next: (data: DiscountCodeResponse[]) => {
        this.discountCodes = data.sort((a, b) => a.code.localeCompare(b.code));
      },
      error: (error) => console.log('Error loading discount codes', error),
    });
  }

  protected isManager(): boolean {
    return this.authenticationService.isManager();
  }

  protected isEmployee(): boolean {
    return this.authenticationService.isEmployee();
  }

  protected showAddDiscountCodeTable(): void {
    this.hideErrorMessages();
    this.isAdding = true;
  }

  protected showUpdateDiscountCodeTable(discountCode: DiscountCodeResponse): void {
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
      updated_remaining_uses: discountCode.remaining_uses,
    };
  }

  protected hideAddDiscountCodeTable(): void {
    this.hideErrorMessages();
    this.isAdding = false;
    this.resetNewDiscountCode();
  }

  protected hideUpdateDiscountCodeTable(): void {
    this.isEditing = false;
    this.hideErrorMessages();
  }

  protected addDiscountCode(): void {
    if (this.newDiscountCode.code === '') {
      this.newDiscountCode.code = null;
    }

    this.discountCodesService.addDiscountCode(this.newDiscountCode).subscribe({
      next: () => {
        this.alertService.showSuccessfulDiscountCodeAddAlert();
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

  protected updateDiscountCode(discountCode: UpdatedDiscountCodeRequest): void {
    this.discountCodesService.updateDiscountCode(discountCode).subscribe({
      next: () => {
        this.alertService.showSuccessfulDiscountCodeUpdateAlert();
        this.loadDiscountCodes();
        this.hideUpdateDiscountCodeTable();
      },
      error: (error) => {
        this.handleError(error);
      },
    });
  }

  protected removeDiscountCode(discountCode: DiscountCodeResponse): void {
    this.alertService.showRemoveDiscountCodeAlert().then((confirmed) => {
      if (!confirmed) return;

      this.discountCodesService
        .removeDiscountCode({
          code: discountCode.code,
        } as RemovedDiscountCodeRequest)
        .subscribe(() => {
          this.alertService.showSuccessfulDiscountCodeRemoveAlert();
          this.loadDiscountCodes();
        });
    });
  }

  private resetNewDiscountCode(): void {
    this.newDiscountCode = {
      code: '',
      discount_percentage: 0,
      expiration_date: null,
      remaining_uses: 0,
    };
  }

  private hideErrorMessages(): void {
    this.errorMessages = {};
  }

  private handleError(error: any): void {
    error.errorMessages
      ? (this.errorMessages = error.errorMessages)
      : (this.errorMessages = { general: 'An unexpected error occurred' });
  }
}
