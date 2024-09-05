import { Component, OnInit } from '@angular/core';
import { TranslateModule } from '@ngx-translate/core';
import { OpeningHoursService } from '../../../services/opening-hours/opening-hours.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-hours',
  standalone: true,
  imports: [CommonModule, TranslateModule],
  templateUrl: './hours.component.html',
  styleUrl: './hours.component.scss'
})
export class HoursComponent implements OnInit {

  openingHours: any[] = [];

  constructor(private openingHoursService: OpeningHoursService) {}

  ngOnInit(): void {
    this.loadOpeningHours();
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
}
