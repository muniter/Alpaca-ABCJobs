/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { TechnicalTestCandidateComponent } from './technical-test-candidate.component';
import { HttpClientModule } from '@angular/common/http';
import { RouterTestingModule } from '@angular/router/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { DatePipe } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { MatDialogConfig, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { SharedModule } from 'src/app/shared/shared.module';
import { DragScrollModule } from 'ngx-drag-scroll';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TechnicalTestService } from '../technical-test.service';
import { of } from 'rxjs';
import { Exam, ExamResponse, ExamResult, ExamResultResponse } from '../exam';
import { Skill } from 'src/app/shared/skill';
import { TechnicalTestExamComponent } from '../technical-test-exam/technical-test-exam.component';

describe('TechnicalTestCandidateComponent', () => {
  let component: TechnicalTestCandidateComponent;
  let service: TechnicalTestService;
  let router: Router;
  let fixture: ComponentFixture<TechnicalTestCandidateComponent>;
  let getExamsSpy: any;
  let getExamsResultSpy: any;
  let availableExams: Exam[];
  let finishedExams: ExamResult[];
  let exam1: Exam, exam2: Exam, exam3: Exam, exam4: Exam;
  let examResult1: ExamResult, examResult2: ExamResult;
  let suggestedExamsResponse: ExamResponse;
  let completedExamsResponse: ExamResultResponse;
  let navigateSpy: any;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientModule,
        HttpClientTestingModule,
        SharedModule,
        RouterTestingModule,
        ReactiveFormsModule,
        MatDialogModule,
        DragScrollModule
      ],
      declarations: [ TechnicalTestCandidateComponent ],
      providers: [DatePipe, {
        provide: ActivatedRoute,
        useValue: { snapshot: { params: { 'userToken': '123' } } }
      }]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TechnicalTestCandidateComponent);
    service = TestBed.inject(TechnicalTestService);
    router = TestBed.inject(Router)
    component = fixture.componentInstance;
    fixture.detectChanges();

    exam1 = new Exam(1, new Skill(1, 'Testing'), 3);
    exam2 = new Exam(2, new Skill(5, 'Proof'), 3);
    exam3 = new Exam(3, new Skill(10, 'Assessment'), 3);
    exam4 = new Exam(4, new Skill(15, 'Exam'), 3);

    availableExams = [exam1, exam2, exam3, exam4];

    examResult1 = new ExamResult(1, exam1, 1, 2, true);
    examResult2 = new ExamResult(2, exam3, 1, 1, true);

    finishedExams = [examResult1, examResult2];

    suggestedExamsResponse = new ExamResponse('success',availableExams);

    completedExamsResponse = new ExamResultResponse('success',finishedExams);

    getExamsSpy = spyOn(service, 'getExams').and.returnValue(of(suggestedExamsResponse));
    getExamsResultSpy = spyOn(service, 'getExamsResult').and.returnValue(of(completedExamsResponse));

    navigateSpy = spyOn(router, 'navigateByUrl').and.stub();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should get and load suggested exams', () => {
    const completedIds = completedExamsResponse.data.map((examResult: {exam:{id: number;};}) => examResult.exam.id);
    const suggestedQuizzes = suggestedExamsResponse.data.filter((exam) => !completedIds.includes(exam.id));

    component.getExams();
    fixture.detectChanges();
    expect(getExamsSpy).toHaveBeenCalledTimes(1);
    expect(getExamsResultSpy).toHaveBeenCalledTimes(1);
    expect(component.suggestedQuizzes).toEqual(suggestedQuizzes);
    expect(component.completedQuizzes).toEqual(completedExamsResponse.data);
  });

  it('should redirect when user token not found', () => {
    let token = '';
    component.validateToken(token);
    expect(component.token).toEqual('');
    expect(navigateSpy).toHaveBeenCalledTimes(1);
  });

  it('should open dialog quiz', () => {
    const openDialogSpy = spyOn(component.dialog, 'open')
      .and.returnValue({ afterClosed: () => of(true) } as MatDialogRef<typeof component>);
    const fakeDialogConfig = new MatDialogConfig;

    component.startExamDialog(exam1);

    expect(openDialogSpy).toHaveBeenCalled();
  });
});