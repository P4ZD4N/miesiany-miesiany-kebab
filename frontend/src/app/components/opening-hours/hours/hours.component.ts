import { Component, OnInit } from '@angular/core';
import { TranslateModule } from '@ngx-translate/core';
import { OpeningHoursService } from '../../../services/opening-hours/opening-hours.service';
import { CommonModule } from '@angular/common';
import { AuthenticationService } from '../../../services/authentication/authentication.service';

@Component({
  selector: 'app-hours',
  standalone: true,
  imports: [CommonModule, TranslateModule],
  templateUrl: './hours.component.html',
  styleUrl: './hours.component.scss'
})
export class HoursComponent implements OnInit {

  openingHours: any[] = [];

  constructor(private authenticationService: AuthenticationService, private openingHoursService: OpeningHoursService) {}

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
    const currentDayOfWeek: string = this.getCurrentDayOfWeek();
    const currentTime = this.getCurrentTime();
    return this.openingHours.some(hour => currentTime >= hour.opening_time && currentTime < hour.closing_time);
  }

  private getCurrentTime(): string {
    const now = new Date();
    const hours = now.getHours().toString().padStart(2, '0');
    const minutes = now.getMinutes().toString().padStart(2, '0');
    return `${hours}:${minutes}`;
  }
}
