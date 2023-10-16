import { TestBed } from '@angular/core/testing';
import { HttpClientModule } from '@angular/common/http';

import { GestionEvaluacionesService } from './gestion-evaluaciones.service';

describe('GestionEvaluacionesService', () => {
  let service: GestionEvaluacionesService;
  
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientModule
      ]
    });
    service = TestBed.inject(GestionEvaluacionesService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
