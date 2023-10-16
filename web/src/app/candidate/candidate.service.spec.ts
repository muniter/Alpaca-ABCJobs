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
});
