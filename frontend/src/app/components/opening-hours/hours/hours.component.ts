import { Component, OnInit } from '@angular/core';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { OpeningHoursService } from '../../../services/opening-hours/opening-hours.service';
import { CommonModule } from '@angular/common';
import { AuthenticationService } from '../../../services/authentication/authentication.service';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { LangService } from '../../../services/lang/lang.service';
import { Subscription } from 'rxjs';
import { OpeningHoursResponse } from '../../../responses/responses';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-hours',
  standalone: true,
  imports: [CommonModule, TranslateModule, ReactiveFormsModule],
  templateUrl: './hours.component.html',
  styleUrl: './hours.component.scss'
})
export class HoursComponent implements OnInit {

  openingHours: OpeningHoursResponse[] = [];
  hourForms: { [key: string]: FormGroup } = {};
  errorMessages: { [key: string]: string } = {};
  languageChangeSubscription: Subscription;
  isEditing = false;

  constructor(
    private authenticationService: AuthenticationService, 
    private openingHoursService: OpeningHoursService,
    private formBuilder: FormBuilder,
    private langService: LangService,
    private translate: TranslateService
  ) {
    this.languageChangeSubscription = this.langService.languageChanged$.subscribe(() => {
      this.hideErrorMessages();
    })
  }

  ngOnInit(): void {
    this.loadOpeningHours();
  }

  isManager(): boolean {
    return this.authenticationService.isManager();
  }

   loadOpeningHours(): void {
    this.openingHoursService.getOpeningHours().subscribe(
      (data: OpeningHoursResponse[]) => {
        this.openingHours = data;
        this.initializeForms(data);
      },
      error => {
        this.handleError(error);
      },
    );
  }

  formatTime(time: string): string {
    const hours = time.slice(0, 2);
    const minutes = time.slice(3, 5);
    return `${hours}:${minutes}`;
  }

  getCurrentDayOfWeek(): string {
    const daysOfWeek = ['SUNDAY', 'MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY'];
    const currentDayIndex = new Date().getDay();
    return daysOfWeek[currentDayIndex];
  }

  checkWhetherIsOpenNow(): boolean {
    const currentTime = this.getCurrentTime();
    const currentDay = this.getCurrentDayOfWeek();
    
    const currentDayOpeningHours = this.openingHours.find(
      hour => hour.day_of_week.toUpperCase() === currentDay
    );
  
    if (!currentDayOpeningHours) return false; 
  
    const openingTime = this.convertToMinutes(currentDayOpeningHours.opening_time);
    const closingTime = this.convertToMinutes(currentDayOpeningHours.closing_time);
    const currentMinutes = this.convertToMinutes(currentTime);
    
    return currentMinutes >= openingTime && currentMinutes < closingTime;
  }
  
  private convertToMinutes(time: string): number {
    const [hours, minutes] = time.split(':').map(Number);
    return hours * 60 + minutes;
  }

  private getCurrentTime(): string {
    const now = new Date();
    const hours = now.getHours().toString().padStart(2, '0');
    const minutes = now.getMinutes().toString().padStart(2, '0');
    return `${hours}:${minutes}`;
  }

  initializeForms(hours: OpeningHoursResponse[]): void {
    hours.forEach((hour) => {
      this.hourForms[hour.day_of_week] = this.formBuilder.group({
        opening_time: new FormControl(hour.opening_time),
        closing_time: new FormControl(hour.closing_time),
      });
    });
  }

  editRow(hour: OpeningHoursResponse): void {
    if (this.isEditing) {
      return;
    }
    this.hideErrorMessages();
    this.isEditing = true;
    hour.isEditing = true;
    const form = this.hourForms[hour.day_of_week];
    form.patchValue({
      opening_time: hour.opening_time,
      closing_time: hour.closing_time,
    });
  }

  saveRow(hour: OpeningHoursResponse): void {
    const formGroup = this.hourForms[hour.day_of_week];
    const newOpeningTime = formGroup.get('opening_time')!.value;
    const newClosingTime = formGroup.get('closing_time')!.value;

    const updatedHour = {
      day_of_week: hour.day_of_week,
      opening_time: newOpeningTime,
      closing_time: newClosingTime
    };

    this.openingHoursService.updateOpeningHour(updatedHour).subscribe(
      response => {
        this.hideErrorMessages();
        const translatedDayOfWeek = this.translate.instant('opening-hours.days.' + updatedHour.day_of_week).toLowerCase();

        Swal.fire({
          text: this.langService.currentLang === 'pl' 
          ? `Pomyslnie zapisano godziny otwarcia w ${translatedDayOfWeek}!` 
          : `Successfully saved opening hours on ${translatedDayOfWeek}!`,
          icon: 'success',
          iconColor: 'green',
          confirmButtonColor: 'green',
          background: '#141414',
          color: 'white',
          confirmButtonText: 'Ok',
        });
        
        hour.isEditing = false;
        this.isEditing = false;
        this.hideErrorMessages();
        this.loadOpeningHours();
      },
      error => {
        this.handleError(error);
      },
    );
  }

  getRowClass(hour: OpeningHoursResponse): string {
    const currentDay = this.getCurrentDayOfWeek();
    
    if (currentDay === hour.day_of_week.toUpperCase()) {
      return this.checkWhetherIsOpenNow() ? 'highlight-today-open' : 'highlight-today-closed';
    }

    return '';
  }

  hideErrorMessages(): void {
    this.errorMessages = {};
  }

  hideRow(hour: OpeningHoursResponse): void {
    hour.isEditing = false;
    this.isEditing = false;
    this.hideErrorMessages();
  }

  handleError(error: any) {
    if (error.errorMessages) {
      this.errorMessages = error.errorMessages;
    } else {
      this.errorMessages = { general: 'An unexpected error occurred' };
    }
  }
}
