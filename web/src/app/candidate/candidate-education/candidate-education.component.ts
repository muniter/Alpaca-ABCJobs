import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormArray, Validators } from '@angular/forms';
import { CollegeDegree } from 'src/app/shared/CollegeDegree';
import { CandidateService } from '../candidate.service';
import SharedCustomValidators from 'src/app/shared/utils/shared-custom-validators';
import { Career, mapKeysCareer } from '../career';
import { ActivatedRoute, Router } from '@angular/router';
import { AppRoutesEnum } from 'src/app/core/enums/AppRoutes.enum';

@Component({
  selector: 'app-candidate-education',
  templateUrl: './candidate-education.component.html',
  styleUrls: ['./candidate-education.component.css']
})
export class CandidateEducationComponent implements OnInit {

  academicInformationForm!: FormGroup;
  collegeDegrees: CollegeDegree[] = [];
  careerStartSet: number[] = [];
  careerEndSet: number[] = [];
  minDate: Date;
  maxDate: Date;
  academicInfoDisabled: boolean = false;
  careersInfo: Career[] = []; 
  token: string;
  deleteCareers: number[] = [];
  globalError!: string;
  globalMessage!: string;
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

  getCollegeDegrees() {
    this.collegeDegrees = []
    this.candidateService.getCollegeDegrees().subscribe({
      next: (response) => {
        this.collegeDegrees = response.data;
        this.getCareersInfo();
      }
    })
  }

  getCareersInfo() {
    this.careersInfo = [];
    this.candidateService.getCareersInfo(this.token).subscribe({
      next: (response) => {
        response.data.forEach(careerService => {
          let collegeDegreeIndex = this.collegeDegrees.findIndex(
            item => { 
              return item.id == (typeof careerService.type === 'number' 
                                ? careerService.type : careerService.type.id) 
            }
          );
          let careerRow = new Career(
            careerService.id,
            this.collegeDegrees[collegeDegreeIndex],            
            careerService.title,
            careerService.institution,
            careerService.start_year,
            careerService.end_year,
            careerService.achievement!
          );
          this.careersInfo.push(careerRow);
        });
        this.careersInfo.forEach(career => this.addCareer());
        this.careers.setValue(this.careersInfo);
        this.disableForm();
      }
    })

  }

  ngOnInit() {
    
    for(let year = this.maxDate.getFullYear(); year >= this.minDate.getFullYear(); year--) {
      this.careerStartSet.push(year);
      this.careerEndSet.push(year);
    }
    
    this.academicInformationForm = this.formBuilder.group(
      {
        careers: this.formBuilder.array([])
      }
      );
      
    this.getCollegeDegrees();
  }
  
  get careers() {
    return this.academicInformationForm.get('careers') as FormArray;
  }

  addCareer() {
    this.careers.push(
      this.formBuilder.group(
        {
          id: [],
          collegeDegree: ["",[Validators.required]],
          careerTitle: ["", [Validators.required, Validators.minLength(2), Validators.maxLength(100),  
                      SharedCustomValidators.spaceOnlyValidator]],
          school: ["", [Validators.required, Validators.minLength(2), Validators.maxLength(255), 
                      SharedCustomValidators.spaceOnlyValidator]],
          careerStart: ["",[Validators.required]],
          careerEnd: ["",[]],
          achievement: ["", [Validators.minLength(10), Validators.maxLength(255),  
                           SharedCustomValidators.spaceOnlyValidator]]
        },
        {
          validator: SharedCustomValidators.greaterThanValidator('careerEnd', 'careerStart', true)
        }
      )
    ); 

  }

  deleteCareer(item: number) {
    if(this.careers.at(item).get('id')?.value) {
      this.deleteCareers.push(this.careers.at(item).get('id')?.value);
    }
    this.careers.removeAt(item);
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
        ? $localize`:@@invalidlengthschool:El nombre de la institución debe tener entre 2 y 255 caracteres`
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
        return this.careers.at(index).get('careerEnd')!.hasError('greaterThan')
               ? $localize`:@@nongreaterthancareerend:El año final debe ser mayor o igual al inicial`
               : this.careers.at(index).get('careerEnd')!.hasError('responseMessageError')
                 ? $localize`:@@responseerrorcareerend:Revise el campo año de finalización: 
                 ${this.careers.at(index).get('careerEnd')?.getError('responseMessageError')}`
                 : "";
      }
      case "achievement": {
        return this.careers.at(index).get('achievement')!.hasError('isOnlyWhiteSpace')
               ? $localize`:@@nonemptyachievement:Los logros no puede ser solo espacios en blanco`
               : (this.careers.at(index).get('achievement')!.hasError('minlength') ||
                  this.careers.at(index).get('achievement')!.hasError('maxlength'))
                 ? $localize`:@@invalidlengthachievement:Los logros puede ser vacio o deben tener entre 10 y 255 caracteres`
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

  setErrorBack(index: number, exception: any) {
    if (exception.error?.errors !== undefined) {
      Object.entries(exception.error.errors).forEach(([key, value]) => {
        if(key != "global") {
          this.careers.at(index).get(mapKeysCareer[key])!.setErrors({ "responseMessageError": value });
        } else {
          this.globalError += ", " + $localize`:@@globalmessage:Error: ${value}`
        }
      });
    } else {
      console.log("Exception response sin array de errores");
    }
  }
  
  saveAcademicInfo() {
    this.taskCount = 0;
    this.deleteCareers.forEach(id => {
      this.taskCount++;
      this.candidateService.deleteCareerInfo(id, this.token).subscribe({
        error: (exception) => { 
          this.globalError += ", " + $localize`:@@errordeletecareer:Error al eliminar el registro`;
          setTimeout(() => { this.globalError = "" }, 3000);
        },
        complete: () => { 
          this.globalMessage += ", " + $localize`:@@okdeletecareer:Registro eliminado (${id})`;
          setTimeout(() => { this.globalMessage = "" }, 3000);
          this.taskCount--;
          this.cancelOrReload();
        }
      });
    });

    this.deleteCareers = [];

    this.academicInformationForm.value.careers.forEach((career: Career, index: number) => {
      this.taskCount++;
      if(career.id) {
        this.candidateService.updateCareerInfo(career, this.token).subscribe({
          error: (exception) => this.setErrorBack(index, exception),
          complete: () => { 
            this.globalMessage += ", " + $localize`:@@okupdatecareer:Registro actualizado (${career.id})` 
            setTimeout(() => { this.globalMessage = "" }, 3000);
            this.taskCount--;
            this.cancelOrReload();
          }
        });
      } else {
        this.candidateService.addCareerInfo(career, this.token).subscribe({
          error: (exception) => this.setErrorBack(index, exception),
          complete: () => { 
            this.globalMessage += ", " + $localize`:@@okaddcareer:Registro ingresado`;
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

  enableForm() {
    this.academicInfoDisabled = false;
    this.academicInformationForm.enable();
  }

  disableForm() {
    this.academicInfoDisabled = true;
    this.academicInformationForm.disable()
  }

  cancelOrReload() {
    if(this.taskCount == 0) {
      this.deleteCareers = [];
      this.disableForm();
      this.careers.clear();
      this.getCareersInfo();
    }
  }
}
