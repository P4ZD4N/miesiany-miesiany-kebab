import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { LangService } from '../lang/lang.service';
import { DiscountCodeResponse } from '../../responses/responses';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class DiscountCodesService {

  private apiUrl = 'http://localhost:8080/api/v1/discount-codes';

  constructor(private http: HttpClient, private langService: LangService) { }

  getDiscountCode(code: string): Observable<DiscountCodeResponse[]> {
    
    const headers = new HttpHeaders({
      'Accept-Language': this.langService.currentLang
    });
    
    return this.http.get<DiscountCodeResponse[]>(`${this.apiUrl}/${code}`, { headers, withCredentials: true });
  }
}
