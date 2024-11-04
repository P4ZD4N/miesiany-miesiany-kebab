import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { TranslateModule } from '@ngx-translate/core';
import { JobEmploymentType, JobOfferGeneralResponse, JobRequirement } from '../../../responses/responses';
import { JobsService } from '../../../services/jobs/jobs.service';
import { LangService } from '../../../services/lang/lang.service';
import { MatExpansionModule } from '@angular/material/expansion';
import { RequirementType } from '../../../enums/requirement-type.enum';
import { AuthenticationService } from '../../../services/authentication/authentication.service';
import { NewJobOfferRequest } from '../../../requests/requests';
import { EmploymentType } from '../../../enums/employment-type.enum';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-jobs',
  standalone: true,
  imports: [CommonModule, TranslateModule, ReactiveFormsModule, MatExpansionModule, FormsModule],
  templateUrl: './jobs.component.html',
  styleUrl: './jobs.component.scss'
})
export class JobsComponent implements OnInit {

  errorMessages: { [key: string]: string } = {};
  jobsWithGeneralDetails: JobOfferGeneralResponse[] = [];
  requirementTypes = Object.keys(RequirementType);
  employmentTypes = Object.keys(EmploymentType) as EmploymentType[];

  isAdding = false;
  isEditing = false;

  newJobOffer: NewJobOfferRequest = {
    position_name: '',
    description: '',
    hourly_wage: 0,
    job_employment_types: [],
    job_requirements: []
  }

  newJobOfferRequirement: JobRequirement = {
    requirement_type: null,
    description: ''
  }

  newJobOfferEmploymentType: JobEmploymentType = {
    employment_type: null
  }

  constructor(private jobsService: JobsService, private langService: LangService, private authenticationService: AuthenticationService) {}

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
    this.newJobOfferRequirement = { requirement_type: null, description: '' };
    this.newJobOfferEmploymentType = { employment_type: null };
  }

  addJobOffer(): void {
    console.log(this.newJobOffer.job_employment_types);
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

  addRequirementToNewJobOffer(): void {
    if (this.newJobOfferRequirement.requirement_type !== null && this.newJobOfferRequirement.description !== '') {
     this.newJobOffer.job_requirements.push(this.newJobOfferRequirement);
    }
    this.newJobOfferRequirement = { requirement_type: null, description: '' };
  }

  addEmploymentTypeToNewJobOffer(): void {
    if (this.newJobOfferEmploymentType.employment_type !== null) {
      this.newJobOffer.job_employment_types.push(this.newJobOfferEmploymentType);
    }
    this.newJobOfferEmploymentType = { employment_type: null };
  }

  removeRequirementFromNewJobOffer(requirement: JobRequirement): void {
    this.newJobOffer.job_requirements = this.newJobOffer.job_requirements.filter(
      (req) => req.requirement_type !== requirement.requirement_type || req.description !== requirement.description
    );
  }

  removeEmploymentTypeFromNewJobOffer(employmentType: JobEmploymentType): void {
    this.newJobOffer.job_employment_types = this.newJobOffer.job_employment_types.filter(
      (empType) => empType.employment_type !== employmentType.employment_type
    );
  }

  checkIfEmploymentTypeAlreadyExistInNewJobOffer(employmentType: EmploymentType): boolean {
    return this.newJobOffer.job_employment_types.some(
      (empType) => empType.employment_type === employmentType
    );
  }

  getAvailableEmploymentTypes(): EmploymentType[] {
    return this.employmentTypes.filter(
      (type) => !this.checkIfEmploymentTypeAlreadyExistInNewJobOffer(type as EmploymentType)
    );
  }

  hideErrorMessages(): void {
    this.errorMessages = {};
  }

  handleError(error: any) {
    if (error.errorMessages) {
      this.errorMessages = error.errorMessages;
      console.log(this.errorMessages);
    } else {
      this.errorMessages = { general: 'An unexpected error occurred' };
    }
  }
}
