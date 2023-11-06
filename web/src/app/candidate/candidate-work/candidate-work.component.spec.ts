/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { CandidateWorkComponent } from './candidate-work.component';
import { HttpClientModule } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { SharedModule } from 'src/app/shared/shared.module';
import { RouterTestingModule } from '@angular/router/testing';
import { FormArray, FormGroup, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { DatePipe } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { MatChipsModule } from '@angular/material/chips';
import { MatAutocompleteModule, MatAutocompleteSelectedEvent } from '@angular/material/autocomplete';
import { CandidateService } from '../candidate.service';
import { of } from 'rxjs';

describe('CandidateWorkComponent', () => {
  let component: CandidateWorkComponent;
  let fixture: ComponentFixture<CandidateWorkComponent>;
  let router: Router;
  let mockCandidateService: jasmine.SpyObj<CandidateService>;


  function spyJobsInfo() {
    spyOn(mockCandidateService, 'getJobsInfo').and.returnValue(of({
      success: true,
      data: [{
        id: 1,
        role: 'Software Developer',
        company: 'Tech Solutions',
        description: 'Responsible for developing and maintaining web applications.',
        start_year: 1995,
        end_year: 1996,
        skills: [{
          id: 1,
          name: 'Java',
        }],
      }]
    })); // Mock the addJobInfo method if it makes HTTP requests
  }


  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule, // Use this instead of HttpClientModule
        SharedModule,
        RouterTestingModule,
        FormsModule,
        ReactiveFormsModule,
        MatFormFieldModule,
        MatSelectModule,
        MatInputModule,
        MatChipsModule,
        MatAutocompleteModule,
        MatIconModule,
        MatDividerModule,
        BrowserAnimationsModule
      ],
      declarations: [CandidateWorkComponent],
      providers: [
        DatePipe,
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { params: { 'userToken': '123' } } }
        },
        CandidateService, // Ensure CandidateService is provided here if it's not included in SharedModule or any other imported module
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CandidateWorkComponent);
    component = fixture.componentInstance;
    mockCandidateService = TestBed.inject(CandidateService) as jasmine.SpyObj<CandidateService>;
    spyOn(mockCandidateService, 'addJobInfo').and.returnValue(of({})); // Mock the addJobInfo method if it makes HTTP requests
    spyOn(mockCandidateService, 'deleteJobInfo').and.returnValue(of({})); // Mock the addJobInfo method if it makes HTTP requests
    spyOn(mockCandidateService, 'updateJobInfo').and.returnValue(of({})); // Mock the addJobInfo method if it makes HTTP requests
    spyOn(mockCandidateService, 'getSkills').and.returnValue(of({
      success: true,
      data: [{
        id: 1,
        name: 'Java',
      },
      {
        "id": 3,
        "name": "Product Manager"
      },
      {
        "id": 4,
        "name": "UX Designer"
      },
      {
        "id": 5,
        "name": "Network Administrator"
      },
      {
        "id": 6,
        "name": "Cybersecurity Analyst"
      },
      ]
    }));
    component.ngOnInit();
    fixture.detectChanges();
    router = TestBed.inject(Router);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('token should work', () => {
    expect(component.token == '123').toBeTruthy();
  });

  it('form enabling should display the elements', () => {
    const labels = [
      'role',
      'periodo',
      'descripcion',
    ];
    const debug = fixture.debugElement;
    for (const label of labels) {
      expect(debug.query(By.css(`.${label}`))).toBeFalsy();
    }
    const addButtons = debug.query(By.css('.add-button'));
    addButtons.triggerEventHandler('click', null);
    // Form field with label "Academic Level" should be enabled
    fixture.detectChanges();
    for (const label of labels) {
      expect(debug.query(By.css(`.${label}`))).toBeTruthy();
    }
  });

  it('form validates required fields', () => {
    component.enableForm();
    fixture.detectChanges(); // Ensure the view updates to reflect the enabled form

    // Add a job form group to test against
    component.addJob();
    fixture.detectChanges();

    const form = component.workInformationForm;
    const jobsFormArray = form.get('jobs') as FormArray;
    const jobGroup = jobsFormArray.at(0) as FormGroup; // Assuming we're testing the first job group

    // Typo in the original 'descripcion' should be 'description' as per the component code
    const required = [
      'role',
      'company',
      'description', // Corrected field name
      'jobStart',
    ];

    required.forEach(field => {
      const control = jobGroup.get(field);
      if (control) {
        control.setValue(''); // Set empty value to trigger required validation
        expect(control.valid).toBeFalse(); // The control should be invalid because it's required
        expect(control.hasError('required')).toBeTrue(); // The specific error should be 'required'
      } else {
        expect(control).toBeTruthy(); // The control should exist
      }
    });
  });

  it('should call addJobInfo when submitting form', () => {
    // Fill out the form with some test values.
    // Make sure the values match the form control names and the data types expected.
    component.enableForm();
    component.addJob();
    fixture.detectChanges();

    component.workInformationForm.setValue({
      jobs: [
        {
          id: null,
          role: 'Software Developer',
          company: 'Tech Solutions',
          description: 'Responsible for developing and maintaining web applications.',
          jobStart: '1995',
          jobEnd: '1996',
          skills: [1],
        }
      ]
    });

    component.saveWorkInfo();
    expect(mockCandidateService.addJobInfo).toHaveBeenCalled();
  });

  it('should call deleteJobInfo when deleting a job', () => {
    spyJobsInfo();
    component.enableForm();
    component.ngOnInit();
    fixture.detectChanges();
    component.deleteJob(0);
    component.saveWorkInfo();
    expect(mockCandidateService.deleteJobInfo).toHaveBeenCalled();
  });

  it('loads the existing jobs', () => {
    spyJobsInfo();

    component.ngOnInit();
    component.enableForm();
    fixture.detectChanges();
    expect(mockCandidateService.getJobsInfo).toHaveBeenCalled();
    expect(component.jobsInfo.length).toBe(1);

    // Fin th existing job in the html
    const debug = fixture.debugElement;
    // Find the word Software Developer in the html
    const element = debug.query(By.css('#role-0'));
    expect(element.nativeElement.value).toContain('Software Developer');
  });

  it('updates existing jobs', () => {
    spyJobsInfo();
    component.ngOnInit();
    component.saveWorkInfo();
    expect(mockCandidateService.updateJobInfo).toHaveBeenCalled();
  });

  it('remove skill', () => {
    spyJobsInfo();
    component.ngOnInit();
    component.enableForm();
    fixture.detectChanges();
    expect(component.selectedSkills[0]).toContain('Java');
    component.removeSkill('Java', 0);
    expect(component.selectedSkills[0]).not.toContain('Java');
    // expect(component.selectedSkills[0]).toBeUndefined();
  });

  it('add skill', () => {
    spyJobsInfo();
    component.ngOnInit();
    component.enableForm();
    fixture.detectChanges();
    component.addSkill({ value: 'Python', chipInput: { clear() { } } } as any, 0);
    expect(component.selectedSkills[0]).toContain('Python');
  });

});
