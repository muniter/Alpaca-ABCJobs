/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { LogonCompanyFormComponent } from './logon-company-form.component';

describe('LogonCompanyFormComponent', () => {
  let component: LogonCompanyFormComponent;
  let fixture: ComponentFixture<LogonCompanyFormComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ LogonCompanyFormComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LogonCompanyFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
