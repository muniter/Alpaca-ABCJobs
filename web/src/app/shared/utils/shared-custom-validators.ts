import { AbstractControl } from "@angular/forms";
import { FormGroup } from "@angular/forms";
import { min } from "rxjs";

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

    static greaterThanValidator(maxControlNameMax: string, minControlName: string, includeEqual: boolean = false) {
      return (formGroup: FormGroup) => {
          const maxControl = formGroup.controls[maxControlNameMax];
          const minControl = formGroup.controls[minControlName];

          if (maxControl.errors && !maxControl.errors['greaterThan']) {
            return;
          }

          if(minControl.value && maxControl.value && maxControl.value != 0 && 
             (maxControl.value < minControl.value || 
              (!includeEqual && maxControl.value === minControl.value))) {
            maxControl.setErrors({ greaterThan: true });
          } else {
            maxControl.setErrors(null);
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