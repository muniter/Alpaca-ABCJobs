/* tslint:disable:no-unused-variable */

import { TestBed, async, inject } from '@angular/core/testing';
import { CandidateService } from './candidate.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';

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
    });

  }));

  it('should get languages', inject([CandidateService, HttpClientTestingModule], (service: CandidateService, client: HttpClientTestingModule) => {

    //spyOn(client, 'get<>').and.stub();

    service.getLanguages().subscribe(value => {
      expect(value).toBeDefined();
    });

  }));

  it('should get personalinfo', inject([CandidateService, HttpClientTestingModule], (service: CandidateService, client: HttpClientTestingModule) => {

    //spyOn(client, 'get<>').and.stub();

    service.getPersonalInfo("aa").subscribe(value => {
      expect(value).toBeDefined();
    });

  }));
});
