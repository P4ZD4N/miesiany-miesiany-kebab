import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { TranslateModule } from '@ngx-translate/core';
import {
  JobOfferGeneralResponse,
  JobOfferManagerResponse,
} from '../../../responses/responses';
import { JobsService } from '../../../services/jobs/jobs.service';
import { LangService } from '../../../services/lang/lang.service';
import { MatExpansionModule } from '@angular/material/expansion';
import { RequirementType } from '../../../enums/requirement-type.enum';
import { AuthenticationService } from '../../../services/authentication/authentication.service';
import {
  JobOfferApplicationRequest,
  NewJobOfferRequest,
  RemovedJobOfferRequest,
  UpdatedJobOfferRequest,
} from '../../../requests/requests';
import { EmploymentType } from '../../../enums/employment-type.enum';
import { Subscription } from 'rxjs';
import {
  JobEmploymentType,
  JobRequirement,
} from '../../../util-types/util-types';
import { HourlyWagePipe } from '../../../pipes/hourly-wage.pipe';
import { AlertService } from '../../../services/alert/alert.service';
import { TranslationHelperService } from '../../../services/translation-helper/translation-helper.service';
import { SortJobOffersByPositionPipe } from '../../../pipes/sort-job-offers-by-position.pipe';

@Component({
  selector: 'app-jobs',
  standalone: true,
  imports: [
    CommonModule,
    TranslateModule,
    ReactiveFormsModule,
    MatExpansionModule,
    FormsModule,
    HourlyWagePipe,
    SortJobOffersByPositionPipe,
  ],
  templateUrl: './jobs.component.html',
  styleUrl: './jobs.component.scss',
})
export class JobsComponent implements OnInit {
  languageChangeSubscription: Subscription;

  jobs: (JobOfferManagerResponse | JobOfferGeneralResponse)[] = [];
  requirementTypes = Object.keys(RequirementType) as RequirementType[];
  employmentTypes = Object.keys(EmploymentType) as EmploymentType[];

  isAdding: boolean = false;
  isEditing: boolean = false;
  showingApplicationsTable: boolean = false;

  currentlyUpdatedJobOfferPositionName: string = '';

  originalJobOffer: JobOfferGeneralResponse | null = null;
  currentlyUpdatedJobOffer: JobOfferGeneralResponse | null = null;
  selectedJobOffer: JobOfferManagerResponse | null = null;

  errorMessages: { [key: string]: string } = {};
  newJobOffer: NewJobOfferRequest = {
    position_name: '',
    description: '',
    hourly_wage: 0,
    job_employment_types: [],
    job_requirements: [],
  };
  jobOfferRequirement: JobRequirement = {
    requirement_type: null,
    description: '',
  };
  jobOfferEmploymentType: JobEmploymentType = {
    employment_type: null,
  };

  constructor(
    private jobsService: JobsService,
    private langService: LangService,
    private authenticationService: AuthenticationService,
    private alertService: AlertService,
    private translationHelper: TranslationHelperService
  ) {
    this.languageChangeSubscription =
      this.langService.languageChanged$.subscribe(() => {
        this.hideErrorMessages();
      });
  }

  ngOnInit(): void {
    if (this.isManager()) {
      this.loadJobsWithManagerDetails();
    } else {
      this.loadJobsWithGeneralDetails();
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
      (data: JobOfferGeneralResponse[]) => (this.jobs = data),
      (error) => console.log('Error loading jobs for other users')
    );
  }

  loadJobsWithManagerDetails(): void {
    this.jobsService.getJobOffersForManager().subscribe(
      (data: JobOfferManagerResponse[]) => (this.jobs = data),
      (error) => console.log('Error loading jobs for manager')
    );
  }

  getMandatoryRequirements(
    jobOffer: JobOfferGeneralResponse
  ): JobRequirement[] {
    return jobOffer.job_requirements.filter(
      (requirement) =>
        requirement.requirement_type === RequirementType.MANDATORY
    );
  }

  getNiceToHaveRequirements(
    jobOffer: JobOfferGeneralResponse
  ): JobRequirement[] {
    return jobOffer.job_requirements.filter(
      (requirement) =>
        requirement.requirement_type === RequirementType.NICE_TO_HAVE
    );
  }

  getAvailableEmploymentTypes(jobOffer: {
    job_employment_types: JobEmploymentType[];
  }): EmploymentType[] {
    return this.employmentTypes.filter(
      (type) =>
        !this.checkIfEmploymentTypeAlreadyExistInJobOffer(
          jobOffer,
          type as EmploymentType
        )
    );
  }

  getEmploymentTypes(jobOffer: JobOfferGeneralResponse): JobEmploymentType[] {
    return jobOffer.job_employment_types;
  }

  getTranslatedPosition(position: string): string {
    return this.translationHelper.getTranslatedPosition(position);
  }

  getCv(cvId: number): void {
    this.jobsService.getCv(cvId).subscribe({
      next: (response: Blob) => {
        const url = window.URL.createObjectURL(response);
        window.open(url);
      },
      error: (error) => console.error('Error previewing CV:', error),
    });
  }

  showAddJobOfferTable(): void {
    this.hideErrorMessages();
    this.isAdding = true;
  }

  showApplications(
    jobOffer: JobOfferGeneralResponse | JobOfferManagerResponse
  ): void {
    const managerOffer = jobOffer as JobOfferManagerResponse;
    this.selectedJobOffer = managerOffer;
    this.showingApplicationsTable = true;
  }

  hideAddJobOfferTable(): void {
    this.hideErrorMessages();
    this.isAdding = false;
    this.newJobOffer = {
      position_name: '',
      description: '',
      hourly_wage: 0,
      job_employment_types: [],
      job_requirements: [],
    };
    this.jobOfferRequirement = { requirement_type: null, description: '' };
    this.jobOfferEmploymentType = { employment_type: null };
  }

  addJobOffer(): void {
    this.jobsService.addNewJobOffer(this.newJobOffer).subscribe({
      next: () => {
        this.alertService.showSuccessfulJobOfferAddAlert(this.newJobOffer);
        this.loadJobsWithManagerDetails();
        this.newJobOffer = {
          position_name: '',
          description: '',
          hourly_wage: 0,
          job_employment_types: [],
          job_requirements: [],
        };
        this.hideAddJobOfferTable();
        this.hideErrorMessages();
      },
      error: (error) => this.handleError(error),
    });
  }

  addRequirementToJobOffer(jobOffer: {
    job_requirements: JobRequirement[];
  }): void {
    if (this.jobOfferRequirement.requirement_type === null) {
      this.langService.currentLang === 'pl'
        ? (this.errorMessages = {
            requirementType: 'Typ wymagania nie moze byc pusty!',
          })
        : (this.errorMessages = {
            requirementType: 'Requirement type cannot be empty!',
          });
      return;
    }

    if (this.jobOfferRequirement.description.trim() === '') {
      this.langService.currentLang === 'pl'
        ? (this.errorMessages = {
            requirementDescription: 'Opis wymagania nie moze byc pusty!',
          })
        : (this.errorMessages = {
            requirementDescription: 'Requirement description cannot be empty!',
          });
      return;
    }

    jobOffer.job_requirements.push(this.jobOfferRequirement);
    this.jobOfferRequirement = { requirement_type: null, description: '' };
    this.hideErrorMessages();
  }

  addEmploymentTypeToJobOffer(jobOffer: {
    job_employment_types: JobEmploymentType[];
  }): void {
    if (this.jobOfferEmploymentType.employment_type === null) {
      this.langService.currentLang === 'pl'
        ? (this.errorMessages = {
            employmentType: 'Typ zatrudnienia nie moze byc pusty!',
          })
        : (this.errorMessages = {
            employmentType: 'Employment type cannot be empty!',
          });
      return;
    }

    jobOffer.job_employment_types.push(this.jobOfferEmploymentType);
    this.jobOfferEmploymentType = { employment_type: null };
    this.hideErrorMessages();
  }

  updateJob(): void {
    let positionTranslated = this.getTranslatedPosition(
      this.currentlyUpdatedJobOfferPositionName
    );

    const updatedJobOffer: UpdatedJobOfferRequest = {
      position_name: this.currentlyUpdatedJobOfferPositionName,
      updated_position_name: this.currentlyUpdatedJobOffer?.position_name,
      updated_description: this.currentlyUpdatedJobOffer?.description,
      updated_hourly_wage: this.currentlyUpdatedJobOffer?.hourly_wage,
      updated_employment_types:
        this.currentlyUpdatedJobOffer?.job_employment_types,
      updated_requirements: this.currentlyUpdatedJobOffer?.job_requirements,
      is_active: this.currentlyUpdatedJobOffer?.is_active,
    };

    this.jobsService.updateJobOffer(updatedJobOffer).subscribe({
      next: () => {
        this.alertService.showSuccessfulJobOfferUpdateAlert(positionTranslated);
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
    let positionNameTranslated = this.getTranslatedPosition(
      jobOffer.position_name
    );

    this.alertService
      .showRemoveJobOfferAlert(positionNameTranslated)
      .then((confirmed) => {
        if (!confirmed) return;

        this.jobsService
          .removeJobOffer({
            position_name: jobOffer.position_name,
          } as RemovedJobOfferRequest)
          .subscribe(() => {
            this.alertService.showSuccessfulJobOfferRemoveAlert(
              positionNameTranslated
            );
            this.loadJobsWithManagerDetails();
          });
      });
  }

  removeJobApplication(
    positionName: string | undefined,
    applicationId: number
  ): void {
    let positionTranslated = this.getTranslatedPosition(positionName || '');

    this.alertService.showRemoveJobApplicationAlert().then((result) => {
      if (!result) return;

      const jobOffer = this.selectedJobOffer;

      if (jobOffer) {
        jobOffer.job_applications = jobOffer.job_applications.filter(
          (applicantion) => applicantion.id !== applicationId
        );
      }

      this.jobsService
        .removeApplication(positionName || '', applicationId)
        .subscribe({
          next: () => {
            this.alertService.showSuccessfulJobApplicationRemoveAlert(
              positionTranslated
            );
            this.hideErrorMessages();
            this.loadJobsWithManagerDetails();
          },
          error: (error) => this.handleError(error),
        });
    });
  }

  removeRequirementFromJobOffer(
    jobOffer: { job_requirements: JobRequirement[] },
    requirement: JobRequirement
  ): void {
    const index = jobOffer.job_requirements.findIndex(
      (req) =>
        req.requirement_type === requirement.requirement_type &&
        req.description === requirement.description
    );

    if (index !== -1) jobOffer.job_requirements.splice(index, 1);
  }

  removeEmploymentTypeFromJobOffer(
    jobOffer: { job_employment_types: JobEmploymentType[] },
    employmentType: JobEmploymentType
  ): void {
    const index = jobOffer.job_employment_types.findIndex(
      (empType) => empType.employment_type === employmentType.employment_type
    );

    if (index !== -1) jobOffer.job_employment_types.splice(index, 1);
  }

  checkIfEmploymentTypeAlreadyExistInJobOffer(
    jobOffer: { job_employment_types: JobEmploymentType[] },
    employmentType: EmploymentType
  ): boolean {
    return jobOffer.job_employment_types.some(
      (empType) => empType.employment_type === employmentType
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

  startApplying(jobOffer: JobOfferGeneralResponse): void {
    this.alertService.showApplyToJobOfferAlert(jobOffer).then((result) => {
      if (!result) return;

      const jobApplication: JobOfferApplicationRequest = {
        position_name: result.positionName,
        applicant_first_name: result.firstName,
        applicant_last_name: result.lastName,
        applicant_email: result.email,
        applicant_telephone: result.telephone,
        additional_message: result.additionalMessage,
        is_student: result.isStudent,
      };

      this.jobsService.addJobApplication(jobApplication).subscribe({
        next: (response) => {
          this.jobsService
            .addCv(response.application_id, result.cvFile)
            .subscribe({});

          this.alertService.showSuccessfulJobOfferApplyAlert(jobOffer);
          this.hideErrorMessages();
          this.loadJobsWithGeneralDetails();
        },
        error: (error) => {
          this.handleError(error);
          this.alertService.showJobOfferApplyErrorAlert(this.errorMessages);
        },
      });
    });

    document
      .querySelector('#cv')
      ?.addEventListener('change', function (event: any) {
        const fileName = event.target.files[0]
          ? event.target.files[0].name
          : '...';
        document.querySelector('#file-name')!.textContent = fileName;
      });
  }

  hideErrorMessages(): void {
    this.errorMessages = {};
  }

  handleError(error: any) {
    if (error.errorMessages) this.errorMessages = error.errorMessages;
    else this.errorMessages = { general: 'An unexpected error occurred' };
  }
}
