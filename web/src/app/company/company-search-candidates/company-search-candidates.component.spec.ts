/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { CompanySearchCandidatesComponent } from './company-search-candidates.component';
import { HttpClientModule } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { SharedModule } from 'src/app/shared/shared.module';
import { RouterTestingModule } from '@angular/router/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatDialogModule } from '@angular/material/dialog';
import { NgxPaginationModule } from 'ngx-pagination';
import { Router } from '@angular/router';
import { CandidateSearch } from 'src/app/candidate/candidate';
import { Country } from 'src/app/shared/Country';
import { Skill } from 'src/app/shared/skill';
import { Language } from 'src/app/shared/Language';
import { MatChipsModule } from '@angular/material/chips';
import { MatIconModule } from '@angular/material/icon';

describe('CompanySearchCandidatesComponent', () => {
  let component: CompanySearchCandidatesComponent;
  let fixture: ComponentFixture<CompanySearchCandidatesComponent>;
  let router: Router;
  let debug: DebugElement;
  let navigateSpy: any;
  let country1: Country;
  let country2: Country;
  let skill1: Skill;
  let skill2: Skill;
  let skills: Skill[];
  let skills_related: Skill[];
  let tech1: Skill;
  let tech2: Skill;
  let techs: Skill[];
  let techs_related: Skill[];
  let lang1: Language;
  let lang2: Language;
  let langs: Language[];
  let langs_related: Language[];
  let candidate1: CandidateSearch;
  let candidate2: CandidateSearch;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientModule,
        HttpClientTestingModule,
        SharedModule,
        RouterTestingModule,
        ReactiveFormsModule,
        MatDialogModule,
        MatIconModule,
        BrowserAnimationsModule,
        NgxPaginationModule,
        MatChipsModule
      ],
      declarations: [ CompanySearchCandidatesComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CompanySearchCandidatesComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router)

    country1 = new Country(1, "CO", "COL", "Colombia", "");
    country2 = new Country(2, "AR", "ARG", "Argentina", "");
    skill1 = new Skill(1, 'testing');
    skill2 = new Skill(2, 'proof');
    skills = [skill1,skill2];
    skills_related = [skill1];
    tech1 = new Skill(10, 'python');
    tech2 = new Skill(11, 'nestjs');
    techs = [tech1,tech2];
    techs_related = [tech1];
    lang1 = new Language("ES", "Spanish");
    lang2 = new Language("EN", "English");
    langs = [lang1,lang2];
    langs_related = [lang1];

    candidate1 = new CandidateSearch(
      1,
      "name", 
      "lastname", 
      "name@email.com", 
      country1, 
      "Bogota", 
      skills, 
      skills_related,
      techs,
      techs_related,
      langs,
      langs_related
    );
    candidate2 = new CandidateSearch(
      2,
      "name 2", 
      "lastname 2", 
      "name2@email.com", 
      country2, 
      "Buenos aíres", 
      skills, 
      skills_related,
      techs,
      techs_related,
      langs,
      langs_related
    );
    
    navigateSpy = spyOn(router, 'navigateByUrl').and.stub();
    fixture.detectChanges();
    debug = fixture.debugElement;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should redirect when user token not found', () => {
    let token = '';
    component.validateToken(token);
    expect(component.token).toEqual('');
    expect(navigateSpy).toHaveBeenCalledTimes(1);
  });

  it('should show four candidates in page by default', () => {
    component.ngOnInit();
    expect(component.itemsInPage).toEqual(4);
  });

  it('should show message with candidates no found', () => {
    component.candidates = [];
    const element = debug.query(By.css('#candidatesnotfound'));
    expect(element).toBeTruthy();
  });

  it('should show candidates', () => {  
    component.candidates = [
      candidate1,
      candidate2
    ];
    fixture.detectChanges();
    const cards = debug.queryAll(By.css("app-person-card"));
    const card1 = cards.at(0)?.query(By.css("div.person-info > h2")).nativeElement.innerHTML;
    const card2 = cards.at(1)?.query(By.css("div.person-info > h2")).nativeElement.innerHTML;
    expect(cards.length).toEqual(2);
    expect(card1).toContain(candidate1.nombres);
    expect(card1).toContain(candidate1.apellidos);
    expect(card2).toContain(candidate2.nombres);
    expect(card2).toContain(candidate2.apellidos);
  });
});
