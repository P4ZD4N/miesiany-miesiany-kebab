import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { AuthenticationService } from '../../../services/authentication/authentication.service';
import { LangService } from '../../../services/lang/lang.service';
import {
  EmployeeResponse,
  WorkScheduleEntryResponse,
} from '../../../responses/responses';
import { Subscription } from 'rxjs';
import { EmployeeService } from '../../../services/employees/employee.service';
import { WorkScheduleService } from '../../../services/work-schedule/work-schedule.service';

@Component({
  selector: 'app-payments',
  standalone: true,
  imports: [CommonModule, TranslateModule],
  templateUrl: './payments.component.html',
  styleUrl: './payments.component.scss',
})
export class PaymentsComponent implements OnInit {
  days: string[] = ['Pon', 'Wt', 'Śr', 'Czw', 'Pt', 'Sob', 'Niedz'];
  scheduleDate: string = '';
  currentMonth: number = new Date().getMonth() + 1;
  currentYear: number = new Date().getFullYear();
  daysInSchedule: string[] = [];
  employees: EmployeeResponse[] = [];
  workScheduleEntries: WorkScheduleEntryResponse[] = [];
  languageChangeSubscription: Subscription;

  constructor(
    private authenticationService: AuthenticationService,
    private langService: LangService,
    private employeeService: EmployeeService,
    private workScheduleService: WorkScheduleService
  ) {
    this.languageChangeSubscription =
      this.langService.languageChanged$.subscribe(() => {
        this.setDays();
        this.daysInSchedule = this.generateMonthDays();
      });
  }

  ngOnInit(): void {
    this.setDays();
    this.daysInSchedule = this.generateMonthDays();
    this.scheduleDate = this.getScheduleDate();

    if (this.isManager()) {
      this.loadEmployees();
      this.loadWorkScheduleEntries();
    }
  }

  loadWorkScheduleEntries(): void {
    this.workScheduleService.getWorkScheduleEntries().subscribe(
      (entries) => (this.workScheduleEntries = entries),
      (err) => console.error(err)
    );
  }

  loadEmployees(): void {
    this.employeeService.getEmployees().subscribe(
      (data: EmployeeResponse[]) =>
        (this.employees = data
          .filter((emp) => emp.job.toLowerCase() !== 'manager')
          .filter((emp) => emp.is_active === true)
          .sort((a, b) => a.last_name.localeCompare(b.last_name))),
      (error) => console.log('Error loading employees', error)
    );
  }

  setDays(): void {
    if (this.langService.currentLang === 'pl') {
      this.days = [
        'Poniedzialek',
        'Wtorek',
        'Sroda',
        'Czwartek',
        'Piatek',
        'Sobota',
        'Niedziela',
      ];
    } else {
      this.days = [
        'Monday',
        'Tuesday',
        'Wednesday',
        'Thursday',
        'Friday',
        'Saturday',
        'Sunday',
      ];
    }
  }

  isManager(): boolean {
    return this.authenticationService.isManager();
  }

  previousMonth(): void {
    if (this.currentMonth === 1) {
      this.currentMonth = 12;
      this.currentYear--;
    } else {
      this.currentMonth--;
    }

    this.daysInSchedule = this.generateMonthDays();
    this.scheduleDate = this.getScheduleDate();
  }

  generateMonthDays(): string[] {
    const daysInMonth = new Date(
      this.currentYear,
      this.currentMonth - 1,
      0
    ).getDate();
    const firstDay = new Date(
      this.currentYear,
      this.currentMonth - 1,
      1
    ).getDay();
    const startIndex = firstDay === 0 ? 6 : firstDay - 1;
    const monthDays: string[] = [];

    for (let i = 0; i < daysInMonth; i++) {
      const dayIndex = (startIndex + i) % 7;
      const currentDate = new Date(
        this.currentYear,
        this.currentMonth - 1,
        i + 1
      );
      const day = String(currentDate.getDate()).padStart(2, '0');
      const monthNum = String(currentDate.getMonth() + 1).padStart(2, '0');
      const yearStr = currentDate.getFullYear();
      const dateString = `${this.days[dayIndex]} (${day}.${monthNum}.${yearStr})`;

      monthDays.push(dateString);
    }

    return monthDays;
  }

  getScheduleDate(): string {
    let lastDayOfCurrentMonth = new Date(
      this.currentYear,
      this.currentMonth,
      0
    ).getDate();
    let monthFormatted =
      this.currentMonth.toString().length == 2
        ? this.currentMonth
        : '0' + this.currentMonth;
    return `01.${monthFormatted}.${this.currentYear} - ${lastDayOfCurrentMonth}.${monthFormatted}.${this.currentYear}`;
  }

  nextMonth(): void {
    if (this.currentMonth === 12) {
      this.currentMonth = 1;
      this.currentYear++;
    } else {
      this.currentMonth++;
    }

    this.daysInSchedule = this.generateMonthDays();
    this.scheduleDate = this.getScheduleDate();
  }

  getHoursFromCurrentMonth(employee: EmployeeResponse): number {
    const entries = this.workScheduleEntries.filter(
      (entry) =>
        entry.employee_email === employee.email &&
        new Date(entry.date).getMonth() + 1 === this.currentMonth &&
        new Date(entry.date).getFullYear() === this.currentYear
    );

    return entries.reduce((sum, entry) => {
      return sum + this.calculateHours(entry.start_time, entry.end_time);
    }, 0);
  }

  private calculateHours(startTime: string, endTime: string): number {
    const start = this.timeStringToHours(startTime);
    let end = this.timeStringToHours(endTime);
    if (end < start) end += 24;
    return end - start;
  }

  private timeStringToHours(time: string): number {
    const [hours, minutes, seconds] = time.split(':').map(Number);
    return hours + minutes / 60 + seconds / 3600;
  }

  getTotalFromCurrentMonth(employee: EmployeeResponse): number {
    const entries = this.workScheduleEntries.filter(
      (entry) =>
        entry.employee_email === employee.email &&
        new Date(entry.date).getMonth() + 1 === this.currentMonth &&
        new Date(entry.date).getFullYear() === this.currentYear
    );

    return entries.reduce((sum, entry) => {
      const hours = this.calculateHours(entry.start_time, entry.end_time);
      return sum + hours * entry.hourly_wage;
    }, 0);
  }

  getHourlyWageFromCurrentMonth(employee: EmployeeResponse): number | null {
    const entries = this.workScheduleEntries
      .filter(
        (entry) =>
          entry.employee_email === employee.email &&
          new Date(entry.date).getMonth() + 1 === this.currentMonth &&
          new Date(entry.date).getFullYear() === this.currentYear
      )
      .sort((a, b) => new Date(a.date).getTime() - new Date(b.date).getTime());

    return entries.length > 0 ? entries[0].hourly_wage : null;
  }
}
