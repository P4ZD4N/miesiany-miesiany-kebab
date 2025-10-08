import { Component, OnInit } from '@angular/core';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { OpeningHoursService } from '../../../services/opening-hours/opening-hours.service';
import { CommonModule } from '@angular/common';
import { AuthenticationService } from '../../../services/authentication/authentication.service';
import {
  FormBuilder,
  FormControl,
  FormGroup,
  ReactiveFormsModule,
} from '@angular/forms';
import { LangService } from '../../../services/lang/lang.service';
import { Subscription } from 'rxjs';
import { OpeningHoursResponse } from '../../../responses/responses';
import Swal from 'sweetalert2';
import { TimeFormatPipe } from '../../../pipes/time-format.pipe';
import { AlertService } from '../../../services/alert/alert.service';

@Component({
  selector: 'app-hours',
  standalone: true,
  imports: [CommonModule, TranslateModule, ReactiveFormsModule, TimeFormatPipe],
  templateUrl: './hours.component.html',
  styleUrl: './hours.component.scss',
})
export class HoursComponent implements OnInit {
  openingHours: OpeningHoursResponse[] = [];
  hourForms: { [key: string]: FormGroup } = {};
  errorMessages: { [key: string]: string } = {};

  languageChangeSubscription: Subscription;

  isEditing: boolean = false;

  constructor(
    private authenticationService: AuthenticationService,
    private openingHoursService: OpeningHoursService,
    private formBuilder: FormBuilder,
    private langService: LangService,
    private translate: TranslateService,
    private alertService: AlertService
  ) {
    this.languageChangeSubscription =
      this.langService.languageChanged$.subscribe(() => {
        this.hideErrorMessages();
      });
  }

  ngOnInit(): void {
    this.loadOpeningHours();
  }

  private loadOpeningHours(): void {
    this.openingHoursService.getOpeningHours().subscribe(
      (data: OpeningHoursResponse[]) => {
        this.openingHours = data;
        this.initializeForms(data);
      },
      (error) => this.handleError(error)
    );
  }

  protected isManager(): boolean {
    return this.authenticationService.isManager();
  }

  protected getRowClass(hour: OpeningHoursResponse): string {
    const currentDay = this.getCurrentDayOfWeek();

    if (currentDay === hour.day_of_week.toUpperCase()) {
      return this.checkWhetherIsOpenNow()
        ? 'highlight-today-open'
        : 'highlight-today-closed';
    }

    return '';
  }

  private checkWhetherIsOpenNow(): boolean {
    const currentTime = this.getCurrentTime();
    const currentDay = this.getCurrentDayOfWeek();

    const currentDayOpeningHours = this.openingHours.find(
      (hour) => hour.day_of_week.toUpperCase() === currentDay
    );

    if (!currentDayOpeningHours) return false;

    const openingTime = this.convertToMinutes(
      currentDayOpeningHours.opening_time
    );
    const closingTime = this.convertToMinutes(
      currentDayOpeningHours.closing_time
    );
    const currentMinutes = this.convertToMinutes(currentTime);

    return currentMinutes >= openingTime && currentMinutes < closingTime;
  }

  private getCurrentTime(): string {
    const now = new Date();
    const hours = now.getHours().toString().padStart(2, '0');
    const minutes = now.getMinutes().toString().padStart(2, '0');
    return `${hours}:${minutes}`;
  }

  private getCurrentDayOfWeek(): string {
    const daysOfWeek = [
      'SUNDAY',
      'MONDAY',
      'TUESDAY',
      'WEDNESDAY',
      'THURSDAY',
      'FRIDAY',
      'SATURDAY',
    ];
    const currentDayIndex = new Date().getDay();
    
    return daysOfWeek[currentDayIndex];
  }


  private convertToMinutes(time: string): number {
    const [hours, minutes] = time.split(':').map(Number);
    return hours * 60 + minutes;
  }

  private initializeForms(hours: OpeningHoursResponse[]): void {
    hours.forEach((hour) => {
      this.hourForms[hour.day_of_week] = this.formBuilder.group({
        opening_time: new FormControl(hour.opening_time),
        closing_time: new FormControl(hour.closing_time),
      });
    });
  }

  protected startUpdatingOpeningHour(hour: OpeningHoursResponse): void {
    if (this.isEditing) return;

    this.hideErrorMessages();
    this.isEditing = true;
    hour.isEditing = true;

    const form = this.hourForms[hour.day_of_week];
    
    form.patchValue({
      opening_time: hour.opening_time,
      closing_time: hour.closing_time,
    });
  }

  protected updateOpeningHour(hour: OpeningHoursResponse): void {
    const formGroup = this.hourForms[hour.day_of_week];
    const newOpeningTime = formGroup.get('opening_time')!.value;
    const newClosingTime = formGroup.get('closing_time')!.value;

    const updatedHour = {
      day_of_week: hour.day_of_week,
      opening_time: newOpeningTime,
      closing_time: newClosingTime,
    };

    this.openingHoursService.updateOpeningHour(updatedHour).subscribe(
      () => {
        const translatedDayOfWeek = this.translate
              .instant('opening-hours.days.' + updatedHour.day_of_week)
              .toLowerCase();

        this.alertService.showSuccessfulOpeningHourUpdateAlert(translatedDayOfWeek);

        hour.isEditing = false;
        this.isEditing = false;

        this.hideErrorMessages();
        this.loadOpeningHours();
      },
      (error) => this.handleError(error)
    );
  }

  protected stopUpdatingOpeningHour(hour: OpeningHoursResponse): void {
    hour.isEditing = false;
    this.isEditing = false;
    this.hideErrorMessages();
  }

  protected handleError(error: any): void {
    error.errorMessages
      ? (this.errorMessages = error.errorMessages)
      : (this.errorMessages = { general: 'An unexpected error occurred' });
  }

  protected hideErrorMessages(): void {
    this.errorMessages = {};
  }
}
