/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { CompanySearchParamsComponent } from './company-search-params.component';
import { HttpClientModule } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { SharedModule } from 'src/app/shared/shared.module';
import { RouterTestingModule } from '@angular/router/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ActivatedRoute } from '@angular/router';
import { DatePipe } from '@angular/common';
import { MatDialogModule } from '@angular/material/dialog';
import { CompanySearchCandidatesComponent } from '../company-search-candidates/company-search-candidates.component';
import { NgxPaginationModule } from 'ngx-pagination';

describe('CompanySearchParamsComponent', () => {
  let component: CompanySearchParamsComponent;
  let fixture: ComponentFixture<CompanySearchParamsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientModule,
        HttpClientTestingModule,
        SharedModule,
        RouterTestingModule,
        ReactiveFormsModule,
        MatDialogModule,
        BrowserAnimationsModule,
        NgxPaginationModule
      ],
      declarations: [ CompanySearchParamsComponent, CompanySearchCandidatesComponent ],
      providers: [DatePipe, {
        provide: ActivatedRoute,
        useValue: { snapshot: { params: { 'userToken': '123' } } }
      }]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CompanySearchParamsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
