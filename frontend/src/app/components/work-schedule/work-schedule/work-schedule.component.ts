import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { TranslateModule } from '@ngx-translate/core';
import { AuthenticationService } from '../../../services/authentication/authentication.service';
import { LangService } from '../../../services/lang/lang.service';
import { EmployeeService } from '../../../services/employees/employee.service';
import {
  EmployeeResponse,
  OpeningHoursResponse,
  WorkScheduleEntryResponse,
} from '../../../responses/responses';
import { Subscription } from 'rxjs';
import { WorkScheduleService } from '../../../services/work-schedule/work-schedule.service';
import {
  NewWorkScheduleEntryRequest,
  RemovedWorkScheduleEntryRequest,
} from '../../../requests/requests';
import { OpeningHoursService } from '../../../services/opening-hours/opening-hours.service';
import { AlertService } from '../../../services/alert/alert.service';

@Component({
  selector: 'app-work-schedule',
  standalone: true,
  imports: [CommonModule, TranslateModule],
  templateUrl: './work-schedule.component.html',
  styleUrl: './work-schedule.component.scss',
})
export class WorkScheduleComponent implements OnInit {
  days: string[] = ['Pon', 'Wt', 'Śr', 'Czw', 'Pt', 'Sob', 'Niedz'];
  daysInSchedule: string[] = [];
  scheduleDate: string = '';

  employees: EmployeeResponse[] = [];
  openingHours: OpeningHoursResponse[] = [];
  workScheduleEntries: WorkScheduleEntryResponse[] = [];
  currentEmployeeData: EmployeeResponse | null = null;

  firstDayOfCurrentMonth: number = 0;
  lastDayOfCurrentMonth: number = 0;
  currentMonth: number = new Date().getMonth() + 1;
  currentYear: number = new Date().getFullYear();

  languageChangeSubscription: Subscription;

  constructor(
    private authenticationService: AuthenticationService,
    private langService: LangService,
    private employeeService: EmployeeService,
    private workScheduleService: WorkScheduleService,
    private openingHoursService: OpeningHoursService,
    private alertService: AlertService
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

    if (this.isManager() || this.isEmployee()) {
      this.loadEmployees();
      this.loadCurrentEmployeeDetails();
      this.loadWorkScheduleEntries();
      this.loadOpeningHours();
    }
  }

  private setDays(): void {
    this.days =
      this.langService.currentLang === 'pl'
        ? [
            'Poniedziałek',
            'Wtorek',
            'Środa',
            'Czwartek',
            'Piątek',
            'Sobota',
            'Niedziela',
          ]
        : [
            'Monday',
            'Tuesday',
            'Wednesday',
            'Thursday',
            'Friday',
            'Saturday',
            'Sunday',
          ];
  }

  private generateMonthDays(): string[] {
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

  private loadEmployees(): void {
    this.employeeService.getEmployees().subscribe({
      next: (data: EmployeeResponse[]) =>
        (this.employees = data
          .filter((emp) => emp.job.toLowerCase() !== 'manager')
          .filter((emp) => emp.is_active === true)
          .sort((a, b) => a.last_name.localeCompare(b.last_name))),
      error: (error) => console.log('Error loading employees', error),
    });
  }

  private loadCurrentEmployeeDetails(): void {
    this.employeeService.getCurrentEmployee().subscribe({
      next: (data: EmployeeResponse) => (this.currentEmployeeData = data),
      error: (error) =>
        console.log('Error loading current employee data', error),
    });
  }

  private loadWorkScheduleEntries(): void {
    this.workScheduleService.getWorkScheduleEntries().subscribe({
      next: (data: WorkScheduleEntryResponse[]) =>
        (this.workScheduleEntries = data),
      error: (error) =>
        console.log('Error loading work schedule entries', error),
    });
  }

  private loadOpeningHours(): void {
    this.openingHoursService.getOpeningHours().subscribe({
      next: (data: OpeningHoursResponse[]) => (this.openingHours = data),
      error: (error) => console.log('Error loading opening hours', error),
    });
  }

  protected isManager(): boolean {
    return this.authenticationService.isManager();
  }

  protected isEmployee(): boolean {
    return this.authenticationService.isEmployee();
  }

  private getScheduleDate(): string {
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

  protected nextMonth(): void {
    if (this.currentMonth === 12) {
      this.currentMonth = 1;
      this.currentYear++;
    } else {
      this.currentMonth++;
    }

    this.daysInSchedule = this.generateMonthDays();
    this.scheduleDate = this.getScheduleDate();
  }

  protected previousMonth(): void {
    if (this.currentMonth === 1) {
      this.currentMonth = 12;
      this.currentYear--;
    } else {
      this.currentMonth--;
    }

    this.daysInSchedule = this.generateMonthDays();
    this.scheduleDate = this.getScheduleDate();
  }

  protected startAddingWorkScheduleEntry(
    employee: EmployeeResponse,
    day: string
  ) {
    this.alertService
      .showAddWorkScheduleEntryAlert(employee, day)
      .then((result) => {
        if (!(result.isConfirmed && result.value)) return;

        const startTime = result.value.startTime;
        const endTime = result.value.endTime;
        const employee = result.value.employee;

        const parts = result.value.day.split(' ');
        const day = parts[0];
        const dayIndex = this.days.indexOf(day);
        const openingHourAtChosenDay = this.openingHours[dayIndex].opening_time;
        const closingHourAtChosenDay = this.openingHours[dayIndex].closing_time;
        const rawDate = parts[1].replace(')', '').replace('(', '');
        const [dd, mm, yyyy] = rawDate.split('.');
        const formattedDate = `${yyyy}-${mm}-${dd}`;

        if (
          startTime < openingHourAtChosenDay ||
          endTime > closingHourAtChosenDay
        ) {
          this.alertService
            .showConfirmWorkScheduleEntryAlert(
              day,
              openingHourAtChosenDay,
              closingHourAtChosenDay,
              startTime,
              endTime
            )
            .then((confirmed) => {
              if (!confirmed) return;

              this.addWorkScheduleEntry({
                employee_email: employee.email,
                date: formattedDate,
                start_time: startTime,
                end_time: endTime,
              });
            });

          return;
        }

        this.addWorkScheduleEntry({
          employee_email: employee.email,
          date: formattedDate,
          start_time: startTime,
          end_time: endTime,
        });
      });
  }

  private addWorkScheduleEntry(request: NewWorkScheduleEntryRequest): void {
    this.workScheduleService.addWorkScheduleEntry(request).subscribe({
      next: () => {
        this.alertService.showSuccessfulAddWorkScheduleEntryAlert();
        this.loadWorkScheduleEntries();
      },
      error: (err) => this.alertService.showAddWorkScheduleEntryErrorAlert(err),
    });
  }

  protected removeWorkScheduleEntry(
    employeeEmail: string,
    day: string,
    startTime: string,
    endTime: string
  ): void {
    let matchingEntries = this.getMatchingEntries(employeeEmail, day).filter(
      (entry) => entry.start_time === startTime && entry.end_time === endTime
    );

    if (matchingEntries.length !== 1) return;

    this.alertService.showRemoveWorkScheduleEntryAlert().then((confirmed) => {
      if (!confirmed) return;

      this.workScheduleService
        .removeWorkScheduleEntry({
          id: matchingEntries[0].id,
        } as RemovedWorkScheduleEntryRequest)
        .subscribe(() => {
          this.alertService.showSuccessfulWorkScheduleEntryRemoveAlert();
          this.loadWorkScheduleEntries();
        });
    });
  }

  protected getMatchingEntries(
    employeeEmail: string,
    day: string
  ): WorkScheduleEntryResponse[] {
    const parts = day.split(' ');
    const rawDate = parts[1].replace(')', '').replace('(', '');

    return this.workScheduleEntries.filter((entry) => {
      const [dd, mm, yyyy] = rawDate.split('.');
      const formattedDate = `${yyyy}-${mm}-${dd}`;
      return (
        entry.employee_email === employeeEmail && entry.date === formattedDate
      );
    });
  }

  protected downloadWorkSchedule(): void {
    let lastDayOfCurrentMonth = new Date(
      this.currentYear,
      this.currentMonth,
      0
    ).getDate();
    let monthFormatted =
      this.currentMonth.toString().length == 2
        ? this.currentMonth
        : '0' + this.currentMonth;
    let startDate = `${this.currentYear}-${monthFormatted}-01`;
    let endDate = `${this.currentYear}-${monthFormatted}-${lastDayOfCurrentMonth}`;

    this.workScheduleService.getWorkSchedulePDF(startDate, endDate).subscribe({
      next: (response: Blob) => {
        const url = window.URL.createObjectURL(response);
        window.open(url);
      },
      error: (error) =>
        console.error('Error previewing work schedule: ', error),
    });
  }

  protected shouldHighlight(employee: EmployeeResponse): boolean {
    return employee.email === this.currentEmployeeData?.email;
  }
}
