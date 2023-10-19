import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import SharedCustomValidators from "../../shared/utils/shared-custom-validators"
import { CompanyLoginRequest } from '../company';
import { CompanyService } from '../company.service';
import { Router } from '@angular/router';
import { AppRoutesEnum } from 'src/app/core/enums';

@Component({
  selector: 'app-company-login',
  templateUrl: './company-login.component.html',
  styleUrls: ['./company-login.component.css']
})
export class CompanyLoginComponent implements OnInit {

  companyLoginForm!: FormGroup;

  constructor(private formBuilder: FormBuilder, private companyService: CompanyService, private router: Router) { }

  ngOnInit() {
    this.companyLoginForm = this.formBuilder.group({
      companyEmail: ["", [Validators.required, Validators.email, SharedCustomValidators.spaceOnlyValidator]],
      password: ["", [Validators.required, SharedCustomValidators.spaceOnlyValidator]],
    });
  }

  getErrorMessage(field: String) {
    switch (field) {
      case "companyEmail": {
        return (this.companyLoginForm.get('companyEmail')!.hasError('required') ||
          this.companyLoginForm.get('companyEmail')!.hasError('isOnlyWhiteSpace')) ?
          $localize`:@@nonemptyemail:La dirección de correo electrónico no puede ser vacía` :
          this.companyLoginForm.get('companyEmail')!.hasError('email') ?
            $localize`:@@invalidemail:La dirección de correo electrónico ingresada es inválida` :
            "";
      }
      case "password": {
        return (this.companyLoginForm.get('password')!.hasError('required')) ?
          $localize`:@@nonemptypassword:La contraseña no puede ser vacía` :
          (this.companyLoginForm.get('password')!.hasError('isOnlyWhiteSpace')) ?
            $localize`:@@invalidpassword:La contraseña ingresada es inválida` :
            this.companyLoginForm.get('password')!.hasError('responseMessageError') ?
              $localize`:@@responseerrorcompanylogin:Falló el inicio de sesión, inténtelo de nuevo.` :
              "";
      }
      default: {
        return "";
      }
    }
  }

  setErrorBack(exception: any) {
    if (exception.error?.errors !== undefined) {
      Object.entries(exception.error.errors).forEach(([key, value]) => {
        this.companyLoginForm.controls['password'].setErrors({ "responseMessageError": value });
      });

    } else {
      console.log("Exception response sin array de errores");
    }
  }

  loginCompany() {
    let loginData = new CompanyLoginRequest(this.companyLoginForm.get('companyEmail')?.value,
      this.companyLoginForm.get('password')?.value);

    this.companyService.companyLogin(loginData).subscribe({
      error: (exception) => this.setErrorBack(exception),
      complete: () => this.router.navigateByUrl(`${AppRoutesEnum.company}/${AppRoutesEnum.companyHome}`)
    })
  }
}
