import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Vacancy, VacancyRequest } from '../vacancy';
import { MAT_DIALOG_DATA, MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { AppRoutesEnum } from 'src/app/core/enums';
import { CompanyService } from '../company.service';

@Component({
  selector: 'app-company-search-preselect',
  templateUrl: './company-search-preselect.component.html',
  styleUrls: ['./company-search-preselect.component.css']
})
export class CompanySearchPreselectComponent implements OnInit {

  token: string = "";
  dialog!: MatDialog;
  theme: string;
  candidateId: number;
  preselectCandidateForm!: FormGroup;
  vacancies!: Vacancy[];
  globalError!: string;
  globalMessage!: string;

  constructor(
    private formBuilder: FormBuilder,
    private router: Router,
    private companyService: CompanyService,
    @Inject(MAT_DIALOG_DATA) public data: {token: string, dialog: MatDialog, theme: string, candidateId: number}
  ) { 
    this.token = data.token;
    this.dialog = data.dialog;
    this.theme = data.theme;
    this.candidateId = data.candidateId;
  }

  getVacancies() {
    this.vacancies = []
    this.companyService.getVacancies(this.token).subscribe({
      next: (response) => this.vacancies = response.data
    })
  }

  ngOnInit() {
    this.validateToken(this.token)
    this.getVacancies();

    this.preselectCandidateForm = this.formBuilder.group(
      {
        vacancy: ["", [Validators.required]]
      }
    )
  }

  validateToken(token:string|null) {
    this.token = "";
    if(!token || token == "") {
      this.dialog?.closeAll();
      this.router.navigateByUrl(`${AppRoutesEnum.company}/${AppRoutesEnum.companyLogin}`)
    } 
    this.token = token!;
  }

  getErrorMessage(field: String) {
    switch(field) {
      case "vacancy": {
        return this.preselectCandidateForm.get('vacancy')!.hasError('responseMessageError')
               ? $localize`:@@responseerrorvacancy:Mensaje: 
                 ${this.preselectCandidateForm.get('vacancy')?.getError('responseMessageError')}`
               : "";
      }
      default: {
        return "";
      }
    }
  }

  setErrorBack(exception: any) {
    if (exception.error?.errors !== undefined) {
      Object.entries(exception.error.errors).forEach(([key, value]) => {
        if(key != "global") {
          this.preselectCandidateForm.get("vacancy")!.setErrors({ "responseMessageError": value });
        } else {
          this.globalError = $localize`:@@globalmessage:Error: ${value}`
        }
      });
    } else {
      console.log("Exception response sin array de errores");
    }
  }

  cancel() {
    this.dialog.closeAll();
  }

  preselectCandidate() {
    const vacancyId = this.preselectCandidateForm.get('vacancy')?.value;
    const request = new VacancyRequest(this.candidateId);
    this.companyService.preselectCandidate(this.token, vacancyId, request).subscribe({
      error: (exception) => { 
        this.setErrorBack(exception);
      },
      complete: () => { 
        this.globalMessage = $localize`:@@okpreselectcandidate:Candidato preseleccionado satisfactoriamente`;
        setTimeout(() => { 
          this.globalMessage = "";
          this.dialog.closeAll();
        }, 3000);
      }
    });
  }

}
