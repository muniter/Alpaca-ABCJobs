import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { CompanyLoginRequest, CompanyRegisterRequest } from './company';
import { EmployeeCreationRequest, EmployeeResponse, EmployeesListResponse } from './Employee';
import { Observable } from 'rxjs';
import { SkillResponse } from '../shared/skill';
import { PersonalityResponse } from '../shared/Personality';
import { TeamCreateRequest, TeamCreateResponse, TeamsListResponse } from './Team';
import { SaveScoresRequest, VacancyRequest, VacancyResponse } from './vacancy';
import { ProjectCreateRequest, ProjectCreateResponse, ProjectsListResponse } from './Project';
import { PositionCreateRequest, PositionCreateResponse, PositionsListResponse } from './Position';

@Injectable({
  providedIn: 'root'
})
export class CompanyService {
  private backCompanyUrl = environment.baseUrl + 'empresas';
  private backProjectUrl = environment.baseUrl + 'proyectos';
  private backUsersUrl = environment.baseUrl + 'usuarios';
  private backCandidatosUtilsUrl = environment.baseUrl + 'candidatos/utils';
  private backUtilsUrl = environment.baseUrl + 'empresas/utils';

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

  getEmployees(token: string) {
    const headers = this.getHeader(token);
    return this.http.get<EmployeesListResponse>(`${this.backCompanyUrl}/employee`, { headers })
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

  getHeader(token: string) {
    return {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    };
  }
}
