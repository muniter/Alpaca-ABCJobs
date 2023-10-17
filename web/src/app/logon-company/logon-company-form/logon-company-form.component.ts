import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import SharedCustomValidators from "../../shared/utils/shared-custom-validators"

@Component({
  selector: 'app-logon-company-form',
  templateUrl: './logon-company-form.component.html',
  styleUrls: ['./logon-company-form.component.css']
})

export class LogonCompanyFormComponent implements OnInit {

  companyLogonForm!: FormGroup;

  constructor(private formBuilder: FormBuilder) {
  }

  ngOnInit() {
    this.companyLogonForm = this.formBuilder.group({
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
    this.companyLogonForm.controls["termsCheck2"].markAsTouched();

    if (!this.companyLogonForm.controls["termsCheck"].value) {
      this.companyLogonForm.controls["termsCheck2"].setErrors({ required: true });
    }
    else {
      this.companyLogonForm.controls["termsCheck2"].setErrors(null);
    }
  }

  getErrorMessage(field: String) {
    switch (field) {
      case "companyName": {
        return (this.companyLogonForm.get('companyName')!.hasError('required') ||
          this.companyLogonForm.get('companyName')!.hasError('isOnlyWhiteSpace')) ?
          $localize`:@@nonemptycompanyname:El nombre de la empresa no puede ser vacío` :
          (this.companyLogonForm.get('companyName')!.hasError('minlength') ||
            this.companyLogonForm.get('companyName')!.hasError('maxlength')) ?
            $localize`:@@invalidlengthcompanyname:El nombre de la empresa debe tener entre 2 y 255 caracteres` :
            "";
      }
      case "companyEmail": {
        return (this.companyLogonForm.get('companyEmail')!.hasError('required') ||
          this.companyLogonForm.get('companyEmail')!.hasError('isOnlyWhiteSpace')) ?
          $localize`:@@nonemptyemail:La dirección de correo electrónico no puede ser vacía` :
          (this.companyLogonForm.get('companyEmail')!.hasError('minlength') ||
            this.companyLogonForm.get('companyEmail')!.hasError('maxlength')) ?
            $localize`:@@invalidlengthemail:La dirección de correo electrónico debe tener entre 2 y 255 caracteres` :
            this.companyLogonForm.get('companyEmail')!.hasError('email') ?
              $localize`:@@invalidemail:La dirección de correo electrónico ingresada es inválida` :
              "";
      }
      case "password": {
        return (this.companyLogonForm.get('password')!.hasError('required')) ?
          $localize`:@@nonemptypassword:La contraseña no puede ser vacía` :
          (this.companyLogonForm.get('password')!.hasError('minlength') ||
            this.companyLogonForm.get('password')!.hasError('maxlength')) ?
            $localize`:@@invalidlengthpassword:La contraseña debe tener entre 8 y 20 caracteres` :
            (this.companyLogonForm.get('password')!.hasError('isOnlyWhiteSpace')) ?
              $localize`:@@invalidpassword:La contraseña ingresada es inválida` :
              "";
      }
      case "passwordConfirm": {
        return (this.companyLogonForm.get('passwordConfirm')!.hasError('required')) ?
          $localize`:@@nonemptypasswordconfirm:La confirmación de contraseña es obligatoria` :
          (this.companyLogonForm.get('passwordConfirm')!.hasError('confirmedValidator')) ?
            $localize`:@@nonmatchingpasswordconfirm:Las contraseñas no coinciden` :
            "";
      }
      case "termsCheck": {
        return (this.companyLogonForm.get('termsCheck')!.hasError('required')) ?
          $localize`:@@nonselectedterms:Debe aceptar los términos y condiciones` :
          "";
      }
      default: {
        return "";
      }
    }

  }
}


