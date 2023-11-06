/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { CompanyHomeComponent } from './company-home.component';
import { SharedModule } from 'src/app/shared/shared.module';
import { MatDialogModule } from '@angular/material/dialog';
import { DatePipe } from '@angular/common';
import { ActivatedRoute } from '@angular/router';

describe('CompanyHomeComponent', () => {
  let component: CompanyHomeComponent;
  let fixture: ComponentFixture<CompanyHomeComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports:[SharedModule,
      MatDialogModule],
      declarations: [ CompanyHomeComponent ],
      providers: [DatePipe, {
        provide: ActivatedRoute,
        useValue: { snapshot: { params: { 'userToken': '123' } } }
      }]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CompanyHomeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });
  
  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
