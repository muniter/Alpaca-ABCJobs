/* tslint:disable:no-unused-variable */

import { TestBed, async, inject } from '@angular/core/testing';
import { CompanyService } from './company.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';

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
});
