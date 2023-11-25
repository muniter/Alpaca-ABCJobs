import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { CompanyLoginRequest, CompanyRegisterRequest } from './company';
import { EmployeeCreationRequest, EmployeeResponse, EmployeesListResponse } from './Employee';
import { Observable } from 'rxjs';
import { SkillResponse } from '../shared/skill';
import { PersonalityResponse } from '../shared/Personality';
import { TeamCreateRequest, TeamCreateResponse, TeamsListResponse } from './Team';
import { SaveScoresRequest, ScheduleInterviewRequest, SelectCandidateRequest, VacancyRequest, VacancyResponse } from './vacancy';
import { ProjectCreateRequest, ProjectCreateResponse, ProjectsListResponse } from './Project';
import { PositionCreateRequest, PositionCreateResponse, PositionsListResponse } from './Position';
import { CountryResponse } from '../shared/Country';

@Injectable({
  providedIn: 'root'
})
export class CompanyService {
  private backCompanyUrl = environment.baseUrl + 'empresas';
  private backProjectUrl = environment.baseUrl + 'proyectos';
  private backUsersUrl = environment.baseUrl + 'usuarios';
  private backCandidatosUtilsUrl = environment.baseUrl + 'candidatos/utils';
  private backUtilsUrl = environment.baseUrl + 'empresas/utils';
  private candUtilsUrl = environment.baseUrl + 'candidatos/utils';

  constructor(private http: HttpClient) { }

  companySignUp(company: CompanyRegisterRequest) {
    return this.http.post<any>(`${this.backCompanyUrl}/crear`, company);
  }

  companyLogin(credentials: CompanyLoginRequest) {
    return this.http.post<any>(`${this.backUsersUrl}/login`, credentials);
  }

  getSkills(): Observable<SkillResponse> {
    return this.http.get<SkillResponse>(`${this.backCandidatosUtilsUrl}/skills`)
  }

  getPersonalities(): Observable<PersonalityResponse> {
    return this.http.get<PersonalityResponse>(`${this.backUtilsUrl}/personalities`)
  }

  getEmployees(token: string, hired: boolean|undefined = undefined) {
    const options = { headers: this.getHeader(token), params: {} };
    if (hired !== undefined) {
      options.params = { hired_abc: hired ? 'true' : 'false' };
    }
    return this.http.get<EmployeesListResponse>(`${this.backCompanyUrl}/employee`, options)
  }

  postEmployee(token: string, request: EmployeeCreationRequest) {
    const headers = this.getHeader(token)

    return this.http.post<EmployeeResponse>(`${this.backCompanyUrl}/employee`, request, { headers })
  }

  getTeams(token: string) {
    const headers = this.getHeader(token);
    return this.http.get<TeamsListResponse>(`${this.backCompanyUrl}/team`, { headers })
  }

  getProjects(token: string) {
    const headers = this.getHeader(token);
    return this.http.get<ProjectsListResponse>(`${this.backProjectUrl}/project`, { headers })
  }

  getPositions(token: string) {
    const headers = this.getHeader(token);
    return this.http.get<PositionsListResponse>(`${this.backCompanyUrl}/vacancy`, { headers })
  }

  postTeam(token: string, request: TeamCreateRequest) {
    const headers = this.getHeader(token);
    return this.http.post<TeamCreateResponse>(`${this.backCompanyUrl}/team`, request, { headers })
  }

  postProject(token: string, request: ProjectCreateRequest) {
    const headers = this.getHeader(token);
    return this.http.post<ProjectCreateResponse>(`${this.backProjectUrl}/project`, request, { headers })
  }

  postPosition(token: string, request: PositionCreateRequest) {
    const headers = this.getHeader(token);
    return this.http.post<PositionCreateResponse>(`${this.backCompanyUrl}/vacancy`, request, { headers })
  }

  getVacancies(token: string): Observable<VacancyResponse> {
    const headers = this.getHeader(token);
    return this.http.get<VacancyResponse>(`${this.backCompanyUrl}/vacancy`, { headers })
  }

  preselectCandidate(token: string, vacancyId: number, request: VacancyRequest) {
    const headers = this.getHeader(token);
    return this.http.post<VacancyResponse>(`${this.backCompanyUrl}/vacancy/${vacancyId}/preselect`, request, { headers })
  }

  saveScores(token: string, vacancyId: number, request: SaveScoresRequest) {
    const headers = this.getHeader(token);
    return this.http.post<any>(`${this.backCompanyUrl}/vacancy/${vacancyId}/test-result`, JSON.stringify(request.scores), { headers })
  }

  postEmployeeEvaluation(token: string, employeeId: number, body: { result: number, date: string }){
    const headers = this.getHeader(token);
    return this.http.post<EmployeeResponse>(`${this.backCompanyUrl}/employee/${employeeId}/evaluation`, body, { headers })
  }

  selectCandidate(token: string, positionId: number, request: SelectCandidateRequest){
    const headers = this.getHeader(token);
    return this.http.post<any>(`${this.backCompanyUrl}/vacancy/${positionId}/select`, request, { headers })
  }

  getCountries(): Observable<CountryResponse> {
    return this.http.get<CountryResponse>(`${this.candUtilsUrl}/countries`)
  }

  scheduleInterview(token: string, positionId: number, request: ScheduleInterviewRequest): Observable<any> {
    const headers = this.getHeader(token);
    return this.http.post<any>(`${this.backCompanyUrl}/vacancy/${positionId}/interivew-date`, request, { headers })
  }

  getHeader(token: string) {
    return {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    };
  }
}
