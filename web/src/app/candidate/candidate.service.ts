import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { CandidateLoginRequest, CandidateFormRegister, CandidateServiceSchema, PersonalInfoResponse, SavePersonalInfoRequest, CandidateSearchResponse } from './candidate';
import { CountryResponse } from '../shared/Country';
import { LanguageResponse } from '../shared/Language';
import { CollegeDegreeResponse } from '../shared/CollegeDegree';
import { Career, CareerResponse, CareerServiceSchema } from './career';
import { JobResponse, JobServiceSchema } from './job';
import { SkillResponse } from '../shared/skill';
import { TechRequest, TechRequestRow, TechResponse } from './tech';
import { Search } from '../company/search';
import { CandidateInterviewsResponse } from './CandidateInterview';

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
      candidate.last_names,
      candidate.email,
      candidate.password
    );
    return this.http.post<any>(`${this.backCandidateUrl}/crear`, candidateServiceSchema);
  }

  login(credentials: CandidateLoginRequest): Observable<any> {
    return this.http.post<any>(`${this.backUsersUrl}/login`, credentials);
  }

  getCountries(): Observable<CountryResponse> {
    return this.http.get<CountryResponse>(`${this.backUtilsUrl}/countries`)
  }

  getLanguages(): Observable<LanguageResponse> {
    return this.http.get<LanguageResponse>(`${this.backUtilsUrl}/languages`)
  }

  getPersonalInfo(token: string): Observable<PersonalInfoResponse> {

    const headers = this.getHeader(token)

    return this.http.get<PersonalInfoResponse>(`${this.backCandidateUrl}/personal-info`, { headers })
  }

  updatePersonalInfo(request: SavePersonalInfoRequest, token: string): Observable<any> {

    const headers = this.getHeader(token)

    return this.http.post<PersonalInfoResponse>(`${this.backCandidateUrl}/personal-info`, request, { headers })
  }

  getCollegeDegrees(): Observable<CollegeDegreeResponse> {
    return this.http.get<CollegeDegreeResponse>(`${this.backUtilsUrl}/title-types`)
  }

  getCareersInfo(token: string): Observable<CareerResponse> {
    const headers = this.getHeader(token);
    return this.http.get<CareerResponse>(`${this.backCandidateUrl}/academic-info`, { headers })
  }

  addCareerInfo(career: Career, token: string): Observable<any> {
    const headers = this.getHeader(token);
    let request = new CareerServiceSchema(
      career.id,
      career.collegeDegree.id,
      career.careerTitle,
      career.school,
      career.careerStart,
      career.careerEnd,
      career.achievement
    );

    return this.http.post<CareerResponse>(
      `${this.backCandidateUrl}/academic-info`, request, { headers })
  }

  updateCareerInfo(career: Career, token: string): Observable<any> {
    const headers = this.getHeader(token);
    let request = new CareerServiceSchema(
      career.id,
      career.collegeDegree.id,
      career.careerTitle,
      career.school,
      career.careerStart,
      career.careerEnd,
      career.achievement
    );
    return this.http.post<CareerResponse>(
      `${this.backCandidateUrl}/academic-info/${request.id}`, request, { headers })
  }

  deleteCareerInfo(careerId: number, token: string): Observable<any> {
    const headers = this.getHeader(token);
    return this.http.delete<CareerResponse>(
      `${this.backCandidateUrl}/academic-info/${careerId}`, { headers })
  }

  getSkills(): Observable<SkillResponse> {
    return this.http.get<SkillResponse>(`${this.backUtilsUrl}/skills`)
  }

  getJobsInfo(token: string): Observable<JobResponse> {
    const headers = this.getHeader(token);
    return this.http.get<JobResponse>(`${this.backCandidateUrl}/work-info`, { headers })
  }

  addJobInfo(request: JobServiceSchema, token: string): Observable<any> {
    const headers = this.getHeader(token);

    return this.http.post<JobResponse>(
      `${this.backCandidateUrl}/work-info`, request, { headers })
  }

  updateJobInfo(request: JobServiceSchema, token: string): Observable<any> {
    const headers = this.getHeader(token);

    return this.http.post<JobResponse>(
      `${this.backCandidateUrl}/work-info/${request.id}`, request, { headers })
  }

  deleteJobInfo(jobId: number, token: string): Observable<any> {
    const headers = this.getHeader(token);
    return this.http.delete<JobResponse>(
      `${this.backCandidateUrl}/work-info/${jobId}`, { headers })
  }

  getTechnicalInfoTypes(): Observable<SkillResponse> {
    return this.http.get<SkillResponse>(`${this.backUtilsUrl}/technical-info-types`)
  }

  getTechnicalInfo(token: string): Observable<TechResponse> {
    const headers = this.getHeader(token);
    return this.http.get<TechResponse>(`${this.backCandidateUrl}/technical-info`, { headers })
  }

  updateTechnicalInfo(requestRows: TechRequestRow[], token: string): Observable<any> {
    const headers = this.getHeader(token);
    const request = new TechRequest(requestRows);

    return this.http.post<JobResponse>(
      `${this.backCandidateUrl}/technical-info/batch-set`, request, { headers })
  }

  searchCandidate(request: Search, token: string){
    const headers = this.getHeader(token);
    return this.http.post<CandidateSearchResponse>(`${this.backCandidateUrl}/search`, request, { headers })
  }

  getInterviews(token: string): Observable<CandidateInterviewsResponse> {
    const headers = this.getHeader(token);
    return this.http.get<CandidateInterviewsResponse>(`${this.backCandidateUrl}/interviews`, { headers })
  }

  getHeader(token: string) {
    return {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    };
  }
}

