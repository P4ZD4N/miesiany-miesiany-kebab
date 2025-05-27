import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { LangService } from '../lang/lang.service';
import { DiscountCodeResponse, NewDiscountCodeResponse, RemovedDiscountCodeResponse, UpdatedAddonPromotionResponse } from '../../responses/responses';
import { catchError, map, Observable, throwError } from 'rxjs';
import { NewDiscountCodeRequest, RemovedDiscountCodeRequest, UpdatedDiscountCodeRequest } from '../../requests/requests';

@Injectable({
  providedIn: 'root'
})
export class DiscountCodesService {

  private apiUrl = 'http://localhost:8080/api/v1/discount-codes';

  constructor(private http: HttpClient, private langService: LangService) { }

  getDiscountCode(code: string): Observable<DiscountCodeResponse> {
    
    const headers = new HttpHeaders({
      'Accept-Language': this.langService.currentLang
    });
    
    return this.http.get<DiscountCodeResponse>(`${this.apiUrl}/${code}`, { headers, withCredentials: true });
  }

  getDiscountCodes(): Observable<DiscountCodeResponse[]> {
    return this.http.get<DiscountCodeResponse[]>(`${this.apiUrl}/all`, { withCredentials: true });
  }

  addDiscountCode(request: NewDiscountCodeRequest): Observable<NewDiscountCodeResponse> {
    
    const headers = new HttpHeaders({
      'Accept-Language': this.langService.currentLang
    });
    
    return this.http.post<NewDiscountCodeResponse>(`${this.apiUrl}/add-discount-code`, request, { headers, withCredentials: true }).pipe(
      map(response => response),
      catchError(this.handleError)
    )
  }

  updateDiscountCode(request: UpdatedDiscountCodeRequest): Observable<UpdatedAddonPromotionResponse> {

    const headers = new HttpHeaders({
      'Accept-Language': this.langService.currentLang
    });
    
    return this.http.put<UpdatedAddonPromotionResponse>(`${this.apiUrl}/update-discount-code`, request, { headers, withCredentials: true }).pipe(
      map(response => response),
      catchError(this.handleError)
    )
  }

  removeDiscountCode(discountCode: RemovedDiscountCodeRequest): Observable<RemovedDiscountCodeResponse> {
  
      const requestBody = { code: discountCode.code };
  
      return this.http.delete<RemovedDiscountCodeResponse>(`${this.apiUrl}/remove-discount-code`, { body: requestBody, withCredentials: true }).pipe(
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
