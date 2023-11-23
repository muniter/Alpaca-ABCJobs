/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { CandidateInterviewsComponent } from './candidate-interviews.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { SharedModule } from 'src/app/shared/shared.module';
import { MatDialogModule } from '@angular/material/dialog';
import { DragScrollModule } from 'ngx-drag-scroll';
import { CandidateService } from '../candidate.service';
import { CandidateInterview, CandidateInterviewsResponse } from '../CandidateInterview';
import { of } from 'rxjs';

describe('CandidateInterviewsComponent', () => {
  let component: CandidateInterviewsComponent;
  let fixture: ComponentFixture<CandidateInterviewsComponent>;

  let candidateService: CandidateService;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        SharedModule,
        MatDialogModule,
        DragScrollModule
      ],
      declarations: [ CandidateInterviewsComponent ],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { params: { 'userToken': '123' } } }
        }
      ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CandidateInterviewsComponent);
    
    candidateService = TestBed.inject(CandidateService)
    spyOn(candidateService, 'getInterviews').and.returnValue(of(new CandidateInterviewsResponse(true, 
      [new CandidateInterview(3, "name", "company", "12/12/2022", false, 23), new CandidateInterview(3, "name", "company", "12/12/3322", false, 23)])))
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
  
  it('should contain interviews', () => {
    expect(component.scheduledInterviews .length).toBe(1);
    expect(component.finishedInterviews .length).toBe(1);
  });
});
