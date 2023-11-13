/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, fakeAsync, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement, forwardRef } from '@angular/core';

import { CompanySearchParamsComponent } from './company-search-params.component';
import { HttpClientModule } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { SharedModule } from 'src/app/shared/shared.module';
import { RouterTestingModule } from '@angular/router/testing';
import { NG_VALUE_ACCESSOR, ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ActivatedRoute, Router } from '@angular/router';
import { DatePipe } from '@angular/common';
import { CompanySearchCandidatesComponent } from '../company-search-candidates/company-search-candidates.component';
import { NgxPaginationModule } from 'ngx-pagination';
import { Country, CountryResponse } from 'src/app/shared/Country';
import { CompanyService } from '../company.service';
import { CandidateService } from 'src/app/candidate/candidate.service';
import { of } from 'rxjs';
import { faker } from '@faker-js/faker';
import { Personality, PersonalityResponse } from 'src/app/shared/Personality';
import { CollegeDegree, CollegeDegreeResponse } from 'src/app/shared/CollegeDegree';
import { Skill, SkillResponse } from 'src/app/shared/skill';
import { Language, LanguageResponse } from 'src/app/shared/Language';
import { MatChipInput, MatChipInputEvent, MatChipsModule } from '@angular/material/chips';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatDialogModule } from '@angular/material/dialog';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { CandidateSearch, CandidateSearchResponse } from 'src/app/candidate/candidate';

describe('CompanySearchParamsComponent', () => {
  let component: CompanySearchParamsComponent;
  let fixture: ComponentFixture<CompanySearchParamsComponent>;
  let candidateService: CandidateService;
  let companyService: CompanyService;
  let router: Router;
  let debug: DebugElement;
  let navigateSpy: any;
  let country1: Country;
  let country2: Country;
  let countries: Country[];
  let countryResponse: CountryResponse;
  let personality1: Personality;
  let personality2: Personality;
  let personalities: Personality[];
  let personalityResponse: PersonalityResponse;
  let collegeDegree1: CollegeDegree;
  let collegeDegree2: CollegeDegree;
  let collegeDegrees: CollegeDegree[];
  let collegeDegreeResponse: CollegeDegreeResponse;
  let tech1: Skill;
  let tech2: Skill;
  let techs: Skill[];
  let techResponse: SkillResponse;
  let lang1: Language;
  let lang2: Language;
  let languages: Language[];
  let languageResponse: LanguageResponse;
  let role1: Skill;
  let role2: Skill;
  let roles: Skill[];
  let roleResponse: SkillResponse;
  let candidateSearch1: CandidateSearch;
  let candidateSearch2: CandidateSearch;
  let candidateSearchResponse: CandidateSearchResponse;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientModule,
        HttpClientTestingModule,
        SharedModule,
        RouterTestingModule,
        ReactiveFormsModule,
        MatFormFieldModule,
        MatChipsModule,
        MatDialogModule,
        MatAutocompleteModule,
        BrowserAnimationsModule,
        NgxPaginationModule
      ],
      declarations: [ CompanySearchParamsComponent, CompanySearchCandidatesComponent ],
      providers: [
        DatePipe, 
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { params: { 'userToken': '123' } } }
        }, 
        /* {
          provide: NG_VALUE_ACCESSOR,
          useExisting: forwardRef(() => MatChipInput),
          multi: true,
        } */
      ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CompanySearchParamsComponent);
    candidateService = TestBed.inject(CandidateService);
    companyService = TestBed.inject(CompanyService);
    component = fixture.componentInstance;
    router = TestBed.inject(Router)
    navigateSpy = spyOn(router, 'navigateByUrl').and.stub();

    country1 = new Country(
      parseInt(faker.location.countryCode('numeric')), 
      faker.location.countryCode('alpha-2'), 
      faker.location.countryCode('alpha-3'), 
      faker.location.country(),
      "");
    country2 = new Country(
      parseInt(faker.location.countryCode('numeric')), 
      faker.location.countryCode('alpha-2'), 
      faker.location.countryCode('alpha-3'), 
      faker.location.country(),
      "");
    countries = [country1,country2];
    countryResponse = new CountryResponse(true,countries);

    personality1 = new Personality(faker.number.int(), faker.word.sample());
    personality2 = new Personality(faker.number.int(), faker.word.sample());
    personalities = [personality1,personality2];
    personalityResponse = new PersonalityResponse(true,personalities);

    collegeDegree1 = new CollegeDegree(faker.number.int(), faker.word.sample());
    collegeDegree2 = new CollegeDegree(faker.number.int(), faker.word.sample());
    collegeDegrees = [collegeDegree1,collegeDegree2];
    collegeDegreeResponse = new CollegeDegreeResponse(true, collegeDegrees);

    tech1 = new Skill(faker.number.int(), faker.word.sample());
    tech2 = new Skill(faker.number.int(), faker.word.sample());
    techs = [tech1,tech2];
    techResponse = new SkillResponse(true, techs);

    lang1 = new Language(faker.word.sample(2), faker.word.sample());
    lang2 = new Language(faker.word.sample(2), faker.word.sample());
    languages = [lang1, lang2];
    languageResponse = new LanguageResponse(true, languages);

    role1 = new Skill(faker.number.int(), faker.word.sample());
    role2 = new Skill(faker.number.int(), faker.word.sample());
    roles = [role1, role2];
    roleResponse = new SkillResponse(true, roles);

    candidateSearch1 = new CandidateSearch(
      faker.number.int(),
      faker.person.firstName(), 
      faker.person.lastName(),
      faker.internet.email(),
      country1,
      faker.location.city(),
      [role1],
      [],
      [tech1],
      [],
      [lang1],
      []
    );
    candidateSearch1 = new CandidateSearch(
      faker.number.int(),
      faker.person.firstName(), 
      faker.person.lastName(),
      faker.internet.email(),
      country2,
      faker.location.city(),
      [role2],
      [],
      [tech2],
      [],
      [lang2],
      []
    );
    candidateSearchResponse = new CandidateSearchResponse(true,[candidateSearch1,candidateSearch2]);

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

  it('should retrieve countries', () => {
    let getCountriesSpy = spyOn(candidateService, 'getCountries').and.returnValue(of(countryResponse));    
    component.getCountries();
    expect(getCountriesSpy).toHaveBeenCalledTimes(1);
    expect(component.countries).toEqual(countries);
    expect(component.countries[0]?.alpha_2_code).toEqual(country1.alpha_2_code);
    expect(component.countries[1]?.alpha_2_code).toEqual(country2.alpha_2_code);
  });

  it('should retrieve personalities', () => {
    let getPersonalitySpy = spyOn(companyService, 'getPersonalities').and.returnValue(of(personalityResponse));    
    component.getPersonalities();
    expect(getPersonalitySpy).toHaveBeenCalledTimes(1);
    expect(component.personalities).toEqual(personalities);
    expect(component.personalities[0]?.id).toEqual(personality1.id);
    expect(component.personalities[0]?.name).toEqual(personality1.name);
    expect(component.personalities[1]?.id).toEqual(personality2.id);
    expect(component.personalities[1]?.name).toEqual(personality2.name);
  });

  it('should retrieve collegDegrees', () => {
    let getCollegeDegreeSpy = spyOn(candidateService, 'getCollegeDegrees').and.returnValue(of(collegeDegreeResponse));    
    component.getCollegeDegrees();
    expect(getCollegeDegreeSpy).toHaveBeenCalledTimes(1);
    expect(component.collegeDegrees).toEqual(collegeDegrees);
    expect(component.collegeDegrees[0]?.id).toEqual(collegeDegree1.id);
    expect(component.collegeDegrees[0]?.name).toEqual(collegeDegree1.name);
    expect(component.collegeDegrees[1]?.id).toEqual(collegeDegree2.id);
    expect(component.collegeDegrees[1]?.name).toEqual(collegeDegree2.name);
  });

  it('should retrieve techs', () => {
    let getTechsSpy = spyOn(candidateService, 'getTechnicalInfoTypes').and.returnValue(of(techResponse));    
    component.getTechs();
    expect(getTechsSpy).toHaveBeenCalledTimes(1);
    expect(component.objectTechs).toEqual(techs);
    expect(component.objectTechs[0]?.id).toEqual(tech1.id);
    expect(component.objectTechs[0]?.name).toEqual(tech1.name);
    expect(component.objectTechs[1]?.id).toEqual(tech2.id);
    expect(component.objectTechs[1]?.name).toEqual(tech2.name);
  });
  
  it('should retrieve languages', () => {
    let getLanguagesSpy = spyOn(candidateService, 'getLanguages').and.returnValue(of(languageResponse));    
    component.getLanguages();
    expect(getLanguagesSpy).toHaveBeenCalledTimes(1);
    expect(component.objectLanguages).toEqual(languages);
    expect(component.objectLanguages[0]?.id).toEqual(lang1.id);
    expect(component.objectLanguages[0]?.name).toEqual(lang1.name);
    expect(component.objectLanguages[1]?.id).toEqual(lang2.id);
    expect(component.objectLanguages[1]?.name).toEqual(lang2.name);
  });
  
  it('should retrieve roles', () => {
    let getRolesSpy = spyOn(candidateService, 'getSkills').and.returnValue(of(roleResponse));    
    component.getRoles();
    expect(getRolesSpy).toHaveBeenCalledTimes(1);
    expect(component.objectRoles).toEqual(roles);
    expect(component.objectRoles[0]?.id).toEqual(role1.id);
    expect(component.objectRoles[0]?.name).toEqual(role1.name);
    expect(component.objectRoles[1]?.id).toEqual(role2.id);
    expect(component.objectRoles[1]?.name).toEqual(role2.name);
  });

  it('should search candidates', fakeAsync(() => {
    let getSearchSpy = spyOn(candidateService, 'searchCandidate').and.returnValue(of(candidateSearchResponse));    
    /* component.enableSearch(); */
    /* fixture.detectChanges();  */
    const countryField = component.searchCandidatesForm.get('country_code');
    /* const techField = component.searchCandidatesForm.get('technical_info_types');
    const techElement = debug.query(By.css('#techInput')).nativeElement; */
    
    countryField?.setValue(country1.num_code);
    /* component.addTech({input: techElement, value: tech1.name} as MatChipInputEvent); */
    /* fixture.detectChanges(); */
    component.searchCandidates();
    expect(getSearchSpy).toHaveBeenCalledTimes(1);
    expect(countryField?.getRawValue()).toEqual(country1.num_code);
    expect(component.candidates.length).toEqual(candidateSearchResponse.data.length);
    /* expect(techField?.getRawValue()).toContain(tech1.name); */
  }));
});
