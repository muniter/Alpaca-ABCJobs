/* tslint:disable:no-unused-variable */

import { TestBed, async, inject } from '@angular/core/testing';
import { CandidateService } from './candidate.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { CountryResponse } from '../shared/Country';
import { LanguageResponse } from '../shared/Language';
import { CandidateSearchResponse, PersonalInfoResponse } from './candidate';
import { CollegeDegreeResponse } from '../shared/CollegeDegree';
import { CareerResponse } from './career';
import { JobResponse } from './job';
import { TechResponse } from './tech';
import { SkillResponse } from '../shared/skill';
import { Search } from '../company/search';

describe('Service: Candidate', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [CandidateService]
    });
  });

  it('should ...', inject([CandidateService], (service: CandidateService) => {
    expect(service).toBeTruthy();
  }));

  it('should create headers', inject([CandidateService], (service: CandidateService) => {
    let header = service.getHeader("123");
    expect(header).toBeDefined();
  }));

  it('should get countries', inject([CandidateService, HttpClientTestingModule], (service: CandidateService, client: HttpClientTestingModule) => {
    //spyOn(client, 'get<>').and.stub();
    service.getCountries().subscribe(value => {
      expect(value).toBeDefined();
      expect(value).toBeInstanceOf(CountryResponse);
    });
    
  }));
  
  it('should get languages', inject([CandidateService, HttpClientTestingModule], (service: CandidateService, client: HttpClientTestingModule) => {
    //spyOn(client, 'get<>').and.stub();
    service.getLanguages().subscribe(value => {
      expect(value).toBeDefined();
      expect(value).toBeInstanceOf(LanguageResponse);
    });
    
  }));
  
  it('should get personalinfo', inject([CandidateService, HttpClientTestingModule], (service: CandidateService, client: HttpClientTestingModule) => {
    //spyOn(client, 'get<>').and.stub();
    service.getPersonalInfo("aa").subscribe(value => {
      expect(value).toBeDefined();
      expect(value).toBeInstanceOf(PersonalInfoResponse);
    });

  }));

  it('should get collegeDegrees', inject([CandidateService, HttpClientTestingModule], (service: CandidateService, client: HttpClientTestingModule) => {
    service.getCollegeDegrees().subscribe(value => {
      expect(value).toBeDefined();
      expect(value).toBeInstanceOf(CollegeDegreeResponse);
    });
  }));

  it('should get careers info', inject([CandidateService, HttpClientTestingModule], (service: CandidateService, client: HttpClientTestingModule) => {
    service.getCareersInfo("tokentest").subscribe(value => {
      expect(value).toBeDefined();
      expect(value).toBeInstanceOf(CareerResponse);
    });
  }));

  it('should get skills', inject([CandidateService, HttpClientTestingModule], (service: CandidateService, client: HttpClientTestingModule) => {
    service.getSkills().subscribe(value => {
      expect(value).toBeDefined();
      expect(value).toBeInstanceOf(SkillResponse);
    });
  }));

  it('should get jobs info', inject([CandidateService, HttpClientTestingModule], (service: CandidateService, client: HttpClientTestingModule) => {
    service.getJobsInfo("tokentest").subscribe(value => {
      expect(value).toBeDefined();
      expect(value).toBeInstanceOf(JobResponse);
    });
  }));

  it('should get technical info types', inject([CandidateService, HttpClientTestingModule], (service: CandidateService, client: HttpClientTestingModule) => {
    service.getTechnicalInfoTypes().subscribe(value => {
      expect(value).toBeDefined();
      expect(value).toBeInstanceOf(SkillResponse);
    });
  }));

  it('should get technical info', inject([CandidateService, HttpClientTestingModule], (service: CandidateService, client: HttpClientTestingModule) => {
    service.getTechnicalInfo("tokentest").subscribe(value => {
      expect(value).toBeDefined();
      expect(value).toBeInstanceOf(TechResponse);
    });
  }));

  it('should search candidate', inject([CandidateService, HttpClientTestingModule], (service: CandidateService, client: HttpClientTestingModule) => {
    service.searchCandidate({} as Search, "tokentest").subscribe(value => {
      expect(value).toBeDefined();
      expect(value).toBeInstanceOf(CandidateSearchResponse);
    });
  }));

});
