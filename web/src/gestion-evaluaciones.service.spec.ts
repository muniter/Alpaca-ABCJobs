import { TestBed } from '@angular/core/testing';

import { GestionEvaluacionesService } from './gestion-evaluaciones.service';

describe('GestionEvaluacionesService', () => {
  let service: GestionEvaluacionesService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(GestionEvaluacionesService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
