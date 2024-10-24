import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { TranslateModule } from '@ngx-translate/core';
import { JobOfferGeneralResponse, JobRequirement } from '../../../responses/responses';
import { JobsService } from '../../../services/jobs/jobs.service';
import { LangService } from '../../../services/lang/lang.service';
import { MatExpansionModule } from '@angular/material/expansion';
import { RequirementType } from '../../../enums/requirement-type.enum';
import { AuthenticationService } from '../../../services/authentication/authentication.service';

@Component({
  selector: 'app-jobs',
  standalone: true,
  imports: [CommonModule, TranslateModule,ReactiveFormsModule, MatExpansionModule],
  templateUrl: './jobs.component.html',
  styleUrl: './jobs.component.scss'
})
export class JobsComponent implements OnInit {

  jobsWithGeneralDetails: JobOfferGeneralResponse[] = [];
  requirementTypes = RequirementType;

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
}
