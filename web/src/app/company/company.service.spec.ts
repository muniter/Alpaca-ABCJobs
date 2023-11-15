/* tslint:disable:no-unused-variable */

import { TestBed, async, inject } from '@angular/core/testing';
import { CompanyService } from './company.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { SkillResponse } from '../shared/skill';
import { EmployeeCreationRequest, EmployeeResponse, EmployeesListResponse } from './Employee';
import { TeamCreateRequest, TeamCreateResponse, TeamsListResponse } from './Team';
import { Personality, PersonalityResponse } from '../shared/Personality';
import { VacancyRequest, VacancyResponse } from './vacancy';
import { ProjectCreateRequest, ProjectCreateResponse, ProjectsListResponse } from './Project';
import { CompanyLoginRequest, CompanyRegisterRequest } from './company';

describe('Service: Company', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [CompanyService]
    });
  });

  it('should ...', inject([CompanyService], (service: CompanyService) => {
    expect(service).toBeTruthy();
  }));

  it('should create headers', inject([CompanyService], (service: CompanyService) => {
    let header = service.getHeader("123");
    expect(header).toBeDefined();
  }));

  it('should get skills', inject([CompanyService, HttpClientTestingModule], (service: CompanyService, client: HttpClientTestingModule) => {
    service.getSkills().subscribe(value => {
      expect(value).toBeDefined();
      expect(value).toBeInstanceOf(SkillResponse);
    });
  }));

  it('should get personalities', inject([CompanyService, HttpClientTestingModule], (service: CompanyService, client: HttpClientTestingModule) => {
    service.getPersonalities().subscribe(value => {
      expect(value).toBeDefined();
      expect(value).toBeInstanceOf(PersonalityResponse);
    });
  }));

  it('should get employees', inject([CompanyService, HttpClientTestingModule], (service: CompanyService, client: HttpClientTestingModule) => {
    service.getEmployees("tokentest").subscribe(value => {
      expect(value).toBeDefined();
      expect(value).toBeInstanceOf(EmployeesListResponse);
    });
  }));

  it('should get teams', inject([CompanyService, HttpClientTestingModule], (service: CompanyService, client: HttpClientTestingModule) => {
    service.getTeams("tokentest").subscribe(value => {
      expect(value).toBeDefined();
      expect(value).toBeInstanceOf(TeamsListResponse);
    });
  }));

  it('should get vacancies', inject([CompanyService, HttpClientTestingModule], (service: CompanyService, client: HttpClientTestingModule) => {
    service.getVacancies("tokentest").subscribe(value => {
      expect(value).toBeDefined();
      expect(value).toBeInstanceOf(VacancyResponse);
    });
  }));

  it('should get projects', inject([CompanyService, HttpClientTestingModule], (service: CompanyService, client: HttpClientTestingModule) => {
    service.getProjects("tokentest").subscribe(value => {
      expect(value).toBeDefined();
      expect(value).toBeInstanceOf(ProjectsListResponse);
    });
  }));

  it('should post projects', inject([CompanyService, HttpClientTestingModule], (service: CompanyService, client: HttpClientTestingModule) => {
    service.postProject("tokentest", new ProjectCreateRequest("", "", 1)).subscribe(value => {
      expect(value).toBeDefined();
      expect(value).toBeInstanceOf(ProjectCreateResponse);
    });
  }));

  it('should post team', inject([CompanyService, HttpClientTestingModule], (service: CompanyService, client: HttpClientTestingModule) => {
    service.postTeam("tokentest", new TeamCreateRequest("", [1, 3])).subscribe(value => {
      expect(value).toBeDefined();
      expect(value).toBeInstanceOf(TeamCreateResponse);
    });
  }));

  it('should post employee', inject([CompanyService, HttpClientTestingModule], (service: CompanyService, client: HttpClientTestingModule) => {
    service.postEmployee("tokentest", new EmployeeCreationRequest("", "", new Personality(1, ""), [1, 3])).subscribe(value => {
      expect(value).toBeDefined();
      expect(value).toBeInstanceOf(EmployeeResponse);
    });
  }));

  it('should preselect candidate', inject([CompanyService, HttpClientTestingModule], (service: CompanyService, client: HttpClientTestingModule) => {
    service.preselectCandidate("tokentest", 1, new VacancyRequest(32)).subscribe(value => {
      expect(value).toBeDefined();
      expect(value).toBeInstanceOf(VacancyResponse);
    });
  }));

  it('should log in', inject([CompanyService, HttpClientTestingModule], (service: CompanyService, client: HttpClientTestingModule) => {
    service.companyLogin(new CompanyLoginRequest("", "")).subscribe(value => {
      expect(value).toBeDefined();
    });
  }));

  it('should sign up', inject([CompanyService, HttpClientTestingModule], (service: CompanyService, client: HttpClientTestingModule) => {
    service.companySignUp(new CompanyRegisterRequest("", "", "")).subscribe(value => {
      expect(value).toBeDefined();
    });
  }));

});
