/* tslint:disable:no-unused-variable */

import { TestBed, async, inject } from '@angular/core/testing';
import { TechnicalTestService } from './technical-test.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Answer, QuizResponse } from './quiz';
import { ExamResponse, ExamResultResponse } from './exam';

describe('Service: TechnicalTest', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [TechnicalTestService]
    });
  });

  it('should ...', inject([TechnicalTestService], (service: TechnicalTestService) => {
    expect(service).toBeTruthy();
  }));

  it('should create headers', inject([TechnicalTestService], (service: TechnicalTestService) => {
    let header = service.getHeader("123");
    expect(header).toBeDefined();
  }));

  it('should get exams', inject([TechnicalTestService, HttpClientTestingModule], (service: TechnicalTestService, client: HttpClientTestingModule) => {
    service.getExams("tokentest").subscribe(value => {
      expect(value).toBeDefined();
      expect(value).toBeInstanceOf(ExamResponse);
    });
  }));
  
  it('should get exams result', inject([TechnicalTestService, HttpClientTestingModule], (service: TechnicalTestService, client: HttpClientTestingModule) => {
    service.getExamsResult("tokentest").subscribe(value => {
      expect(value).toBeDefined();
      expect(value).toBeInstanceOf(ExamResultResponse);
    });
  }));
  
  it('should start exam', inject([TechnicalTestService, HttpClientTestingModule], (service: TechnicalTestService, client: HttpClientTestingModule) => {
    service.startExam(1, "tokentest").subscribe(value => {
      expect(value).toBeDefined();
      expect(value).toBeInstanceOf(QuizResponse);
    });
  }));
  
  it('should answer question', inject([TechnicalTestService, HttpClientTestingModule], (service: TechnicalTestService, client: HttpClientTestingModule) => {
    service.answerQuestion(2, new Answer(1, 1, "answer"), "tokentest").subscribe(value => {
      expect(value).toBeDefined();
      expect(value).toBeInstanceOf(QuizResponse);
    });
  }));



});
