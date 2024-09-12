import { Component, OnInit } from '@angular/core';
import { TranslateModule } from '@ngx-translate/core';
import { OpeningHoursService } from '../../../services/opening-hours/opening-hours.service';
import { CommonModule } from '@angular/common';
import { AuthenticationService } from '../../../services/authentication/authentication.service';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { LangService } from '../../../services/lang/lang.service';

@Component({
  selector: 'app-hours',
  standalone: true,
  imports: [CommonModule, TranslateModule, ReactiveFormsModule],
  templateUrl: './hours.component.html',
  styleUrl: './hours.component.scss'
})
export class HoursComponent implements OnInit {

  openingHours: any[] = [];
  hourForms: { [key: string]: FormGroup } = {};
  formErrorMessage: string | null = null;

  constructor(
    private authenticationService: AuthenticationService, 
    private openingHoursService: OpeningHoursService,
    private formBuilder: FormBuilder,
    private langService: LangService
  ) {}

  ngOnInit(): void {
    this.loadOpeningHours();
  }

  isManager(): boolean {
    return this.authenticationService.isManager();
  }

  loadOpeningHours(): void {
    this.openingHoursService.getOpeningHours().subscribe(
      (data: any[]) => {
          this.openingHours = data;
          this.initializeForms(data);
      },
      (error) => {
        console.log('Error loading opening hours', error);
      }
    )
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
    return this.openingHours.some(hour => currentTime >= hour.opening_time && currentTime < hour.closing_time);
  }

  private getCurrentTime(): string {
    const now = new Date();
    const hours = now.getHours().toString().padStart(2, '0');
    const minutes = now.getMinutes().toString().padStart(2, '0');
    return `${hours}:${minutes}`;
  }

  initializeForms(hours: any[]): void {
    hours.forEach((hour) => {
      this.hourForms[hour.day_of_week] = this.formBuilder.group({
        opening_time: new FormControl(hour.opening_time),
        closing_time: new FormControl(hour.closing_time),
      });
    });
  }

  editRow(hour: any): void {
    hour.isEditing = true;

    const form = this.hourForms[hour.day_of_week];
    form.patchValue({
      opening_time: hour.opening_time,
      closing_time: hour.closing_time,
    });
  }

  saveRow(hour: any): void {
    const formGroup = this.hourForms[hour.day_of_week];
    const newOpeningTime = formGroup.get('opening_time')!.value;
    const newClosingTime = formGroup.get('closing_time')!.value;

    if (newClosingTime < newOpeningTime) {
      if (this.langService.currentLang === 'pl') {
        this.formErrorMessage = 'Godzina zamknięcia nie może być wcześniejsza niż godzina otwarcia!';
      } else if (this.langService.currentLang === 'en') {
        this.formErrorMessage = 'Closing hour cannot be earlier than opening hour!';
      }
      return;
    }

    const updatedHour = {
      day_of_week: hour.day_of_week,
      opening_time: newOpeningTime,
      closing_time: newClosingTime
    };

    this.openingHoursService.updateOpeningHour(updatedHour).subscribe(
      response => {
        if (response) {
          this.formErrorMessage = null;
          hour.isEditing = false;
          this.loadOpeningHours();
        }
      }
    );
  }
}
