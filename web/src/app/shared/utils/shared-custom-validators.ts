import { AbstractControl } from "@angular/forms";
import { FormGroup } from "@angular/forms";

export default class SharedCustomValidators {

    static spaceOnlyValidator(
        control: AbstractControl
    ): { [key: string]: any } | null {
        const isSpace = /^\s+$/.test(control.value);
        if (control.value && isSpace) {
            return { isOnlyWhiteSpace: true };
        }
        return null;
    }

    static ConfirmedPassValidator(controlName: string, matchingControlName: string) {
        return (formGroup: FormGroup) => {
            const control = formGroup.controls[controlName];
            const matchingControl = formGroup.controls[matchingControlName];
            if (
                matchingControl.errors &&
                !matchingControl.errors['confirmedValidator']
            ) {
                return;
            }
            if (control.value !== matchingControl.value) {
                matchingControl.setErrors({ confirmedValidator: true });
            } else {
                matchingControl.setErrors(null);
            }
        };
    }

    /* static ConfirmedSaveValidator() {
      return (formGroup: FormGroup) => {
        console.log("entra a ConfirmedSaveValidator");
        Object.keys(formGroup.controls).forEach(key => {
          if(formGroup.controls[key].errors && 
             formGroup.controls[key].errors?.['responseMessageError']) {
              console.log(`key: ${key} detecta error responseMessageError`);
              formGroup.controls[key].setErrors({ responseMessageError: true });
          } 
       });
      };
    } */
}