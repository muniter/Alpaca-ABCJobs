import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import SharedCustomValidators from "../../shared/utils/shared-custom-validators"
import { CandidateService } from '../candidate.service';

@Component({
  selector: 'app-candidate-register',
  templateUrl: './candidate-register.component.html',
  styleUrls: ['./candidate-register.component.css']
})
export class CandidateRegisterComponent implements OnInit {

  candidateRegisterForm!: FormGroup;

  constructor(
    private formBuilder: FormBuilder, 
    private candidateService: CandidateService
  ) { }

  ngOnInit() {
    this.candidateRegisterForm = this.formBuilder.group(
      {
        candidateNames: ["", [Validators.required, Validators.minLength(2), 
                              Validators.maxLength(100), SharedCustomValidators.spaceOnlyValidator]],
        candidateLastNames: ["", [Validators.required, Validators.minLength(2), 
                                  Validators.maxLength(100), SharedCustomValidators.spaceOnlyValidator]],
        candidateEmail: ["", [Validators.required, Validators.email, Validators.minLength(2), 
                              Validators.maxLength(255), SharedCustomValidators.spaceOnlyValidator]],
        password: ["", [Validators.required, Validators.minLength(8), 
                        Validators.maxLength(20), SharedCustomValidators.spaceOnlyValidator]],
        passwordConfirm: ["", [Validators.required]],
        termsCheck: [false, [Validators.requiredTrue]],
        termsCheck2: ["", [Validators.required]]
      },
      {
        validator: SharedCustomValidators.ConfirmedPassValidator('password', 'passwordConfirm')
      }
    )
  }

  MarkTouchedAux() {
    this.candidateRegisterForm.controls["termsCheck2"].markAsTouched();

    if (!this.candidateRegisterForm.controls["termsCheck"].value) {
      this.candidateRegisterForm.controls["termsCheck2"].setErrors({ required: true });
    } else {
      this.candidateRegisterForm.controls["termsCheck2"].setErrors(null);
    }
  }

  getErrorMessage(field: String) {
    switch (field) {
      case "candidateNames": {
        return (this.candidateRegisterForm.get('candidateNames')!.hasError('required') ||
          this.candidateRegisterForm.get('candidateNames')!.hasError('isOnlyWhiteSpace')) ?
          $localize`:@@nonemptycantidatename:El nombre del candidato no puede ser vacío` :
          (this.candidateRegisterForm.get('candidateNames')!.hasError('minlength') ||
            this.candidateRegisterForm.get('candidateNames')!.hasError('maxlength')) ?
            $localize`:@@invalidlengthcandidatename:El nombre del candidato debe tener entre 2 y 100 caracteres` :
            "";
      }
      case "candidateLastNames": {
        return (this.candidateRegisterForm.get('candidateLastNames')!.hasError('required') ||
          this.candidateRegisterForm.get('candidateLastNames')!.hasError('isOnlyWhiteSpace')) ?
          $localize`:@@nonemptycantidatelastname:El apellido del candidato no puede ser vacío` :
          (this.candidateRegisterForm.get('candidateLastNames')!.hasError('minlength') ||
            this.candidateRegisterForm.get('candidateLastNames')!.hasError('maxlength')) ?
            $localize`:@@invalidlengthcandidatelastname:El apellido del candidato debe tener entre 2 y 100 caracteres` :
            "";
      }
      case "candidateEmail": {
        return (this.candidateRegisterForm.get('candidateEmail')!.hasError('required') ||
          this.candidateRegisterForm.get('candidateEmail')!.hasError('isOnlyWhiteSpace')) ?
          $localize`:@@nonemptycantidateemail:El correo electrónico del candidato no puede ser vacío` :
          (this.candidateRegisterForm.get('candidateEmail')!.hasError('minlength') ||
            this.candidateRegisterForm.get('candidateEmail')!.hasError('maxlength')) ?
            $localize`:@@invalidlengthcandidateemail:El correo electrónico del candidato debe tener entre 2 y 255 caracteres` :
            (this.candidateRegisterForm.get('candidateEmail')!.hasError('email'))?
            $localize`:@@invalidformatcandidateemail:El correo electrónico no tiene un formato válido`:"";
      }
      case "password": {
        return (this.candidateRegisterForm.get('password')!.hasError('required')) ?
          $localize`:@@nonemptypassword:La contraseña no puede ser vacía` :
          (this.candidateRegisterForm.get('password')!.hasError('minlength') ||
            this.candidateRegisterForm.get('password')!.hasError('maxlength')) ?
            $localize`:@@invalidlengthpassword:La contraseña debe tener entre 8 y 20 caracteres` :
            (this.candidateRegisterForm.get('password')!.hasError('isOnlyWhiteSpace')) ?
              $localize`:@@invalidpassword:La contraseña ingresada es inválida` :
              "";
      }
      case "passwordConfirm": {
        return (this.candidateRegisterForm.get('passwordConfirm')!.hasError('required')) ?
          $localize`:@@nonemptypasswordconfirm:La confirmación de contraseña es obligatoria` :
          (this.candidateRegisterForm.get('passwordConfirm')!.hasError('confirmedValidator')) ?
            $localize`:@@nonmatchingpasswordconfirm:Las contraseñas no coinciden` :
            "";
      }
      case "termsCheck": {
        return (this.candidateRegisterForm.get('termsCheck')!.hasError('required')) ?
          $localize`:@@nonselectedterms:Debe aceptar los términos y condiciones` :
          "";
      }
      default: {
        return "";
      }
    }

  }

  candidateRegister() {
    this.candidateService
      .userSignUp(
        this.candidateRegisterForm.get('candidateNames')?.value,
        this.candidateRegisterForm.get('candidateLastNames')?.value,
        this.candidateRegisterForm.get('candidateEmail')?.value,
        this.candidateRegisterForm.get('password')?.value)
      .subscribe({
        error: (e) => console.error(e),
        complete: () => console.info("complete")
      })
  }

}
