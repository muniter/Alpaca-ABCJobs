/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { CompanyTeamsProjectsComponent } from './company-teams-projects.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormsModule } from '@angular/forms';
import { SharedModule } from 'src/app/shared/shared.module';
import { DatePipe } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { MatDialogModule } from '@angular/material/dialog';

describe('CompanyTeamsProjectsComponent', () => {
  let component: CompanyTeamsProjectsComponent;
  let fixture: ComponentFixture<CompanyTeamsProjectsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        SharedModule,
        FormsModule,
        MatDialogModule
      ],
      declarations: [CompanyTeamsProjectsComponent],
      providers: [
        DatePipe, {
          provide: ActivatedRoute,
          useValue: { snapshot: { params: { 'userToken': '123' } } }
        }
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CompanyTeamsProjectsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be true', () => {
    expect(true).toBeTruthy();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
