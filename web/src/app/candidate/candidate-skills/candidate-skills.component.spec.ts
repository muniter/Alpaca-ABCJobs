/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, fakeAsync, TestBed, tick, waitForAsync } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement, inject } from '@angular/core';

import { CandidateSkillsComponent } from './candidate-skills.component';
import { HttpClientModule } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { DatePipe } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { CandidateService } from '../candidate.service';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatDividerModule } from '@angular/material/divider';
import { SharedModule } from 'src/app/shared/shared.module';
import { Skill, SkillResponse } from 'src/app/shared/skill';
import { of } from 'rxjs';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { faker } from '@faker-js/faker';
import { TechResponse, TechServiceSchema } from '../tech';

describe('CandidateSkillsComponent', () => {
  let component: CandidateSkillsComponent;
  let fixture: ComponentFixture<CandidateSkillsComponent>;
  let service: CandidateService;
  let router: Router;
  let debug: DebugElement;
  let navigateSpy: any;
  let skill1: Skill;
  let skill2: Skill;
  let skills: Skill[];
  let skillResponse: SkillResponse;
  let techResponse: TechResponse;
  let techServiceSchema1: TechServiceSchema;
  let techServiceSchema2: TechServiceSchema;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientModule,
        HttpClientTestingModule,
        SharedModule,
        RouterTestingModule,
        FormsModule,
        ReactiveFormsModule,
        BrowserAnimationsModule,
        MatFormFieldModule,
        MatSelectModule,
        MatDividerModule,
        MatIconModule,
        MatInputModule
      ],
      declarations: [ 
        CandidateSkillsComponent 
      ],
      providers: [DatePipe, {
        provide: ActivatedRoute,
        useValue: { snapshot: { params: { 'token': '123' } } }
      }]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CandidateSkillsComponent);
    service = TestBed.inject(CandidateService);
    router = TestBed.inject(Router)
    component = fixture.componentInstance;

    skill1 = new Skill(1, 'testing');
    skill2 = new Skill(2, 'proof');
    skills = [skill1,skill2];
    skillResponse = new SkillResponse(true,skills);

    techServiceSchema1 = new TechServiceSchema(1, skill1, faker.lorem.words({ min: 2, max: 4 }));
    techServiceSchema2 = new TechServiceSchema(2, skill2, faker.lorem.words({ min: 2, max: 4 }));
    techResponse = new TechResponse(true, [techServiceSchema1,techServiceSchema2]);

    navigateSpy = spyOn(router, 'navigateByUrl').and.stub();

    fixture.detectChanges();
    debug = fixture.debugElement;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should retrieve skills', () => {
    let getSkillsSpy = spyOn(service, 'getTechnicalInfoTypes').and.returnValue(of(skillResponse));    
    component.getSkills();
    expect(getSkillsSpy).toHaveBeenCalledTimes(1);
    expect(component.skills).toEqual(skills);
    expect(component.skills[0]?.id).toEqual(1);
    expect(component.skills[0]?.name).toEqual('testing');
    expect(component.skills[1]?.id).toEqual(2);
    expect(component.skills[1]?.name).toEqual('proof');
  });

  it('should redirect when user token not found', () => {
    let token = '';
    component.validateToken(token);
    expect(component.token).toEqual('');
    expect(navigateSpy).toHaveBeenCalledTimes(1);
  });

  it('should validate tech', async () => {
    component.enableForm();
    component.addTech();
    const skillTech = component.knowledge.at(0).get("tech");
    skillTech?.markAsTouched();
    skillTech?.markAsUntouched();
    expect(skillTech?.hasError('required')).toBeTruthy();
    fixture.detectChanges();
    skillTech?.setValue(skill1);
    fixture.detectChanges();
    expect(skillTech?.hasError('required')).toBeFalsy();
  })

  it('should validate moreInfoTech', async () => {
    component.enableForm();
    component.addTech();
    const moreInfoTech = component.knowledge.at(0).get("moreInfoTech");
    moreInfoTech?.markAsTouched();

    moreInfoTech?.setValue('       ');
    fixture.detectChanges();
    expect(moreInfoTech?.hasError('isOnlyWhiteSpace')).toBeTruthy();
    expect(moreInfoTech?.valid).toBeFalsy();
    expect(debug.query(By.css('#moreInfoTechFormField-0 mat-error')).nativeElement.innerHTML).toContain('Información adicional no puede ser solo espacios');

    moreInfoTech?.setValue(faker.lorem.words({ min: 501, max: 510 }));
    fixture.detectChanges();

    expect(moreInfoTech?.hasError('maxlength')).toBeTruthy();
    expect(moreInfoTech?.valid).toBeFalsy();
    expect(debug.query(By.css('#moreInfoTechFormField-0 mat-error')).nativeElement.innerHTML).toContain('Información adicional puede tener hasta 500 caracteres');

    expect(debug.query(By.css('app-abc-button[type="submit"]')).attributes['ng-reflect-disabled']).toEqual(
      "true"
    );
  });

  it('should remove tech', fakeAsync(() => {
    component.knowledge.clear();
    component.enableForm();
    
    component.addTech();
    let skillTech = component.knowledge.at(-1).get("tech");
    let moreInfoTech = component.knowledge.at(-1).get("moreInfoTech");
    skillTech?.setValue(skill1);
    moreInfoTech?.setValue(faker.lorem.words({ min: 2, max: 4 }));
    
    component.addTech();
    skillTech = component.knowledge.at(-1).get("tech");
    moreInfoTech = component.knowledge.at(-1).get("moreInfoTech");
    skillTech?.setValue(skill2);
    moreInfoTech?.setValue(faker.lorem.words({ min: 2, max: 4 }));

    expect(component.knowledge.length).toEqual(2);
    component.deleteTech(0);
    expect(component.knowledge.length).toEqual(1);
    skillTech = component.knowledge.at(0).get("tech");
    expect(skillTech?.getRawValue()).toEqual(skill2);
  }));
  
  it('should save technical info', fakeAsync(() => {
    let saveTechnicalInfoSpy = spyOn(service, 'updateTechnicalInfo').and.returnValue(of(
      { success: true }
    ));
    component.knowledge.clear();
    component.enableForm();
    
    component.addTech();
    let skillTech = component.knowledge.at(-1).get("tech");
    let moreInfoTech = component.knowledge.at(-1).get("moreInfoTech");
    skillTech?.setValue(skill1);
    moreInfoTech?.setValue(faker.lorem.words({ min: 2, max: 4 }));
    
    component.addTech();
    skillTech = component.knowledge.at(-1).get("tech");
    moreInfoTech = component.knowledge.at(-1).get("moreInfoTech");
    skillTech?.setValue(skill2);
    moreInfoTech?.setValue(faker.lorem.words({ min: 2, max: 4 }));

    expect(component.globalMessage).toBeUndefined();
    component.saveTechnicalInfo();
    expect(saveTechnicalInfoSpy).toHaveBeenCalledTimes(1);
    expect(component.globalMessage).not.toBeNull();
    tick(3000);
    expect(component.globalMessage).toEqual("");
  }));

  it('should get technical info', async () => {
    let saveTechnicalInfoSpy = spyOn(service, 'getTechnicalInfo').and.returnValue(of(techResponse));
    component.skills = skills;
    component.getTechnicalInfo();
    expect(saveTechnicalInfoSpy).toHaveBeenCalledTimes(1);
    expect(component.technicalsInfo.length).toEqual(2);
    expect(component.technicalsInfo[0].id).toEqual(techResponse.data[0].id);
    expect(component.technicalsInfo[0].tech).toEqual(techResponse.data[0].type);
    expect(component.technicalsInfo[0].moreInfoTech).toEqual(techResponse.data[0].description);
  });
  
  it('should manage exceptions', async () => {
    let exception = { error: { errors: { 
      type: "is required", 
      description: "not space only", 
      global: "Global error"
    }}};
    component.knowledge.clear();
    component.enableForm();
    
    component.addTech();
    let skillTech = component.knowledge.at(-1).get("tech");
    let moreInfoTech = component.knowledge.at(-1).get("moreInfoTech");
    skillTech?.setValue(skill1);
    moreInfoTech?.setValue(faker.lorem.words({ min: 2, max: 4 }));

    component.globalError = "";
    component.setErrorBack(0, exception);
    fixture.detectChanges();
    expect(component.globalError).not.toEqual("");
    expect(component.globalError).toContain("Error");
  });
});
