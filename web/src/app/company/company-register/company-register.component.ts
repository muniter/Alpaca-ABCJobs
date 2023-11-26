import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, NgForm, Validators } from '@angular/forms';
import SharedCustomValidators from 'src/app/shared/utils/shared-custom-validators';
import { CompanyRegisterRequest } from '../company';
import { CompanyService } from '../company.service';
import { AppRoutesEnum } from 'src/app/core/enums';
import { Router } from '@angular/router';

@Component({
  selector: 'app-company-register',
  templateUrl: './company-register.component.html',
  styleUrls: ['./company-register.component.css']
})
export class CompanyRegisterComponent implements OnInit {

  private mapKeys: { [index: string]: string } = {
    "nombre": "companyName",
    "email": "companyEmail",
    "password": "password"
  };

  companyRegisterForm!: FormGroup;
  registerSucess: boolean = false;

  constructor(private formBuilder: FormBuilder, private companyService: CompanyService, private router: Router) {
  }

  ngOnInit() {
    this.companyRegisterForm = this.formBuilder.group({
      companyName: ["", [Validators.required, Validators.minLength(2), Validators.maxLength(255), SharedCustomValidators.spaceOnlyValidator]],
      companyEmail: ["", [Validators.required, Validators.minLength(2), Validators.maxLength(255), SharedCustomValidators.spaceOnlyValidator, Validators.email]],
      password: ["", [Validators.required, Validators.minLength(8), Validators.maxLength(20), SharedCustomValidators.spaceOnlyValidator]],
      passwordConfirm: ["", [Validators.required]],
      termsCheck: [false, [Validators.requiredTrue]],
      termsCheck2: ["", [Validators.required]],
    },
      {
        validator: SharedCustomValidators.ConfirmedPassValidator('password', 'passwordConfirm')
      })
  }

  MarkTouchedAux() {
    this.companyRegisterForm.controls["termsCheck2"].markAsTouched();

    if (!this.companyRegisterForm.controls["termsCheck"].value) {
      this.companyRegisterForm.controls["termsCheck2"].setErrors({ required: true });
    }
    else {
      this.companyRegisterForm.controls["termsCheck2"].setErrors(null);
    }
  }

  getErrorMessage(field: String) {
    switch (field) {
      case "companyName": {
        return (this.companyRegisterForm.get('companyName')!.hasError('required') ||
          this.companyRegisterForm.get('companyName')!.hasError('isOnlyWhiteSpace')) ?
          $localize`:@@nonemptycompanyname:El nombre de la empresa no puede ser vacío` :
          (this.companyRegisterForm.get('companyName')!.hasError('minlength') ||
            this.companyRegisterForm.get('companyName')!.hasError('maxlength')) ?
            $localize`:@@invalidlengthcompanyname:El nombre de la empresa debe tener entre 2 y 255 caracteres` :
            this.companyRegisterForm.get('companyName')!.hasError('responseMessageError') ?
              $localize`:@@responseerrorcompanyname:Error en el registro: ${this.companyRegisterForm.get('companyName')?.getError('responseMessageError')}` :
            "";
      }
      case "companyEmail": {
        return (this.companyRegisterForm.get('companyEmail')!.hasError('required') ||
          this.companyRegisterForm.get('companyEmail')!.hasError('isOnlyWhiteSpace')) ?
          $localize`:@@nonemptyemail:La dirección de correo electrónico no puede ser vacía` :
          (this.companyRegisterForm.get('companyEmail')!.hasError('minlength') ||
            this.companyRegisterForm.get('companyEmail')!.hasError('maxlength')) ?
            $localize`:@@invalidlengthemail:La dirección de correo electrónico debe tener entre 2 y 255 caracteres` :
            this.companyRegisterForm.get('companyEmail')!.hasError('email') ?
              $localize`:@@invalidemail:La dirección de correo electrónico ingresada es inválida` :
              this.companyRegisterForm.get('companyEmail')!.hasError('responseMessageError') ?
                $localize`:@@responseerrorcompanyemail:Error en el registro: ${this.companyRegisterForm.get('companyEmail')?.getError('responseMessageError')}` :
              "";
      }
      case "password": {
        return (this.companyRegisterForm.get('password')!.hasError('required')) ?
          $localize`:@@nonemptypassword:La contraseña no puede ser vacía` :
          (this.companyRegisterForm.get('password')!.hasError('minlength') ||
            this.companyRegisterForm.get('password')!.hasError('maxlength')) ?
            $localize`:@@invalidlengthpassword:La contraseña debe tener entre 8 y 20 caracteres` :
            (this.companyRegisterForm.get('password')!.hasError('isOnlyWhiteSpace')) ?
              $localize`:@@invalidpassword:La contraseña ingresada es inválida` :
              this.companyRegisterForm.get('password')!.hasError('responseMessageError') ?
                $localize`:@@responseerrorcompanypass:Error en el registro: ${this.companyRegisterForm.get('password')?.getError('responseMessageError')}` :
              "";
      }
      case "passwordConfirm": {
        return (this.companyRegisterForm.get('passwordConfirm')!.hasError('required')) ?
          $localize`:@@nonemptypasswordconfirm:La confirmación de contraseña es obligatoria` :
          (this.companyRegisterForm.get('passwordConfirm')!.hasError('confirmedValidator')) ?
            $localize`:@@nonmatchingpasswordconfirm:Las contraseñas no coinciden` :
            "";
      }
      case "termsCheck": {
        return (this.companyRegisterForm.get('termsCheck')!.hasError('required')) ?
          $localize`:@@nonselectedterms:Debe aceptar los términos y condiciones` :
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
        this.companyRegisterForm.controls[this.mapKeys[key]].setErrors({ "responseMessageError": value });
      });

    } else {
      console.log("Exception response sin array de errores");
    }
  }

  registerCompany() {
    let companyData = new CompanyRegisterRequest(this.companyRegisterForm.get('companyName')?.value,
      this.companyRegisterForm.get('companyEmail')?.value,
      this.companyRegisterForm.get('password')?.value);

    this.companyService.companySignUp(companyData).subscribe({
      error: (exception) => this.setErrorBack(exception),
      next: (res) => this.router.navigateByUrl(`${AppRoutesEnum.company}/${AppRoutesEnum.companyHome}/${res.data.token}`)
    })
  }

}
