/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { CompanyHeaderComponent } from './company-header.component';
import { MatDialogModule } from '@angular/material/dialog';
import { DatePipe } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatIconModule } from '@angular/material/icon';

describe('CompanyHeaderComponent', () => {
  let component: CompanyHeaderComponent;
  let fixture: ComponentFixture<CompanyHeaderComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports:[
        MatDialogModule,
        MatToolbarModule,
        RouterModule,
        MatIconModule
      ],
      declarations: [
        CompanyHeaderComponent
      ],
      providers: [
        DatePipe,
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { params: { 'userToken': '123' } } }
        }
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CompanyHeaderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
