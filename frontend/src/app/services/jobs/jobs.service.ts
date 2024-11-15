import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, map, Observable, throwError } from 'rxjs';
import { JobOfferApplicationResponse, JobOfferGeneralResponse, NewCvResponse, NewJobOfferResponse, RemovedJobOfferResponse, UpdatedJobOfferResponse } from '../../responses/responses';
import { JobOfferApplicationRequest, NewJobOfferRequest, RemovedJobOfferRequest, UpdatedJobOfferRequest } from '../../requests/requests';
import { LangService } from '../lang/lang.service';

@Injectable({
  providedIn: 'root'
})
export class JobsService {

  private apiUrl = 'http://localhost:8080/api/v1/jobs'

  constructor(private http: HttpClient, private langService: LangService) {}

  getJobOffersForOtherUsers(): Observable<JobOfferGeneralResponse[]> {

    return this.http.get<JobOfferGeneralResponse[]>(`${this.apiUrl}/job-offers/general`, { withCredentials: true })
  }

  addNewJobOffer(newJobOffer: NewJobOfferRequest): Observable<NewJobOfferResponse> {

    const headers = new HttpHeaders({
      'Accept-Language': this.langService.currentLang
    });

    return this.http.post<NewJobOfferResponse>(`${this.apiUrl}/add-job-offer`, newJobOffer, { headers, withCredentials: true }).pipe(
      map(response => response),
      catchError(this.handleError)
    )
  }

  updateJobOffer(jobOffer: UpdatedJobOfferRequest): Observable<UpdatedJobOfferResponse> {

    const headers = new HttpHeaders({
      'Accept-Language': this.langService.currentLang
    });

    return this.http.put<NewJobOfferResponse>(`${this.apiUrl}/update-job-offer`, jobOffer, { headers, withCredentials: true }).pipe(
      map(response => response),
      catchError(this.handleError)
    );
  }

  removeJobOffer(jobOffer: RemovedJobOfferRequest): Observable<RemovedJobOfferResponse> {

    const requestBody = { position_name: jobOffer.position_name };

    return this.http.delete<RemovedJobOfferResponse>(`${this.apiUrl}/remove-job-offer`, { body: requestBody, withCredentials: true }).pipe(
      map(response => response),
      catchError(this.handleError)
    )
  }

  addJobApplication(jobApplication : JobOfferApplicationRequest): Observable<JobOfferApplicationResponse> {

    const headers = new HttpHeaders({
      'Accept-Language': this.langService.currentLang
    });

    return this.http.post<JobOfferApplicationResponse>(`${this.apiUrl}/add-job-offer-application`, jobApplication, { headers, withCredentials: true }).pipe(
      map(response => response),
      catchError(this.handleError)
    );
  }
  
  addCv(applicationId: number, cvFile: File): Observable<NewCvResponse> {
    
    const formData = new FormData();
    formData.append('applicationId', applicationId.toString());
    formData.append('cv', cvFile);

    const headers = new HttpHeaders({
      'Accept-Language': this.langService.currentLang
    });

    return this.http.post<NewCvResponse>(`${this.apiUrl}/add-cv`, formData, { headers, withCredentials: true }).pipe(
      map(response => response), 
      catchError(this.handleError)
    );
  }

  handleError(error: HttpErrorResponse) {

    let errorMessages: { [key: string]: string } = {};

    if (error.error && typeof error.error === 'object') {
      errorMessages = error.error;
    } else {
      console.error(`Backend returned code ${error.status}, body was: ${error.error}`);
    }

    return throwError(() => ({ errorMessages }));
  }
}
