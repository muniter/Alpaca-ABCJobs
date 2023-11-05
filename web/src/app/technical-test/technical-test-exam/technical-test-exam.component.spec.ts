/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, fakeAsync, TestBed, tick } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { Component, DebugElement } from '@angular/core';

import { TechnicalTestExamComponent } from './technical-test-exam.component';
import { HttpClientModule } from '@angular/common/http';
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from '@angular/material/dialog';
import { DatePipe } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Exam, ExamResult } from '../exam';
import { Skill } from 'src/app/shared/skill';
import { TechnicalTestService } from '../technical-test.service';
import { Answer, Question, Quiz, QuizResponse } from '../quiz';
import { Observable, of, throwError } from 'rxjs';

export class MatDialogMock {
  closeAll() {
    return true;
  }
}

function getExam(): Exam {
  return new Exam(1, new Skill(1, "Testing"),3);
}

describe('TechnicalTestExamComponent', () => {
  let component: TechnicalTestExamComponent;
  let fixture: ComponentFixture<TechnicalTestExamComponent>;
  let service: TechnicalTestService;
  let router: Router;
  let navigateSpy: any;
  let quizResponse: QuizResponse;
  let quizAnswerResponse: QuizResponse;
  let quiz: Quiz;
  let quizResult: Quiz;
  let examResult: ExamResult;
  let question: Question;
  let answer1: Answer;
  let answer2: Answer;
  let answer3: Answer;
  let answersQuestion: Answer[];

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientModule,
        HttpClientTestingModule
      ],
      providers: [
        DatePipe,
        { 
          provide: ActivatedRoute,
          useValue: { snapshot: { params: { 'userToken': '123' } } }
        },
        { provide: MatDialogRef, useValue: undefined }, 
        { provide: MAT_DIALOG_DATA, 
          useValue: { 
            token: 'fake-jwt-token', 
            dialog: new MatDialogMock(), 
            theme: 'candidate-theme',
            quiz: getExam()
          }
        }
      ],
      declarations: [ TechnicalTestExamComponent ]
    })
    .compileComponents();
  }));
  
  beforeEach(() => {
    fixture = TestBed.createComponent(TechnicalTestExamComponent);
    service = TestBed.inject(TechnicalTestService);
    router = TestBed.inject(Router)
    component = fixture.componentInstance;
  
    answer1 = new Answer(1, 1, "Respuesta 1");
    answer2 = new Answer(2, 1, "Respuesta 2");
    answer3 = new Answer(3, 1, "Respuesta 3");
    answersQuestion = [answer1, answer2, answer3];
  
    question = new Question(1, 1, "Pregunta 1", 2, answersQuestion);
    quiz = new Quiz(5, 1, question, null);
    quizResponse = new QuizResponse(1,quiz);

    examResult = new ExamResult(1, getExam(), 1, 2, true);
    quizResult = new Quiz(5, 1, question, examResult);
    quizAnswerResponse = new QuizResponse(1,quizResult);
    
    navigateSpy = spyOn(router, 'navigateByUrl').and.stub();

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should start exam', () => {
    let startExamSpy = spyOn(service, 'startExam').and.returnValue(of(quizResponse));    
    component.startExam();
    fixture.detectChanges();
    expect(startExamSpy).toHaveBeenCalledTimes(1);
    expect(component.quiz).toEqual(quizResponse.data);  
  });

  it('should redirect when user token not found', () => {
    const closeAllDialogSpy = spyOn(component.dialog, 'closeAll');
    let token = '';
    component.validateToken(token);
    expect(component.token).toEqual('');
    expect(navigateSpy).toHaveBeenCalledTimes(1);
    expect(closeAllDialogSpy).toHaveBeenCalledTimes(1);
  });

  it('should handle exceptions when start exam fail', fakeAsync(() => {
    expect([null,'']).toContain(component.messageError);
    let error = { error: { detail: "Invalid authorization code" } };
    let startExamSpyFail = spyOn(service, 'startExam').and.returnValue(throwError(() => error));
    const closeAllDialogSpy = spyOn(component.dialog, 'closeAll');
    component.startExam();
    fixture.detectChanges();
    expect(startExamSpyFail).toHaveBeenCalledTimes(1);
    expect(component.messageError).not.toBeNull();
    tick(3000);
    expect([null,'']).toContain(component.messageError);
    expect(closeAllDialogSpy).toHaveBeenCalledTimes(1);
  }));

  it('should answer question', () => {
    let answerQuestionSpy = spyOn(service, 'answerQuestion').and.returnValue(of(quizAnswerResponse));    
    component.answerQuestion(answer1);
    fixture.detectChanges();
    expect(answerQuestionSpy).toHaveBeenCalledTimes(1);
    expect(component.quiz).toEqual(quizAnswerResponse.data);  
    expect(component.completedQuiz).toBeTrue();
  });

  it('should handle exceptions when answer question fail', fakeAsync(() => {
    expect([null,'']).toContain(component.messageError);
    let error = { error: { detail: "Invalid authorization code" } };
    let answerQuestionSpyFail = spyOn(service, 'answerQuestion').and.returnValue(throwError(() => error));
    component.answerQuestion(answer1);
    fixture.detectChanges();
    expect(answerQuestionSpyFail).toHaveBeenCalledTimes(1);
    expect(component.messageError).not.toBeNull();
    tick(3000);
    expect([null,'']).toContain(component.messageError);
  }));
});


