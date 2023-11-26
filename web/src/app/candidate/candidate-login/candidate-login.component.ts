import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { CandidateService } from '../candidate.service';
import SharedCustomValidators from 'src/app/shared/utils/shared-custom-validators';
import { CandidateLoginRequest, mapKeys } from '../candidate';
import { Router } from '@angular/router';
import { AppRoutesEnum } from 'src/app/core/enums';
import { UserService } from 'src/app/user/user.service';

@Component({
  selector: 'app-candidate-login',
  templateUrl: './candidate-login.component.html',
  styleUrls: ['./candidate-login.component.css']
})
export class CandidateLoginComponent implements OnInit {

  candidateLoginForm!: FormGroup;

  constructor(
    private formBuilder: FormBuilder,
    private candidateService: CandidateService,
    private userService: UserService,
    private router: Router
  ) { }

  ngOnInit() {
    this.candidateLoginForm = this.formBuilder.group({
        email: ["", [Validators.required, Validators.email, SharedCustomValidators.spaceOnlyValidator]],
        password: ["", [Validators.required, SharedCustomValidators.spaceOnlyValidator]]
    });
  }

  getErrorMessage(field: String) {
    switch(field) {
      case "email": {
        return (this.candidateLoginForm.get('email')!.hasError('required') || 
                this.candidateLoginForm.get('email')!.hasError('isOnlyWhiteSpace'))
               ? $localize`:@@nonemptyemail:La dirección de correo electrónico no puede ser vacía`
               : this.candidateLoginForm.get('email')!.hasError('email') 
                 ? $localize`:@@invalidemail:La dirección de correo electrónico ingresada es inválida`
                 : this.candidateLoginForm.get('email')!.hasError('responseMessageError')
                   ? $localize`:@@responseerrorcandidatelogin: inicio de sesión fallido: 
                                 ${this.candidateLoginForm.get('email')?.getError('responseMessageError')}`
                   : "";
      }
      case "password": {
        return this.candidateLoginForm.get('password')!.hasError('required')
               ? $localize`:@@nonemptypassword:La contraseña no puede ser vacía`
               : this.candidateLoginForm.get('password')!.hasError('isOnlyWhiteSpace')
                 ? $localize`:@@invalidpassword:La contraseña ingresada es inválida`
                 : this.candidateLoginForm.get('password')!.hasError('responseMessageError')
                   ? $localize`:@@responseerrorcandidatelogin: inicio de sesión fallido: 
                                 ${this.candidateLoginForm.get('password')?.getError('responseMessageError')}`
                   : "";
      }
      default: {
        return "";
      }
    }
  }

  setErrorBack(exception:any) {
    if(exception.error?.errors !== undefined) {
      Object.entries(exception.error.errors).forEach(([key, value]) => {
        this.candidateLoginForm.controls[mapKeys[key]].setErrors({ "responseMessageError": value});
      });      
    } else {
      console.log("Exception response sin array de errores");
    }
  }

  candidateLogin(candidate: CandidateLoginRequest) {
    this.candidateService
      .login(candidate)
      .subscribe({ 
        next: (response) => {
            this.userService
              .getConfig(response.data.token)
              .subscribe({
                next: (userSettings) => {
                  localStorage.setItem('dateFormat', userSettings.data.config.dateFormat)
                },
              });
          return this.router.navigateByUrl(
            `${AppRoutesEnum.candidate}/${AppRoutesEnum.candidateHome}/${response.data.token}`);
        },
        error: (exception) => this.setErrorBack(exception)
      })
  }
}
