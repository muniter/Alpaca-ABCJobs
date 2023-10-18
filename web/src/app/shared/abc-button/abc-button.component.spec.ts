/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';

import { AbcButtonComponent } from './abc-button.component';

describe('AbcButtonComponent', () => {
  let component: AbcButtonComponent;
  let fixture: ComponentFixture<AbcButtonComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [AbcButtonComponent],
      imports: [MatButtonModule]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AbcButtonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
