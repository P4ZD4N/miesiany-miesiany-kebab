import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { TranslateModule } from '@ngx-translate/core';
import { AuthenticationService } from '../../../services/authentication/authentication.service';
import { LangService } from '../../../services/lang/lang.service';
import { EmployeeService } from '../../../services/employees/employee.service';
import { EmployeeResponse, OpeningHoursResponse, RemovedWorkScheduleEntryResponse, WorkScheduleEntryResponse } from '../../../responses/responses';
import Swal from 'sweetalert2';
import { Subscription } from 'rxjs';
import { WorkScheduleService } from '../../../services/work-schedule/work-schedule.service';
import { NewWorkScheduleEntryRequest, RemovedWorkScheduleEntryRequest } from '../../../requests/requests';
import { OpeningHoursService } from '../../../services/opening-hours/opening-hours.service';

@Component({
  selector: 'app-work-schedule',
  standalone: true,
  imports: [CommonModule, TranslateModule],
  templateUrl: './work-schedule.component.html',
  styleUrl: './work-schedule.component.scss'
})
export class WorkScheduleComponent implements OnInit {
  days: string[] = ['Pon', 'Wt', 'Śr', 'Czw', 'Pt', 'Sob', 'Niedz'];
  daysInSchedule: string[] = [];
  employees: EmployeeResponse[] = [];
  openingHours: OpeningHoursResponse[] = [];
  workScheduleEntries: WorkScheduleEntryResponse[] = [];
  firstDayOfCurrentMonth: number = 0;
  lastDayOfCurrentMonth: number = 0;
  currentMonth: number = new Date().getMonth() + 1;
  currentYear: number = new Date().getFullYear();
  scheduleDate: string = '';
  languageChangeSubscription: Subscription;

  constructor(
    private authenticationService: AuthenticationService,
    private langService: LangService,
    private employeeService: EmployeeService,
    private workScheduleService: WorkScheduleService,
    private openingHoursService: OpeningHoursService
  ) {
    this.languageChangeSubscription = this.langService.languageChanged$.subscribe(() => {
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
      this.loadWorkScheduleEntries();
      this.loadOpeningHours();
    }
  }

  setDays(): void {
    if (this.langService.currentLang === 'pl') {
      this.days = ['Poniedzialek', 'Wtorek', 'Sroda', 'Czwartek', 'Piatek', 'Sobota', 'Niedziela'];
    } else {
      this.days = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday'];
    }
  }

  loadEmployees(): void {
    this.employeeService.getEmployees().subscribe(
      (data: EmployeeResponse[]) => this.employees = data
        .filter(emp => emp.job.toLowerCase() !== 'manager')
        .filter(emp => emp.is_active === true)
        .sort((a, b) => a.last_name.localeCompare(b.last_name)),
      (error) => console.log('Error loading employees', error));
  }

  loadWorkScheduleEntries(): void {
    this.workScheduleService.getWorkScheduleEntries().subscribe(
      (data: WorkScheduleEntryResponse[]) => this.workScheduleEntries = data,
      (error) => console.log('Error loading work schedule entries', error));
  }

  loadOpeningHours(): void {
    this.openingHoursService.getOpeningHours().subscribe(
      (data: OpeningHoursResponse[]) => this.openingHours = data,
      error => console.log('Error loading opening hours', error));
  }

  isManager(): boolean {
    return this.authenticationService.isManager();
  }

  isEmployee(): boolean {
    return this.authenticationService.isEmployee();
  }

  generateMonthDays(): string[] {
    const daysInMonth = new Date(this.currentYear, this.currentMonth - 1, 0).getDate();
    const firstDay = new Date(this.currentYear, this.currentMonth - 1, 1).getDay(); 
    const startIndex = firstDay === 0 ? 6 : firstDay - 1;
    const monthDays: string[] = [];

    for (let i = 0; i < daysInMonth; i++) {
      const dayIndex = (startIndex + i) % 7;
      const currentDate = new Date(this.currentYear, this.currentMonth - 1, i + 1);
      const day = String(currentDate.getDate()).padStart(2, '0');
      const monthNum = String(currentDate.getMonth() + 1).padStart(2, '0'); 
      const yearStr = currentDate.getFullYear();
      const dateString = `${this.days[dayIndex]} (${day}.${monthNum}.${yearStr})`;

      monthDays.push(dateString);
    }

    return monthDays;
  }

  getScheduleDate(): string {
    let lastDayOfCurrentMonth = new Date(this.currentYear, this.currentMonth, 0).getDate()
    let monthFormatted = this.currentMonth.toString().length == 2 ? this.currentMonth : "0" + this.currentMonth;
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

  startAddingWorkScheduleEntry(employee: EmployeeResponse, day: string) {

    let employeeTranslated = this.langService.currentLang === 'pl' ? 'Pracownik' : 'Employee';
    let dateTranslated = this.langService.currentLang === 'pl' ? 'Data' : 'Date';
    let startTimeTranslated = this.langService.currentLang === 'pl' ? 'Godzina rozpoczecia' : 'Start time';
    let endTimeTranslated = this.langService.currentLang === 'pl' ? 'Godzina zakonczenia' : 'End time';
    let nullValidationMessage = this.langService.currentLang === 'pl' ? 'Obie godziny powinny byc uzupelnione!' : 'Both start and end time should be fulfilled!';
    let invalidValidationMessage = this.langService.currentLang === 'pl' ? 'Godzina zakonczenia musi byc po godzinie rozpoczecia!' : 'End hour must be after start hour!';

    Swal.fire({
      title: this.langService.currentLang === 'pl' ? 'Dodaj wpis do grafiku' : 'Add work schedule entry',
      showCancelButton: true,
      confirmButtonColor: '#198754',
      cancelButtonColor: 'red',
      background: '#141414',
      color: 'white',
      confirmButtonText: this.langService.currentLang === 'pl' ? 'Dodaj' : 'Add',
      cancelButtonText: this.langService.currentLang === 'pl' ? 'Anuluj' : 'Cancel',
      customClass: {
        validationMessage: 'custom-validation-message',
        title: 'swal2-title-red'
      },
      html: `
        <span style="display:block; margin-bottom: 1rem;">
          <span style="color: red;">${employeeTranslated}:</span>
          ${employee.first_name} ${employee.last_name}
        </span>

        <span style="display:block; margin-bottom: 1rem;">
          <span style="color: red;">Email:</span> ${employee.email}
        </span>

        <span style="display:block; margin-bottom: 1rem;">
          <span style="color: red;">${dateTranslated}:</span> ${day}
        </span>

        <label style="display:block; margin-top: 2rem;">${startTimeTranslated}</label>
        <input id="start-time" type="time" class="swal2-input">

        <label style="display:block; margin-top: 1rem;">${endTimeTranslated}</label>
        <input id="end-time" type="time" class="swal2-input">
      `,
      preConfirm: () => {
        const startTime = (document.getElementById('start-time') as HTMLInputElement).value;
        const endTime = (document.getElementById('end-time') as HTMLInputElement).value;

        if (!startTime || !endTime) {
          Swal.showValidationMessage(nullValidationMessage);
          return false;
        }

        if (startTime >= endTime) {
          Swal.showValidationMessage(invalidValidationMessage);
          return false;
        }

        return { startTime, endTime, employee, day };
      }
    }).then((result) => {
      if (result.isConfirmed && result.value) {
        const startTime = result.value.startTime;
        const endTime = result.value.endTime;
        const employee = result.value.employee;

        const parts = result.value.day.split(" ");
        const day = parts[0];
        const dayIndex = this.days.indexOf(day);
        const openingHourAtChosenDay = this.openingHours[dayIndex].opening_time;
        const closingHourAtChosenDay = this.openingHours[dayIndex].closing_time;
        const rawDate = parts[1].replace(")", "").replace("(", "");
        const [dd, mm, yyyy] = rawDate.split(".");
        const formattedDate = `${yyyy}-${mm}-${dd}`;

        if (startTime < openingHourAtChosenDay || endTime > closingHourAtChosenDay) {
          Swal.fire({
            title: this.langService.currentLang === 'pl' ? 'Czy napewno dodac taki wpis?' : 'Are you sure you want to add such entry?',
            icon: 'warning',
            iconColor: 'red',
            showCancelButton: true,
            confirmButtonColor: '#0077ff',
            cancelButtonColor: 'red',
            background: '#141414',
            color: 'white',
            confirmButtonText: this.langService.currentLang === 'pl' ? 'Tak' : 'Yes',
            cancelButtonText: this.langService.currentLang === 'pl' ? 'Anuluj' : 'Cancel',
            html: `
              <span style="display:block; margin-bottom: 1rem;">
                ${this.langService.currentLang === 'pl' ? 'Rozwaz godziny otwarcia w ' : 'Consider opening hours at '}<span style="color: red;">${day}</span>
              </span>

              <span style="display:block; margin-bottom: 1rem;">
                ${this.langService.currentLang === 'pl' ? 'Otwarcie o ' : 'Opening at '}<span style="color: red;">${openingHourAtChosenDay}</span>
              </span>

              <span style="display:block; margin-bottom: 1rem;">
                ${this.langService.currentLang === 'pl' ? 'Zamkniecie o ' : 'Closing at '}<span style="color: red;">${closingHourAtChosenDay}</span>
              </span>

              <span style="display:block; margin-bottom: 1rem;">
                ${this.langService.currentLang === 'pl' ? 'Twoje dane dotyczace nowego wpisu' : 'Your data about new entry'}
              </span>

              <span style="display:block; margin-bottom: 1rem;">
                ${this.langService.currentLang === 'pl' ? 'Poczatek zmiany o ' : 'Beginning of shift at '}<span style="color: red;">${startTime}</span>
              </span>

              <span style="display:block; margin-bottom: 1rem;">
                ${this.langService.currentLang === 'pl' ? 'Koniec zmiany o ' : 'End of shift at '}<span style="color: red;">${endTime}</span>
              </span>
            `,
            customClass: { title: 'swal2-title-red' }
          }).then((result) => {
            if (result.isConfirmed) {
              this.addWorkScheduleEntry({
                employee_email: employee.email,
                date: formattedDate,
                start_time: startTime,
                end_time: endTime
              });
            }
          }); 

          return;
        }

        this.addWorkScheduleEntry({
          employee_email: employee.email,
          date: formattedDate,
          start_time: startTime,
          end_time: endTime
        });
      }
    });
  }

  addWorkScheduleEntry(request: NewWorkScheduleEntryRequest) {
    this.workScheduleService.addWorkScheduleEntry(request).subscribe(
      () => {
        Swal.fire({
          text: this.langService.currentLang === 'pl' ? `Pomyslnie dodano nowy wpis do grafiku!` : `Successfully added new work schedule entry!`,
          icon: 'success',
          iconColor: 'green',
          confirmButtonColor: 'green',
          background: '#141414',
          color: 'white',
          confirmButtonText: 'Ok',
        });
      this.loadWorkScheduleEntries();
    },
      (err) => {
        Swal.fire({
          text: err.errorMessages.message,
          icon: 'error',
          iconColor: 'red',
          confirmButtonColor: 'red',
          background: '#141414',
          color: 'white',
          confirmButtonText: 'Ok',
        });
      }
    );
  }

  getMatchingEntries(employeeEmail: string, day: string): WorkScheduleEntryResponse[] {
    const parts = day.split(" ");
    const rawDate = parts[1].replace(")", "").replace("(", "");

    return this.workScheduleEntries.filter(entry => {
      const [dd, mm, yyyy] = rawDate.split(".");
      const formattedDate = `${yyyy}-${mm}-${dd}`;
      return entry.employee.email === employeeEmail && entry.date === formattedDate;
    });
  }

  removeWorkScheduleEntry(employeeEmail: string, day: string, startTime: string, endTime: string): void {

    let matchingEntries = this.getMatchingEntries(employeeEmail, day).filter(entry =>
      entry.start_time === startTime && entry.end_time === endTime
    );
    
    if (matchingEntries.length !== 1) return;

    Swal.fire({
      title: this.langService.currentLang === 'pl' ? 'Potwierdzenie' : 'Confirmation',
      text: this.langService.currentLang === 'pl'
        ? `Czy na pewno chcesz usunac ten wpis?`
        : `Are you sure you want to remove this entry?`,
      icon: 'warning',
      iconColor: 'red',
      showCancelButton: true,
      confirmButtonColor: '#0077ff',
      cancelButtonColor: 'red',
      background: '#141414',
      color: 'white',
      confirmButtonText: this.langService.currentLang === 'pl' ? 'Tak' : 'Yes',
      cancelButtonText: this.langService.currentLang === 'pl' ? 'Anuluj' : 'Cancel',
    }).then((result) => {
      if (result.isConfirmed) {
        this.workScheduleService.removeWorkScheduleEntry({ id: matchingEntries[0].id } as RemovedWorkScheduleEntryRequest).subscribe(() => {
          Swal.fire({
            text: this.langService.currentLang === 'pl' ? `Pomyslnie usunieto wpis do grafiku!` : `Successfully removed work schedule entry!`,
            icon: 'success',
            iconColor: 'green',
            confirmButtonColor: 'green',
            background: '#141414',
            color: 'white',
            confirmButtonText: 'Ok',
          });
          this.loadWorkScheduleEntries();
        });
      }
    });
  }
}
