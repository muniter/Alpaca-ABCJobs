/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { LoginLogonContainerComponent } from './login-logon-container.component';

describe('LoginLogonContainerComponent', () => {
  let component: LoginLogonContainerComponent;
  let fixture: ComponentFixture<LoginLogonContainerComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ LoginLogonContainerComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LoginLogonContainerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
