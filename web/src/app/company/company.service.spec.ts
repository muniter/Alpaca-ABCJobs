/* tslint:disable:no-unused-variable */

import { TestBed, async, inject } from '@angular/core/testing';
import { CompanyService } from './company.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { SkillResponse } from '../shared/skill';
import { EmployeesListResponse } from './Employee';
import { TeamsListResponse } from './Team';
import { PersonalityResponse } from '../shared/Personality';

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

});
