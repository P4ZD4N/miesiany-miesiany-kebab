import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { JobOfferGeneralResponse } from '../../responses/responses';

@Injectable({
  providedIn: 'root'
})
export class JobsService {

  private apiUrl = 'http://localhost:8080/api/v1/jobs'

  constructor(private http: HttpClient) {}

  getJobOffersForOtherUsers(): Observable<JobOfferGeneralResponse[]> {

    return this.http.get<JobOfferGeneralResponse[]>(`${this.apiUrl}/job-offers/general`, { withCredentials: true })
  }
}
