import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { environment } from '../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class CandidateService {
  private backCandidateUrl = environment.backCandidateUrl;

  constructor(private http: HttpClient) { }

  userSignUp(
    names: string,
    lastnames: string,
    email: string,
    password: string
  ): Observable<any> {
    return this.http.post<any>(`${this.backCandidateUrl}/crear`, {
      nombres: names,
      apellidos: lastnames,
      email: email,
      password: password
    });
  }

}
