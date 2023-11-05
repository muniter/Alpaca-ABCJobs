/* tslint:disable:no-unused-variable */

import { TestBed, async, inject } from '@angular/core/testing';
import { TechnicalTestService } from './technical-test.service';

describe('Service: TechnicalTest', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [TechnicalTestService]
    });
  });

  it('should ...', inject([TechnicalTestService], (service: TechnicalTestService) => {
    expect(service).toBeTruthy();
  }));
});
