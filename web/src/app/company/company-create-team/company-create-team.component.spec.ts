/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { CompanyCreateTeamComponent } from './company-create-team.component';

describe('CompanyCreateTeamComponent', () => {
  let component: CompanyCreateTeamComponent;
  let fixture: ComponentFixture<CompanyCreateTeamComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CompanyCreateTeamComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CompanyCreateTeamComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
