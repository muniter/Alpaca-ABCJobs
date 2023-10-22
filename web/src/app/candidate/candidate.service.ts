import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { environment } from 'src/environments/environment';
import { CandidateLoginRequest, CandidateFormRegister, CandidateServiceSchema, PersonalInfoResponse, SavePersonalInfoRequest } from './candidate';
import { CountryResponse } from '../shared/Country';
import { LanguageResponse } from '../shared/Language';

@Injectable({
  providedIn: 'root'
})
export class CandidateService {
  private backCandidateUrl = environment.baseUrl + 'candidatos';
  private backUtilsUrl = environment.baseUrl + 'candidatos/utils';
  private backUsersUrl = environment.baseUrl + 'usuarios';

  constructor(private http: HttpClient) { }

  userSignUp(candidate: CandidateFormRegister): Observable<any> {
    let candidateServiceSchema = new CandidateServiceSchema(
      candidate.names,
      candidate.lastnames,
      candidate.email,
      candidate.password
    );
    return this.http.post<any>(`${this.backCandidateUrl}/crear`, candidateServiceSchema);
  }

  login(credentials: CandidateLoginRequest): Observable<any> {
    return this.http.post<any>(`${this.backUsersUrl}/login`, credentials);
  }

  getCountries(): Observable<CountryResponse> {
    let response = this.http.get<CountryResponse>(`${this.backUtilsUrl}/countries`)
    return response;
  }

  getLanguages(): Observable<LanguageResponse> {
    let response = this.http.get<LanguageResponse>(`${this.backUtilsUrl}/languages`)
    return response;
  }

  getPersonalInfo(token: string): Observable<PersonalInfoResponse> {
    
    const headers = getHeader(token)

    return this.http.get<PersonalInfoResponse>(`${this.backCandidateUrl}/personal-info`, { headers } )
  }

  updatePersonalInfo(request: SavePersonalInfoRequest, token: string): Observable<any> {
     
    const headers = getHeader(token)

    return this.http.post<PersonalInfoResponse>(`${this.backCandidateUrl}/personal-info`, request, { headers } )
  }

}
function getHeader(token: string) {
  return {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}`
  };
}

