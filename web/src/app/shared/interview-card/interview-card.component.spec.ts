/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { InterviewCardComponent } from './interview-card.component';
import { CandidateInterview } from 'src/app/candidate/CandidateInterview';

describe('InterviewCardComponent', () => {
  let component: InterviewCardComponent;
  let fixture: ComponentFixture<InterviewCardComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [InterviewCardComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InterviewCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should parse date', () => {
    component.interview = new CandidateInterview(1, "", "", "", true, 1);
    fixture.detectChanges();

    expect(component).toBeTruthy();
  });
});
