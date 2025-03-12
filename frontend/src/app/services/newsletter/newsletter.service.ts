import { Injectable } from '@angular/core';
import { LangService } from '../lang/lang.service';
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { catchError, map, Observable, throwError } from 'rxjs';
import { NewNewsletterSubscriberRequest, RegenerateOtpRequest, VerifyNewsletterSubscriptionRequest } from '../../requests/requests';
import { NewNewsletterSubscriberResponse, RegenerateOtpResponse, VerifyNewsletterSubscriptionResponse } from '../../responses/responses';

@Injectable({
  providedIn: 'root'
})
export class NewsletterService {
  private apiUrl = 'http://localhost:8080/api/v1/newsletter';

  constructor(private http: HttpClient, private langService: LangService) {}

  subscribe(request: NewNewsletterSubscriberRequest): Observable<NewNewsletterSubscriberResponse> {

    const headers = new HttpHeaders({
      'Accept-Language': this.langService.currentLang
    });
      
    return this.http.post<NewNewsletterSubscriberResponse>(`${this.apiUrl}/subscribe`, request, { headers, withCredentials: true }).pipe(
      map(response => response),
      catchError(this.handleError)
    )
  }

  verifySubscription(request: VerifyNewsletterSubscriptionRequest): Observable<VerifyNewsletterSubscriptionResponse> {
    
    const headers = new HttpHeaders({
      'Accept-Language': this.langService.currentLang
    });
      
    return this.http.put<VerifyNewsletterSubscriptionResponse>(`${this.apiUrl}/verify-subscription`, request, { headers, withCredentials: true }).pipe(
      map(response => response),
      catchError(this.handleError)
    )
  }

  regenerateOtp(request: RegenerateOtpRequest): Observable<RegenerateOtpResponse> {

    const headers = new HttpHeaders({
      'Accept-Language': this.langService.currentLang
    });
      
    return this.http.put<RegenerateOtpResponse>(`${this.apiUrl}/regenerate-otp`, request, { headers, withCredentials: true }).pipe(
      map(response => response),
      catchError(this.handleError)
    )
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
