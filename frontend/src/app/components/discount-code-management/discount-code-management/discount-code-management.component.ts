import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { LangService } from '../../../services/lang/lang.service';
import { AuthenticationService } from '../../../services/authentication/authentication.service';
import { DiscountCodeResponse } from '../../../responses/responses';
import { DiscountCodesService } from '../../../services/discount-codes/discount-codes.service';

@Component({
  selector: 'app-discount-code-management',
  standalone: true,
  imports: [CommonModule, TranslateModule],
  templateUrl: './discount-code-management.component.html',
  styleUrl: './discount-code-management.component.scss'
})
export class DiscountCodeManagementComponent implements OnInit {

  discountCodes: DiscountCodeResponse[] = [];

  constructor(
      private langService: LangService,
      private authenticationService: AuthenticationService,
      private translate: TranslateService,
      private discountCodesService: DiscountCodesService
  ) {}

  ngOnInit(): void {
    if (this.isManager()) {
      this.loadDiscountCodes();
    }
  }

  loadDiscountCodes(): void {
    this.discountCodesService.getDiscountCodes().subscribe(
      (data: DiscountCodeResponse[]) => {
        this.discountCodes = data;
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

  addDiscountCode(): void {

  }
}
