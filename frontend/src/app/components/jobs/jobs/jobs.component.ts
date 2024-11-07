import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { JobEmploymentType, JobOfferGeneralResponse, JobRequirement } from '../../../responses/responses';
import { JobsService } from '../../../services/jobs/jobs.service';
import { LangService } from '../../../services/lang/lang.service';
import { MatExpansionModule } from '@angular/material/expansion';
import { RequirementType } from '../../../enums/requirement-type.enum';
import { AuthenticationService } from '../../../services/authentication/authentication.service';
import { NewJobOfferRequest, UpdatedJobOfferRequest } from '../../../requests/requests';
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
  jobsWithGeneralDetails: JobOfferGeneralResponse[] = [];
  requirementTypes = Object.keys(RequirementType);
  employmentTypes = Object.keys(EmploymentType) as EmploymentType[];

  isAdding = false;
  isEditing = false;

  currentlyUpdatedJobOfferPositionName: string = '';
  originalJobOffer: JobOfferGeneralResponse | null = null;
  currentlyUpdatedJobOffer: JobOfferGeneralResponse | null = null;

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
    this.loadJobsWithGeneralDetails();
  }

  isManager(): boolean {
    return this.authenticationService.isManager();
  }


  loadJobsWithGeneralDetails(): void {
    this.jobsService.getJobOffersForOtherUsers().subscribe(
      (data: JobOfferGeneralResponse[]) => {
        this.jobsWithGeneralDetails = data;
      },
      (error) => {
        console.log('Error loading jobs for other users');
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

        this.loadJobsWithGeneralDetails();
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
    console.log(this.currentlyUpdatedJobOffer?.position_name);
    console.log(this.currentlyUpdatedJobOfferPositionName);

    console.log(this.currentlyUpdatedJobOffer?.job_requirements);

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
        this.loadJobsWithGeneralDetails();
      },
      error: (error) => {
        this.handleError(error);
      },
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
    this.loadJobsWithGeneralDetails();
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
}
