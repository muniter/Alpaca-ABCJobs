import { Component, OnInit } from '@angular/core';
import { FormArray, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Skill } from 'src/app/shared/skill';
import { Tech, TechRequestRow, mapKeysTech } from '../tech';
import { ActivatedRoute, Router } from '@angular/router';
import { CandidateService } from '../candidate.service';
import { AppRoutesEnum } from 'src/app/core/enums';
import SharedCustomValidators from 'src/app/shared/utils/shared-custom-validators';

@Component({
  selector: 'app-candidate-skills',
  templateUrl: './candidate-skills.component.html',
  styleUrls: ['./candidate-skills.component.css']
})
export class CandidateSkillsComponent implements OnInit {

  technicalInformationForm!: FormGroup;
  skills: Skill[] = [];
  technicalInfoDisabled: boolean = false;
  technicalsInfo: Tech[] = [];
  token: string = "";
  globalError!: string;
  globalMessage!: string;

  constructor(
    private formBuilder: FormBuilder,
    private activatedRouter: ActivatedRoute,
    private router: Router,
    private candidateService: CandidateService
  ) { 
    this.validateToken(this.activatedRouter.snapshot.params['userToken']);
  }

  validateToken(token:string) {
    this.token = "";
    if (!token) {
      this.router.navigateByUrl(`${AppRoutesEnum.candidate}/${AppRoutesEnum.candidateLogin}`)
    } else {
      this.token = this.activatedRouter.snapshot.params['userToken'];
    }
  }

  getSkills() {
    this.skills = []
    this.candidateService.getTechnicalInfoTypes().subscribe({
      next: (response) => {
        this.skills = response.data;
        this.getTechnicalInfo();
      }
    })
  }

  getTechnicalInfo() {
    this.technicalsInfo = [];
    this.candidateService.getTechnicalInfo(this.token).subscribe({
      next: (response) => {
        response.data.forEach(techService => {
          let skillIndex = this.skills.findIndex(
            item => { 
              return item.id == (typeof techService.type === 'number' 
                                ? techService.type : techService.type.id) 
            }
          );
          let techRow = new Tech(
            techService.id,
            this.skills[skillIndex],
           /*  techService.raiting, */
            techService.description
          );
          this.technicalsInfo.push(techRow);
        });
        this.technicalsInfo.forEach(tech => this.addTech());
        this.knowledge.setValue(this.technicalsInfo);
        this.disableForm();
      }
    })

  }

  ngOnInit() {
    
    this.technicalInformationForm = this.formBuilder.group(
      {
        knowledge: this.formBuilder.array([])
      }
      );
      
    this.getSkills();
  }

  get knowledge() {
    return this.technicalInformationForm.get('knowledge') as FormArray;
  }

  addTech() {
    this.knowledge.push(
      this.formBuilder.group(
        {
          id: [],
          tech: ["",[Validators.required]],
          /* raiting: [], */
          moreInfoTech: ["", [Validators.maxLength(500), SharedCustomValidators.spaceOnlyValidator]]
        }
      )
    ); 
  }

  deleteTech(item: number) {
    this.knowledge.removeAt(item);
  }

  getErrorMessage(field: String, index: number) {
    switch(field) {
      case "tech": {
        return (this.knowledge.at(index).get('tech')!.hasError('required'))
               ? $localize`:@@nonemptytech:Debe seleccionar una tecnología`
               : this.knowledge.at(index).get('tech')!.hasError('responseMessageError')
                 ? $localize`:@@responseerrortech:Revise el campo de tecnología: 
                              ${this.knowledge.at(index).get('tech')?.getError('responseMessageError')}`
                 : "";
      }
      case "moreInfoTech": {
        return this.knowledge.at(index).get('moreInfoTech')!.hasError('isOnlyWhiteSpace')
               ? $localize`:@@nonemptymoreinfotech:Información adicional no puede ser solo espacios`
               : this.knowledge.at(index).get('moreInfoTech')!.hasError('maxlength')
                 ? $localize`:@@invalidlengthmoreinfotech:Información adicional puede tener hasta 500 caracteres`
                 : this.knowledge.at(index).get('moreInfoTech')!.hasError('responseMessageError')
                 ? $localize`:@@responseerrormoreinfotech:Revise el campo de información adicional: 
                                 ${this.knowledge.at(index).get('moreInfoTech')?.getError('responseMessageError')}`
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
          this.knowledge.at(index).get(mapKeysTech[key])!.setErrors({ "responseMessageError": value });
        } else {
          this.globalError += ", " + $localize`:@@globalmessage:Error: ${value}`
        }
      });
    } else {
      console.log("Exception response sin array de errores");
    }
  }
  
  saveTechnicalInfo() {
    let knowledges: TechRequestRow[] = [];
    this.technicalInformationForm.value.knowledge.forEach((techRow: Tech) => {
      knowledges.push(new TechRequestRow(techRow.tech.id, techRow.moreInfoTech));
    });

    this.candidateService.updateTechnicalInfo(knowledges, this.token).subscribe({
      error: (exception) => this.setErrorBack(0, exception),
      complete: () => { 
        this.globalMessage = $localize`:@@okupdatetechnicalinfo:Información técnica actualizada` 
        setTimeout(() => { this.globalMessage = "" }, 3000);
        this.cancelOrReload();
      }
    });
  }

  enableForm() {
    this.technicalInfoDisabled = false;
    this.technicalInformationForm.enable();
  }

  disableForm() {
    this.technicalInfoDisabled = true;
    this.technicalInformationForm.disable()
  }

  cancelOrReload() {
    this.disableForm();
    this.knowledge.clear();
    this.getTechnicalInfo();
  }


}
