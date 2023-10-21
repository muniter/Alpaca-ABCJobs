import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import SharedCustomValidators from "../../shared/utils/shared-custom-validators"
import { CandidateService } from '../candidate.service';
import { CandidateFormRegister, mapKeys } from '../candidate';
import { Router } from '@angular/router';
import { AppRoutesEnum } from 'src/app/core/enums';

@Component({
  selector: 'app-candidate-register',
  templateUrl: './candidate-register.component.html',
  styleUrls: ['./candidate-register.component.css']
})
export class CandidateRegisterComponent implements OnInit {

  candidateRegisterForm!: FormGroup;
  registerSucess: boolean = false;

  constructor(
    private formBuilder: FormBuilder, 
    private candidateService: CandidateService,
    private router: Router
  ) { }

  ngOnInit() {
    this.candidateRegisterForm = this.formBuilder.group(
      {
        names: ["", [Validators.required, Validators.minLength(2), 
                     Validators.maxLength(100), SharedCustomValidators.spaceOnlyValidator]],
        lastnames: ["", [Validators.required, Validators.minLength(2), 
                         Validators.maxLength(100), SharedCustomValidators.spaceOnlyValidator]],
        email: ["", [Validators.required, Validators.email, Validators.minLength(5), 
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
      case "names": {
        return (this.candidateRegisterForm.get('names')!.hasError('required') ||
          this.candidateRegisterForm.get('names')!.hasError('isOnlyWhiteSpace')) ?
            $localize`:@@nonemptycantidatename:El nombre del candidato no puede ser vacío` :
            (this.candidateRegisterForm.get('names')!.hasError('minlength') ||
              this.candidateRegisterForm.get('names')!.hasError('maxlength')) ?
              $localize`:@@invalidlengthcandidatename:El nombre del candidato debe tener entre 2 y 100 caracteres` :
              (this.candidateRegisterForm.get('names')!.hasError('responseMessageError'))?
              $localize`:@@responsemessageerrorcandidatenames:Revisar nombres: 
                         ${this.candidateRegisterForm.get('names')?.getError('responseMessageError')}`:
              "";
      }
      case "lastnames": {
        return (this.candidateRegisterForm.get('lastnames')!.hasError('required') ||
          this.candidateRegisterForm.get('lastnames')!.hasError('isOnlyWhiteSpace')) ?
            $localize`:@@nonemptycantidatelastname:El apellido del candidato no puede ser vacío` :
            (this.candidateRegisterForm.get('lastnames')!.hasError('minlength') ||
              this.candidateRegisterForm.get('lastnames')!.hasError('maxlength')) ?
              $localize`:@@invalidlengthcandidatelastname:El apellido del candidato debe tener entre 2 y 100 caracteres` :
              (this.candidateRegisterForm.get('lastnames')!.hasError('responseMessageError'))?
                $localize`:@@responsemessageerrorcandidatelastnames:Revisar apellidos: 
                             ${this.candidateRegisterForm.get('lastnames')?.getError('responseMessageError')}`:
                "";
      }
      case "email": {
        return (this.candidateRegisterForm.get('email')!.hasError('required') ||
          this.candidateRegisterForm.get('email')!.hasError('isOnlyWhiteSpace')) ?
            $localize`:@@nonemptycantidateemail:El correo electrónico del candidato no puede ser vacío` :
            (this.candidateRegisterForm.get('email')!.hasError('minlength') ||
              this.candidateRegisterForm.get('email')!.hasError('maxlength')) ?
              $localize`:@@invalidlengthcandidateemail:El correo electrónico del candidato debe tener entre 5 y 255 caracteres` :
              (this.candidateRegisterForm.get('email')!.hasError('email'))?
                $localize`:@@invalidformatcandidateemail:El correo electrónico no tiene un formato válido`:
                (this.candidateRegisterForm.get('email')!.hasError('responseMessageError'))?
                  $localize`:@@responsemessageerrorcandidateemail:Revisar correo electrónico: 
                             ${this.candidateRegisterForm.get('email')?.getError('responseMessageError')}`:
                  "";
      }
      case "password": {
        return (this.candidateRegisterForm.get('password')!.hasError('required')) ?
          $localize`:@@nonemptypassword:La contraseña no puede ser vacía` :
          (this.candidateRegisterForm.get('password')!.hasError('minlength') ||
            this.candidateRegisterForm.get('password')!.hasError('maxlength')) ?
            $localize`:@@invalidlengthpassword:La contraseña debe tener entre 8 y 20 caracteres` :
            (this.candidateRegisterForm.get('password')!.hasError('isOnlyWhiteSpace')) ?
              $localize`:@@invalidpassword:La contraseña ingresada es inválida` :
              (this.candidateRegisterForm.get('password')!.hasError('responseMessageError'))?
              $localize`:@@responsemessageerrorcandidatepass:Revisar password: 
                         ${this.candidateRegisterForm.get('password')?.getError('responseMessageError')}`:
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

  setErrorBack(exception:any) {
    if(exception.error?.errors !== undefined) {
      Object.entries(exception.error.errors).forEach(([key, value]) => {
        this.candidateRegisterForm.controls[mapKeys[key]].setErrors({ "responseMessageError": value});
      });
      
    } else {
      console.log("Exception response sin array de errores");
    }
  }

  candidateRegister(candidate: CandidateFormRegister) {
    this.candidateService
      .userSignUp(candidate)
      .subscribe({
        error: (exception) => this.setErrorBack(exception),
        complete: () => { 
          this.registerSucess = true
          setTimeout(() => {
            this.router.navigateByUrl(`${AppRoutesEnum.candidate}/${AppRoutesEnum.candidateLogin}`)
          }, 2000);
        }
      })
  }

}
