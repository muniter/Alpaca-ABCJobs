import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormArray, Validators } from '@angular/forms';
import { CollegeDegree } from 'src/app/shared/CollegeDegree';
import { CandidateService } from '../candidate.service';
import SharedCustomValidators from 'src/app/shared/utils/shared-custom-validators';

@Component({
  selector: 'app-candidate-education',
  templateUrl: './candidate-education.component.html',
  styleUrls: ['./candidate-education.component.css']
})
export class CandidateEducationComponent implements OnInit {

  academicInformationForm!: FormGroup;
  collegeDegrees: CollegeDegree[] = [];
  careerStartSet: string[] = [];
  careerEndSet: string[] = [];
  minDate: Date;
  maxDate: Date;
  academicInfoDisabled: boolean = true;

  constructor(
    private formBuilder: FormBuilder,
    private candidateService: CandidateService
  ) { 
    const now = new Date().getTime();
    this.minDate = new Date(now);
    this.minDate.setFullYear(this.minDate.getFullYear() - 100);
    this.maxDate = new Date(now);
    this.maxDate.setFullYear(this.maxDate.getFullYear());
  }

  ngOnInit() {
    // temporal
    this.collegeDegrees =  [
      new CollegeDegree("Pregrado"),
      new CollegeDegree("Postgrado"),
      new CollegeDegree("Certificación"),
      new CollegeDegree("Capacitación"),
    ];
    // original
    /* this.collegeDegrees = []
    this.candidateService.getCollegeDegrees().subscribe({
      next: (response) => this.collegeDegrees = response.data
    }) */
    
    for(let year = this.minDate.getFullYear(); year <= this.maxDate.getFullYear(); year++) {
      this.careerStartSet.push(year.toString());
      this.careerEndSet.push(year.toString());
    }
    this.careerEndSet.push($localize`:@@careerendinprogress:En curso`);
    
    this.academicInformationForm = this.formBuilder.group(
      {
        careers: this.formBuilder.array([], Validators.required)
      }
    );

    this.disableForm();

  }
  
  get careers() {
    return this.academicInformationForm.get('careers') as FormArray;
  }

  addCareer() {
    this.careers.push(
      this.formBuilder.group(
        {
          collegeDegree: ["",[Validators.required]],
          careerTitle: ["", [Validators.required, Validators.minLength(2), Validators.maxLength(100),  
                      SharedCustomValidators.spaceOnlyValidator]],
          school: ["", [Validators.required, Validators.minLength(5), Validators.maxLength(255), 
                      SharedCustomValidators.spaceOnlyValidator]],
          careerStart: ["",[Validators.required]],
          careerEnd: ["",[]],
          achievement: ["", [Validators.minLength(10), Validators.maxLength(255),  
                           SharedCustomValidators.spaceOnlyValidator]]
        }
      )
    ); 

    if(this.academicInfoDisabled) {
      this.disableForm();
    }
  }

  getErrorMessage(field: String, index: number) {
    switch(field) {
      case "collegeDegree": {
        return (this.careers.at(index).get('collegeDegree')!.hasError('required'))
               ? $localize`:@@nonemptycollegedegree:Debe seleccionar un nivel académico`
               : this.careers.at(index).get('collegeDegree')!.hasError('responseMessageError')
                 ? $localize`:@@responseerrorcollegedegree:Revise el campo de nivel académico: 
                              ${this.careers.at(index).get('collegeDegree')?.getError('responseMessageError')}`
                 : "";
      }
      case "careerTitle": {
        return (this.careers.at(index).get('careerTitle')!.hasError('required') || 
        this.careers.at(index).get('careerTitle')!.hasError('isOnlyWhiteSpace'))
               ? $localize`:@@nonemptycareer:El título obtenido no puede ser vacío`
               : (this.careers.at(index).get('careerTitle')!.hasError('minlength') ||
                  this.careers.at(index).get('careerTitle')!.hasError('maxlength'))
                  ? $localize`:@@invalidlengthcareer:El título obtenido debe tener entre 2 y 100 caracteres`
                 : this.careers.at(index).get('careerTitle')!.hasError('responseMessageError')
                 ? $localize`:@@responseerrorcareer:Revise el campo título obtenido: 
                                 ${this.careers.at(index).get('careerTitle')?.getError('responseMessageError')}`
                                 : "";
                                }
      case "school": {
        return (this.careers.at(index).get('school')!.hasError('required') || 
        this.careers.at(index).get('school')!.hasError('isOnlyWhiteSpace'))
        ? $localize`:@@nonemptyschool:La institución educativa no puede ser vacía`
        : (this.careers.at(index).get('school')!.hasError('minlength') ||
        this.careers.at(index).get('school')!.hasError('maxlength'))
        ? $localize`:@@invalidlengthschool:El nombre de la institución debe tener entre 5 y 255 caracteres`
        : this.careers.at(index).get('school')!.hasError('responseMessageError')
        ? $localize`:@@responseerrorschool:Revise el campo institución educativa: 
        ${this.careers.at(index).get('school')?.getError('responseMessageError')}`
        : "";
      }
      case "careerStart": {
        return (this.careers.at(index).get('careerStart')!.hasError('required'))
        ? $localize`:@@nonemptycareerstart:Debe seleccionar un año`
        : this.careers.at(index).get('careerStart')!.hasError('responseMessageError')
                 ? $localize`:@@responseerrorcareerstart:Revise el campo año de inicio: 
                 ${this.careers.at(index).get('careerStart')?.getError('responseMessageError')}`
                 : "";
      }
      case "careerEnd": {
        return this.careers.at(index).get('careerEnd')!.hasError('responseMessageError')
               ? $localize`:@@responseerrorcareerend:Revise el campo año de finalización: 
               ${this.careers.at(index).get('careerEnd')?.getError('responseMessageError')}`
               : "";
      }
      case "achievement": {
        return this.careers.at(index).get('achievement')!.hasError('isOnlyWhiteSpace')
               ? $localize`:@@nonemptyachievement:Los logros no puede ser solo espacios en blanco`
               : (this.careers.at(index).get('achievement')!.hasError('minlength') ||
                  this.careers.at(index).get('achievement')!.hasError('maxlength'))
                 ? $localize`:@@invalidlengthachievement:Los logros puede ser vacio o deben tener entre 8 y 255 caracteres`
                 : this.careers.at(index).get('achievement')!.hasError('responseMessageError')
                   ? $localize`:@@responseerrorachievement:Revise el campo logros: 
                   ${this.careers.at(index).get('achievement')?.getError('responseMessageError')}`
                   : "";
      }
      default: {
        return "";
      }
    }
  }
  
  saveAcademicInfo() {
    this.disableForm();
  }

  enableForm() {
    this.academicInfoDisabled = false;
    this.academicInformationForm.enable();
  }

  disableForm() {
    this.academicInfoDisabled = true;
    this.academicInformationForm.disable()
  }
}
