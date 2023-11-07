import { COMMA, ENTER } from '@angular/cdk/keycodes';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormArray, Validators } from '@angular/forms';
import { CandidateService } from '../candidate.service';
import SharedCustomValidators from 'src/app/shared/utils/shared-custom-validators';
import { Observable, map, startWith } from 'rxjs';
import { ActivatedRoute, Router } from '@angular/router';
import { AppRoutesEnum } from 'src/app/core/enums/AppRoutes.enum';
import { Job, JobServiceSchema, mapKeysJob } from '../job';
import { Skill } from 'src/app/shared/skill';
import { MatChipInputEvent } from '@angular/material/chips';
import { MatAutocompleteSelectedEvent } from '@angular/material/autocomplete';

@Component({
  selector: 'app-candidate-work',
  templateUrl: './candidate-work.component.html',
  styleUrls: ['./candidate-work.component.css']
})
export class CandidateWorkComponent implements OnInit {

  workInformationForm!: FormGroup;
  objectSkills!: Skill[];
  skills!: string[];
  filteredSkills!: Observable<string[]>;
  selectedSkills: [string[]] = [[]];
  jobStartSet: number[] = [];
  jobEndSet: number[] = [];
  minDate: Date;
  maxDate: Date;
  workInfoDisabled: boolean = false;
  jobsInfo: Job[] = []; 
  token: string;
  deleteJobs: number[] = [];
  globalError!: string;
  globalMessage!: string;
  separatorKeysCodes: number[] = [ENTER, COMMA];
  taskCount: number = 0;

  constructor(
    private formBuilder: FormBuilder,
    private activatedRouter: ActivatedRoute,
    private router: Router,
    private candidateService: CandidateService
  ) { 
    this.token = ""
    if (!this.activatedRouter.snapshot.params['userToken']) {
      this.router.navigateByUrl(`${AppRoutesEnum.candidate}/${AppRoutesEnum.candidateLogin}`)
    } else {
      this.token = this.activatedRouter.snapshot.params['userToken'];
    }

    const now = new Date().getTime();
    this.minDate = new Date(now);
    this.minDate.setFullYear(this.minDate.getFullYear() - 100);
    this.maxDate = new Date(now);
    this.maxDate.setFullYear(this.maxDate.getFullYear());
  }

  getSkills() {
    this.skills = []
    this.objectSkills = []
    this.candidateService.getSkills().subscribe({
      next: (response) => {
        this.objectSkills = response.data
        this.skills = this.objectSkills.map(skill => skill.name)
        this.getJobsInfo();
      }
    })
  }

  getJobsInfo() {
    this.jobsInfo = [];
    this.candidateService.getJobsInfo(this.token).subscribe({
      next: (response) => {
        response.data.forEach(jobService => {          
          let jobRow = new Job(
            jobService.id,
            jobService.role,
            jobService.company,
            jobService.description,
            jobService.skills,
            jobService.start_year,
            jobService.end_year
          );
          this.jobsInfo.push(jobRow);
        });

        this.jobsInfo.forEach((job, index) => { 
          this.addJob(job.skills, index);
        });
        this.jobs.setValue(this.jobsInfo);
        this.disableForm();
      }
    })

  }

  ngOnInit() {

    for(let year = this.maxDate.getFullYear(); year >= this.minDate.getFullYear(); year--) {
      this.jobStartSet.push(year);
      this.jobEndSet.push(year);
    }

    this.workInformationForm = this.formBuilder.group(
      {
        jobs: this.formBuilder.array([])
      }
      );

      this.getSkills();
  }

  get jobs() {
    return this.workInformationForm.get('jobs') as FormArray;
  }

  addJob(skills: Skill[] = [], index: number | null = null) {
    this.jobs.push(
      this.formBuilder.group(
        {
          id: [],
          role: ["", [Validators.required, Validators.minLength(2), Validators.maxLength(255),  
                      SharedCustomValidators.spaceOnlyValidator]],
          company: ["", [Validators.required, Validators.minLength(2), Validators.maxLength(255), 
                      SharedCustomValidators.spaceOnlyValidator]],
          description: ["", [Validators.required, Validators.minLength(2), Validators.maxLength(500),  
                            SharedCustomValidators.spaceOnlyValidator]],
          skills: [, []],
          jobStart: ["",[Validators.required]],
          jobEnd: ["",[]],
        },
        {
          validator: SharedCustomValidators.greaterThanValidator('jobEnd', 'jobStart', true)
        }
      )
    ); 

    this.selectedSkills.push(skills.map((skill) => skill.name));
    if(index == 0) {
      this.selectedSkills.splice(0,1);
    }

    this.filteredSkills = this.jobs.at(-1).get('skills')!.valueChanges.pipe(
      startWith(null),
      map((skill: Skill) => (skill ? this._filter(skill.name) : this.skills.slice())),
    );

  }
  
  deleteJob(item: number) {
    if(this.jobs.at(item).get('id')?.value) {
      this.deleteJobs.push(this.jobs.at(item).get('id')?.value);
    }
    this.jobs.removeAt(item);
    this.selectedSkills.splice(item,1);
  }

  getErrorMessage(field: String, index: number) {
    switch(field) {
      case "role": {
        return (this.jobs.at(index).get('role')!.hasError('required') || 
                this.jobs.at(index).get('role')!.hasError('isOnlyWhiteSpace'))
                ? $localize`:@@nonemptyjob:El cargo no puede ser vacío`
                : (this.jobs.at(index).get('role')!.hasError('minlength') ||
                  this.jobs.at(index).get('role')!.hasError('maxlength'))
                  ? $localize`:@@invalidlengthjob:El cargo debe tener entre 2 y 255 caracteres`
                  : this.jobs.at(index).get('role')!.hasError('responseMessageError')
                  ? $localize`:@@responseerrorjob:Revise el campo cargo: 
                    ${this.jobs.at(index).get('role')?.getError('responseMessageError')}`
                    : "";
      }
      case "company": {
        return (this.jobs.at(index).get('company')!.hasError('required') || 
                this.jobs.at(index).get('company')!.hasError('isOnlyWhiteSpace'))
                ? $localize`:@@nonemptycompany:La compañia no puede ser vacío`
                : (this.jobs.at(index).get('company')!.hasError('minlength') ||
                  this.jobs.at(index).get('company')!.hasError('maxlength'))
                  ? $localize`:@@invalidlengthcompany:La compañia debe tener entre 2 y 255 caracteres`
                  : this.jobs.at(index).get('company')!.hasError('responseMessageError')
                  ? $localize`:@@responseerrorcompany:Revise el campo compañia: 
                    ${this.jobs.at(index).get('company')?.getError('responseMessageError')}`
                    : "";
      }
      case "jobStart": {
        return (this.jobs.at(index).get('jobStart')!.hasError('required'))
        ? $localize`:@@nonemptyjobstart:Debe seleccionar un año`
        : this.jobs.at(index).get('jobStart')!.hasError('responseMessageError')
                 ? $localize`:@@responseerrorjobstart:Revise el campo año de inicio: 
                 ${this.jobs.at(index).get('jobStart')?.getError('responseMessageError')}`
                 : "";
      }
      case "jobEnd": {
        return this.jobs.at(index).get('jobEnd')!.hasError('greaterThan')
               ? $localize`:@@nongreaterthanjobend:El año final debe ser mayor o igual al inicial`
               : this.jobs.at(index).get('jobEnd')!.hasError('responseMessageError')
                 ? $localize`:@@responseerrorjobend:Revise el campo año de finalización: 
                 ${this.jobs.at(index).get('jobEnd')?.getError('responseMessageError')}`
                 : "";
      }
      case "description": {
        return (this.jobs.at(index).get('description')!.hasError('required') || 
                this.jobs.at(index).get('description')!.hasError('isOnlyWhiteSpace'))
               ? $localize`:@@nonemptdescription:La descripción no puede ser solo espacios en blanco`
               : (this.jobs.at(index).get('description')!.hasError('minlength') ||
                  this.jobs.at(index).get('description')!.hasError('maxlength'))
                 ? $localize`:@@invalidlengthdescription:La descripción debe tener entre 2 y 500 caracteres`
                 : this.jobs.at(index).get('description')!.hasError('responseMessageError')
                   ? $localize`:@@responseerrordescription:Revise el campo logros: 
                   ${this.jobs.at(index).get('description')?.getError('responseMessageError')}`
                   : "";
      }
      case "skills": {
        return ""
      }
      default: {
        return "";
      }
    }
  }

  setErrorBack(index: number, exception: any) {
    if (exception.error?.errors !== undefined) {
      Object.entries(exception.error.errors).forEach(([key, value]) => {
        if(key != "global") {
          this.jobs.at(index).get(mapKeysJob[key])!.setErrors({ "responseMessageError": value });
        } else {
          this.globalError += ", " + $localize`:@@globalmessage:Error: ${value}`
        }
      });
    } else {
      console.log("Exception response sin array de errores");
    }
  }
  
  saveWorkInfo() {

    this.taskCount = 0;    
    this.deleteJobs.forEach(id => {
      this.taskCount++;
      this.candidateService.deleteJobInfo(id, this.token).subscribe({
        error: (exception) => { 
          this.globalError += ", " + $localize`:@@errordeletejob:Error al eliminar el registro`;
          setTimeout(() => { this.globalError = "" }, 3000);
        },
        complete: () => { 
          this.globalMessage += ", " + $localize`:@@okdeletejob:Registro eliminado (${id})`;
          setTimeout(() => { this.globalMessage = "" }, 3000);
          this.taskCount--;
          this.cancelOrReload();
        }
      });
    });

    this.deleteJobs = [];

    this.workInformationForm.value.jobs.forEach((job: Job, index: number) => {
      this.taskCount++;
      let jobRequest = new JobServiceSchema(
        job.id,
        job.role,
        job.company,
        job.description,
        this.objectSkills.filter(x => this.selectedSkills.at(index)?.includes(x?.name))
                         .map((skill) => skill.id ),
        job.jobStart,
        job.jobEnd
      );

      if(job.id) {
        this.candidateService.updateJobInfo(jobRequest, this.token).subscribe({
          error: (exception) => this.setErrorBack(index, exception),
          complete: () => { 
            this.globalMessage += ", " + $localize`:@@okupdatejob:Registro actualizado (${job.id})` 
            setTimeout(() => { this.globalMessage = "" }, 3000);
            this.taskCount--;
            this.cancelOrReload();
          }
        });
      } else {
        this.candidateService.addJobInfo(jobRequest, this.token).subscribe({
          error: (exception) => this.setErrorBack(index, exception),
          complete: () => { 
            this.globalMessage += ", " + $localize`:@@okaddjob:Registro ingresado`;
            setTimeout(() => { this.globalMessage = "" }, 3000);
            this.taskCount--;
            this.cancelOrReload();
          }
        });
      }
    });

    if (this.taskCount == 0) {
      this.cancelOrReload();
    }
  }

  removeSkill(skill: string, item: number): void {
    const index = this.selectedSkills.at(item)!.indexOf(skill);

    if (index! >= 0) {
      this.selectedSkills.at(item)!.splice(index, 1);
    }
  }

  addSkill(event: MatChipInputEvent, item: number): void {
    const value = (event.value || '').trim();

    if (value) {
      this.selectedSkills.at(item)!.push(value);
    }

    event.chipInput!.clear();
  }

  selected(event: MatAutocompleteSelectedEvent, item: number): void {
    const index = this.selectedSkills.at(item)!.indexOf(event.option.viewValue);

    if (index < 0) {
      this.selectedSkills.at(item)!.push(event.option.viewValue);
      this.jobs.at(item).get('skills')!.setValue(null);
    }

  }

  enableForm() {
    this.workInfoDisabled = false;
    this.workInformationForm.enable();
  }

  disableForm() {
    this.workInfoDisabled = true;
    this.workInformationForm.disable()
  }

  private _filter(value: string): string[] {
    const filterValue = value ? value.toLowerCase() : '';

    return this.skills.filter(skill => skill.toLowerCase().includes(filterValue));
  }

  cancelOrReload() {
    if(this.taskCount == 0) {
      this.deleteJobs = [];
      this.disableForm();
      this.jobs.clear();
      this.selectedSkills = [[]];
      this.getJobsInfo();
    }
  }
}
