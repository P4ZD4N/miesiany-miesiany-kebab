import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { JobEmploymentType, JobOfferGeneralResponse, JobOfferManagerResponse, JobRequirement } from '../../../responses/responses';
import { JobsService } from '../../../services/jobs/jobs.service';
import { LangService } from '../../../services/lang/lang.service';
import { MatExpansionModule } from '@angular/material/expansion';
import { RequirementType } from '../../../enums/requirement-type.enum';
import { AuthenticationService } from '../../../services/authentication/authentication.service';
import { JobOfferApplicationRequest, NewJobOfferRequest, RemovedJobOfferRequest, UpdatedJobOfferRequest } from '../../../requests/requests';
import { EmploymentType } from '../../../enums/employment-type.enum';
import Swal from 'sweetalert2';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-jobs',
  standalone: true,
  imports: [CommonModule, TranslateModule, ReactiveFormsModule, MatExpansionModule, FormsModule],
  templateUrl: './jobs.component.html',
  styleUrl: './jobs.component.scss'
})
export class JobsComponent implements OnInit {

  languageChangeSubscription: Subscription;

  errorMessages: { [key: string]: string } = {};
  jobs: (JobOfferManagerResponse | JobOfferGeneralResponse)[] = [];
  requirementTypes = Object.keys(RequirementType);
  employmentTypes = Object.keys(EmploymentType) as EmploymentType[];

  isAdding = false;
  isEditing = false;
  showingApplicationsTable: boolean = false;

  currentlyUpdatedJobOfferPositionName: string = '';
  originalJobOffer: JobOfferGeneralResponse | null = null;
  currentlyUpdatedJobOffer: JobOfferGeneralResponse | null = null;
  selectedJobOffer: JobOfferManagerResponse | null = null;

  newJobOffer: NewJobOfferRequest = {
    position_name: '',
    description: '',
    hourly_wage: 0,
    job_employment_types: [],
    job_requirements: []
  }

  jobOfferRequirement: JobRequirement = {
    requirement_type: null,
    description: ''
  }

  jobOfferEmploymentType: JobEmploymentType = {
    employment_type: null
  }

  constructor(
    private jobsService: JobsService, 
    private langService: LangService, 
    private authenticationService: AuthenticationService,
    private translate: TranslateService
  ) {
    this.languageChangeSubscription = this.langService.languageChanged$.subscribe(() => {
      this.hideErrorMessages();
    });
  }

  ngOnInit(): void {
    if (this.isManager()) {this.loadJobsWithManagerDetails();
    }else {this.loadJobsWithGeneralDetails();
    }
  }

  isManager(): boolean {
    return this.authenticationService.isManager();
  }

  isEmployee(): boolean {
    return this.authenticationService.isEmployee();
  }

  loadJobsWithGeneralDetails(): void {
    this.jobsService.getJobOffersForOtherUsers().subscribe(
      (data: JobOfferGeneralResponse[]) => {
        this.jobs = data;
      },
      (error) => {
        console.log('Error loading jobs for other users');
      }
    )
  }

  loadJobsWithManagerDetails(): void {
    this.jobsService.getJobOffersForManager().subscribe(
      (data: JobOfferManagerResponse[]) => {
        this.jobs = data;
      },
      (error) => {
        console.log('Error loading jobs for manager');
      }
    )
  }

  getMandatoryRequirements(jobOffer: JobOfferGeneralResponse): JobRequirement[] {
    return jobOffer.job_requirements.filter(requirement => requirement.requirement_type === RequirementType.MANDATORY);
  }

  getNiceToHaveRequirements(jobOffer: JobOfferGeneralResponse): JobRequirement[] {
    return jobOffer.job_requirements.filter(requirement => requirement.requirement_type === RequirementType.NICE_TO_HAVE);
  }

  getEmploymentTypes(jobOffer: JobOfferGeneralResponse): JobEmploymentType[] {
    return jobOffer.job_employment_types;
  }

  formatHourlyWage(hourlyWage: number | unknown): string {
    if (typeof hourlyWage === 'number') {
      return hourlyWage.toFixed(2);
    } else {
      return 'Invalid hourly wage';
    }
  }

  showAddJobOfferTable(): void {
    this.hideErrorMessages();
    this.isAdding = true;
  }

  hideAddJobOfferTable(): void {
    this.hideErrorMessages();
    this.isAdding = false;
    this.newJobOffer = { position_name: '',
      description: '',
      hourly_wage: 0,
      job_employment_types: [],
      job_requirements: []
    }
    this.jobOfferRequirement = { requirement_type: null, description: '' };
    this.jobOfferEmploymentType = { employment_type: null };
  }

  addJobOffer(): void {
    this.jobsService.addNewJobOffer(this.newJobOffer).subscribe({
      next: (response) => {
        Swal.fire({
          text: this.langService.currentLang === 'pl' ? `Pomyslnie dodano oferte pracy na stanowisko '${this.newJobOffer.position_name}'!` : `Successfully added job offer for position '${this.newJobOffer.position_name}'!`,
          icon: 'success',
          iconColor: 'green',
          confirmButtonColor: 'green',
          background: 'black',
          color: 'white',
          confirmButtonText: 'Ok',
        });

        this.loadJobsWithManagerDetails();
        this.newJobOffer = { position_name: '',
          description: '',
          hourly_wage: 0,
          job_employment_types: [],
          job_requirements: []
        }
        this.hideAddJobOfferTable();
        this.hideErrorMessages();
      },
      error: (error) => {
        this.handleError(error);
      },
    });
  }

  updateJob(): void {
    let positionTranslated = this.translate.instant('jobs.offers.' + this.currentlyUpdatedJobOfferPositionName);

    if (!this.isPositionTranslationAvailable(this.currentlyUpdatedJobOfferPositionName)) positionTranslated = this.currentlyUpdatedJobOfferPositionName
    
    const updatedJobOffer: UpdatedJobOfferRequest = {
      position_name: this.currentlyUpdatedJobOfferPositionName,
      updated_position_name: this.currentlyUpdatedJobOffer?.position_name,
      updated_description: this.currentlyUpdatedJobOffer?.description,
      updated_hourly_wage: this.currentlyUpdatedJobOffer?.hourly_wage,
      updated_employment_types: this.currentlyUpdatedJobOffer?.job_employment_types,
      updated_requirements: this.currentlyUpdatedJobOffer?.job_requirements,
      is_active: this.currentlyUpdatedJobOffer?.is_active
    }

    this.jobsService.updateJobOffer(updatedJobOffer).subscribe({
      next: (response) => {
        Swal.fire({
          text: this.langService.currentLang === 'pl' ? `Pomyslnie zaktualizowano oferte pracy na pozycji '${positionTranslated}'!` : `Successfully updated job offer on '${positionTranslated}' position!`,
          icon: 'success',
          iconColor: 'green',
          confirmButtonColor: 'green',
          background: 'black',
          color: 'white',
          confirmButtonText: 'Ok',
        });

        this.isEditing = false;
        this.hideErrorMessages();
        this.loadJobsWithManagerDetails();
      },
      error: (error) => {
        this.handleError(error);
      },
    });
  }

  removeJobOffer(jobOffer: JobOfferGeneralResponse): void {
    let positionNameTranslated = this.translate.instant('jobs.offers.' + jobOffer.position_name);

    if (!this.isPositionTranslationAvailable(jobOffer.position_name)) {
      positionNameTranslated = jobOffer.position_name;
    }

    const confirmationMessage =
      this.langService.currentLang === 'pl'
        ? `Czy na pewno chcesz usunac oferte pracy na stanowisku '${positionNameTranslated}'?`
        : `Are you sure you want to remove job offer on '${positionNameTranslated}' position?`;

    Swal.fire({
      title: this.langService.currentLang === 'pl' ? 'Potwierdzenie' : 'Confirmation',
      text: confirmationMessage,
      icon: 'warning',
      iconColor: 'red',
      showCancelButton: true,
      confirmButtonColor: '#0077ff',
      cancelButtonColor: 'red',
      background: 'black',
      color: 'white',
      confirmButtonText: this.langService.currentLang === 'pl' ? 'Tak' : 'Yes',
      cancelButtonText: this.langService.currentLang === 'pl' ? 'Anuluj' : 'Cancel',
    }).then((result) => {
      if (result.isConfirmed) {
        this.jobsService.removeJobOffer({ position_name: jobOffer.position_name } as RemovedJobOfferRequest).subscribe(() => {
          Swal.fire({
            text: this.langService.currentLang === 'pl' ? `Pomyslnie usunieto oferte pracy na stanowisku '${positionNameTranslated}'!` : `Successfully removed job offer on '${positionNameTranslated}' position!`,
            icon: 'success',
            iconColor: 'green',
            confirmButtonColor: 'green',
            background: 'black',
            color: 'white',
            confirmButtonText: 'Ok',
          });
          this.loadJobsWithManagerDetails();
        });
      }
    });
  }

  addRequirementToJobOffer(jobOffer: { job_requirements: JobRequirement[] }): void {
    if (this.jobOfferRequirement.requirement_type === null) {
      this.langService.currentLang === 'pl' ? this.errorMessages = { requirementType: 'Typ wymagania nie moze byc pusty!' } : this.errorMessages = { requirementType: 'Requirement type cannot be empty!' };
      return;
    } 
    
    if (this.jobOfferRequirement.description.trim() === '') {
      this.langService.currentLang === 'pl' ? this.errorMessages = { requirementDescription: 'Opis wymagania nie moze byc pusty!' } : this.errorMessages = { requirementDescription: 'Requirement description cannot be empty!' };
      return;
    }

    jobOffer.job_requirements.push(this.jobOfferRequirement);
    this.jobOfferRequirement = { requirement_type: null, description: '' };
    this.hideErrorMessages();
  }

  addEmploymentTypeToJobOffer(jobOffer: { job_employment_types: JobEmploymentType[] }): void {
    if (this.jobOfferEmploymentType.employment_type === null) {
      this.langService.currentLang === 'pl' ? this.errorMessages = { employmentType: "Typ zatrudnienia nie moze byc pusty!" } : this.errorMessages = { employmentType: "Employment type cannot be empty!" }
      return;
    }
    jobOffer.job_employment_types.push(this.jobOfferEmploymentType);
    this.jobOfferEmploymentType = { employment_type: null };
    this.hideErrorMessages();
  }

  removeRequirementFromJobOffer(jobOffer: { job_requirements: JobRequirement[] }, requirement: JobRequirement): void {
    const index = jobOffer.job_requirements.findIndex(
      (req) => req.requirement_type === requirement.requirement_type && req.description === requirement.description
    );
  
    if (index !== -1) jobOffer.job_requirements.splice(index, 1);
  }

  removeEmploymentTypeFromJobOffer(jobOffer: { job_employment_types: JobEmploymentType[] }, employmentType: JobEmploymentType): void {
    const index = jobOffer.job_employment_types.findIndex(
      (empType) => empType.employment_type === employmentType.employment_type
    );

    if (index !== -1) jobOffer.job_employment_types.splice(index, 1);
  }

  checkIfEmploymentTypeAlreadyExistInJobOffer(jobOffer: { job_employment_types: JobEmploymentType[] }, employmentType: EmploymentType): boolean {
    return jobOffer.job_employment_types.some(
      (empType) => empType.employment_type === employmentType
    );
  }

  getAvailableEmploymentTypes(jobOffer: { job_employment_types: JobEmploymentType[] }): EmploymentType[] {
    return this.employmentTypes.filter(
      (type) => !this.checkIfEmploymentTypeAlreadyExistInJobOffer(jobOffer, type as EmploymentType)
    );
  }

  startUpdating(jobOffer: JobOfferGeneralResponse): void {
    this.isEditing = true;
    this.currentlyUpdatedJobOfferPositionName = jobOffer.position_name;
    this.currentlyUpdatedJobOffer = { ...jobOffer };
    this.originalJobOffer = { ...jobOffer };
  }

  stopUpdating(): void {
    this.isEditing = false;
    this.currentlyUpdatedJobOfferPositionName = '';
    this.currentlyUpdatedJobOffer = null;
    this.hideErrorMessages();
    this.loadJobsWithManagerDetails();
  }

  hideErrorMessages(): void {
    this.errorMessages = {};
  }

  isPositionTranslationAvailable(position: string): boolean {
    const translatedName = this.translate.instant('jobs.offers.' + position);
    return translatedName !== 'jobs.offers.' + position;
  }

  handleError(error: any) {
    if (error.errorMessages) {
      this.errorMessages = error.errorMessages;
      console.log(this.errorMessages);
    } else this.errorMessages = { general: 'An unexpected error occurred' };
  }

  sortJobOffersByPosition(jobOffers: JobOfferGeneralResponse[]): JobOfferGeneralResponse[] {
    return jobOffers.sort((a, b) => {
      
      let firstPositionNameTranslated = this.translate.instant('jobs.offers.' + a.position_name);
      let secondPositionNameTranslated = this.translate.instant('jobs.offers.' + b.position_name);

      if (!this.isPositionTranslationAvailable(a.position_name)) {
        firstPositionNameTranslated = a.position_name;
      }
  
      if (!this.isPositionTranslationAvailable(b.position_name)) {
        secondPositionNameTranslated = b.position_name; 
      }
  
      return firstPositionNameTranslated.localeCompare(secondPositionNameTranslated);
    });
  }

  startApplying(jobOffer: JobOfferGeneralResponse): void {

    let positionNameTranslated = this.translate.instant('jobs.offers.' + jobOffer.position_name);

    if (!this.isPositionTranslationAvailable(jobOffer.position_name)) {
      positionNameTranslated = jobOffer.position_name;
    }

    const title = this.langService.currentLang === 'pl' ? 'Aplikuj na stanowisko ' + positionNameTranslated : 'Apply for ' + jobOffer.position_name + ' position';
    const confirmButtonText = this.langService.currentLang === 'pl' ? 'Aplikuj' : 'Apply';
    const cancelButtonText = this.langService.currentLang === 'pl' ? 'Anuluj' : 'Cancel';
    const areYouStudent = this.langService.currentLang === 'pl' ? "Czy jestes studentem?" : "Are you a student?";
    const attachCv = this.langService.currentLang === 'pl' ? "Zalacz CV!" : "Attach CV!";
    const chooseFile = this.langService.currentLang === 'pl' ? "Wybierz plik" : "Choose file";
    const noFileChosen = this.langService.currentLang === 'pl' ? "Brak pliku" : "No file chosen";
  
    const firstNamePlaceholder = this.langService.currentLang === 'pl' ? 'Imie' : 'First Name';
    const lastNamePlaceholder = this.langService.currentLang === 'pl' ? 'Nazwisko' : 'Last Name';
    const emailPlaceholder = this.langService.currentLang === 'pl' ? 'Email' : 'Email';
    const telephonePlaceholder = this.langService.currentLang === 'pl' ? 'Telefon (9 cyfr)' : 'Phone (9 digits)';
    const additionalMessagePlaceholder = this.langService.currentLang === 'pl' ? 'Wiadomosc dodatkowa' : 'Additional Message';
  
    Swal.fire({
      title: `<span style="color: red;">${title}</span>`,
      background: '#141414',
      color: 'white',
      html: `
        <div>
          <input type="text" id="firstName" maxlength=25 class="swal2-input" placeholder="${firstNamePlaceholder}">
        </div>
        <div>
          <input type="text" id="lastName" maxlength=25 class="swal2-input" placeholder="${lastNamePlaceholder}">
        </div>
        <div>
          <input type="email" id="email" maxlength=30 class="swal2-input" placeholder="${emailPlaceholder}">
        </div>
        <div>
          <input type="text" id="telephone" maxlength=9 class="swal2-input" placeholder="${telephonePlaceholder}">
        </div>
        <div>
          <textarea id="additionalMessage" maxlength=200 class="swal2-textarea" placeholder="${additionalMessagePlaceholder}"></textarea>
        </div>
        <div style="margin-top: 10px; display: flex; justify-content: center;">
          <input type="checkbox" id="isStudent" style="margin-right: 5px; accent-color: red;">
          <label for="isStudent">${areYouStudent}</label>
        </div>
        <div style="margin-top: 10px; display: flex; justify-content: center; flex-direction: column;">
          <label for="cv" class="swal2-label">${attachCv}</label>
          <input type="file" id="cv" class="swal2-input" accept=".pdf, .doc, .docx" style="display: none;" />
          <button 
            type="button" 
            style="margin-top: 10px; background-color: red; color: white; border: none; padding: 10px 20px; border-radius: 5px; cursor: pointer;" 
            onclick="document.getElementById('cv').click();"
          >
            ${chooseFile}
          </button>
          <div id="file-name" style="margin-top: 10px; color: white;">${noFileChosen}</div>
        </div>
      `,
      confirmButtonText: confirmButtonText,
      confirmButtonColor: '#198754',
      cancelButtonText: cancelButtonText,
      cancelButtonColor: 'red',
      showCancelButton: true,
      focusConfirm: false,
      preConfirm: () => {
        const firstName = (document.getElementById('firstName') as HTMLInputElement).value;
        const lastName = (document.getElementById('lastName') as HTMLInputElement).value;
        const email = (document.getElementById('email') as HTMLInputElement).value;
        const telephone = (document.getElementById('telephone') as HTMLInputElement).value;
        const additionalMessage = (document.getElementById('additionalMessage') as HTMLTextAreaElement).value;
        const isStudent = (document.getElementById('isStudent') as HTMLInputElement).checked;
        const cvFile = (document.getElementById('cv') as HTMLInputElement).files?.[0];

        if (!firstName || !lastName || !email || !telephone) {
          Swal.showValidationMessage(
            this.langService.currentLang === 'pl' ? 'Wszystkie pola z wyjatkiem dodatkowej wiadomosci sa wymagane' : 'All fields except additional message are required'
          );
          return null;
        }

        const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
        if (!emailRegex.test(email)) {
          Swal.showValidationMessage(
            this.langService.currentLang === 'pl' ? 'Niepoprawny email!' : 'Invalid email!'
          );
          return null;
        }

        const phoneRegex = /^[0-9]{9}$/;
        if (!phoneRegex.test(telephone)) {
          Swal.showValidationMessage(
            this.langService.currentLang === 'pl' ? 'Niepoprawny numer telefonu!' : 'Invalid phone number!'
          );
          return null;
        }

        if (cvFile == null) {
          Swal.showValidationMessage(
            this.langService.currentLang === 'pl' ? 'Brak zalaczonego CV!' : 'No CV attached!'
          );
          return null;
        }
    
        return { positionName: jobOffer.position_name, firstName, lastName, email, telephone, additionalMessage, isStudent, cvFile };
      }
    }).then((result) => {
      if (result.isConfirmed && result.value) {
        const jobApplication: JobOfferApplicationRequest = {
          position_name: result.value.positionName,
          applicant_first_name: result.value.firstName,
          applicant_last_name: result.value.lastName, 
          applicant_email: result.value.email,
          applicant_telephone: result.value.telephone,
          additional_message: result.value.additionalMessage,
          is_student: result.value.isStudent
        }

        this.jobsService.addJobApplication(jobApplication).subscribe({
          next: (response) => {
            this.jobsService.addCv(response.application_id, result.value.cvFile).subscribe({});

            Swal.fire({
              text: this.langService.currentLang === 'pl' ? `Pomyslnie zaaplikowano na pozycje '${positionNameTranslated}'!` : `Successfully applied for a '${positionNameTranslated}' position!`,
              icon: 'success',
              iconColor: 'green',
              confirmButtonColor: 'green',
              background: 'black',
              color: 'white',
              confirmButtonText: 'Ok',
            });

            this.hideErrorMessages();
            this.loadJobsWithGeneralDetails();
          },
          error: (error) => {
            this.handleError(error);
          },
        });
      }
    });

    document.querySelector('#cv')?.addEventListener('change', function (event: any) {
      const fileName = event.target.files[0] ? event.target.files[0].name : '...';
      document.querySelector('#file-name')!.textContent = fileName;
    });
  }

  showApplications(jobOffer: JobOfferGeneralResponse | JobOfferManagerResponse): void {
    const managerOffer = jobOffer as JobOfferManagerResponse;
    this.selectedJobOffer = managerOffer;
    this.showingApplicationsTable = true;
  }

  removeJobApplication(positionName: string | undefined, applicationId: number): void {
    let positionTranslated = this.translate.instant('jobs.offers.' + positionName);

    if (!this.isPositionTranslationAvailable(positionName || '')) positionTranslated = positionName;
    
    const confirmationMessage =
      this.langService.currentLang === 'pl'
        ? `Czy na pewno chcesz usunac aplikacje?`
        : `Are you sure you want to remove job application?`;

    Swal.fire({
      title: this.langService.currentLang === 'pl' ? 'Potwierdzenie' : 'Confirmation',
      text: confirmationMessage,
      icon: 'warning',
      iconColor: 'red',
      showCancelButton: true,
      confirmButtonColor: '#0077ff',
      cancelButtonColor: 'red',
      background: 'black',
      color: 'white',
      confirmButtonText: this.langService.currentLang === 'pl' ? 'Tak' : 'Yes',
      cancelButtonText: this.langService.currentLang === 'pl' ? 'Anuluj' : 'Cancel',
    }).then((result) => {
      if (result.isConfirmed) {
        const jobOffer = this.selectedJobOffer;
        if (jobOffer) {
          jobOffer.job_applications = jobOffer.job_applications.filter(applicantion => applicantion.id !== applicationId);
        }
        this.jobsService.removeApplication(positionName || '', applicationId).subscribe({
          next: (response) => {
            Swal.fire({
              text: this.langService.currentLang === 'pl' ? `Pomyslnie usunieto aplikacje na oferte pracy na pozycji '${positionTranslated}'!` : `Successfully removed job application to job offer on '${positionTranslated}' position!`,
              icon: 'success',
              iconColor: 'green',
              confirmButtonColor: 'green',
              background: 'black',
              color: 'white',
              confirmButtonText: 'Ok',
            });

            this.hideErrorMessages();
            this.loadJobsWithManagerDetails();
          },
          error: (error) => {
            this.handleError(error);
          },
        });
      }
    });
  }

  getCv(cvId: number): void {
    this.jobsService.getCv(cvId).subscribe({
      next: (response: Blob) => {
        const url = window.URL.createObjectURL(response);
        window.open(url);
      },
      error: (error) => {
        console.error('Error previewing CV:', error);
      },
    });
  }
}
