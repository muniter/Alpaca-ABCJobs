import { COMMA, ENTER } from '@angular/cdk/keycodes';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { $enum } from "ts-enum-util";
import { Languages } from 'src/app/core/enums/Languages.enum';
import SharedCustomValidators from 'src/app/shared/utils/shared-custom-validators';
import { MatChipInputEvent } from '@angular/material/chips';
import { Observable } from 'rxjs';
import { map, startWith } from 'rxjs/operators'
import { MatAutocompleteSelectedEvent } from '@angular/material/autocomplete';
import { Countries } from 'src/app/core/enums/Countries.enum';

@Component({
  selector: 'app-candidate-profile',
  templateUrl: './candidate-profile.component.html',
  styleUrls: ['./candidate-profile.component.css']
})
export class CandidateProfileComponent implements OnInit {

  countries = Countries
  languages = Languages
  filteredLanguages!: Observable<string[]>;
  selectedLanguages: string[] = []
  personalInformationForm!: FormGroup;
  userFullName: string = "Maria Camila López";
  maxDate: Date;
  separatorKeysCodes: number[] = [ENTER, COMMA];

  constructor(private formBuilder: FormBuilder) {

    const now = new Date().getTime();
    this.maxDate = new Date(now)
    this.maxDate.setFullYear(this.maxDate.getFullYear() - 18);

  }

  ngOnInit() {

    this.personalInformationForm = this.formBuilder.group(
      {
        birthdate: ["", []],
        candidateCountry: ["", []],
        candidateCity: ["", []],
        candidateAddress: ["", [Validators.maxLength(255), Validators.minLength(5), SharedCustomValidators.spaceOnlyValidator]],
        candidateMobile: ["", [Validators.maxLength(15), Validators.minLength(2), Validators.pattern('^[0-9]+$'), SharedCustomValidators.spaceOnlyValidator]],
        candidateLanguages: ["", []],
        candidateBio: ["", [Validators.maxLength(255), Validators.minLength(10), SharedCustomValidators.spaceOnlyValidator]],
      }
    )

    this.filteredLanguages = this.personalInformationForm.get('candidateLanguages')!.valueChanges.pipe(
      startWith(null),
      map((language: string | null) => (language ? this._filter(language) : this.languages.slice())),
    );
  }

  getPersonalInfoErrorMessage(field: String) {
    switch (field) {
      case "birthdate": {
        return this.personalInformationForm.get('birthdate')!.hasError('matDatepickerMax') ?
          $localize`:@@minagevalidation:Su edad no puede ser menor a 18 años.` :
          this.personalInformationForm.get('birthdate')!.hasError('matDatepickerParse') ?
            $localize`:@@invaliddatevalidation:La fecha ingresada es inválida.` :
            "";
      }
      case "candidateAddress": {
        return (this.personalInformationForm.get('candidateAddress')!.hasError('minlength') ||
          this.personalInformationForm.get('candidateAddress')!.hasError('maxlength')) ?
          $localize`:@@invalidlengthaddress:La dirección debe tener entre 5 y 255 caracteres` :
          this.personalInformationForm.get('candidateAddress')!.hasError('isOnlyWhiteSpace') ?
            $localize`:@@nonemptyaddress:La dirección no puede contener solo espacios` :
            "";
      }
      case "candidateMobile": {
        return this.personalInformationForm.get('candidateMobile')!.hasError('pattern') ?
          $localize`:@@invalidformatmobile:El número celular debe tener solamente números` :
          (this.personalInformationForm.get('candidateMobile')!.hasError('minlength') ||
            this.personalInformationForm.get('candidateMobile')!.hasError('maxlength')) ?
            $localize`:@@invalidlengthmobile:El número celular debe tener entre 2 y 15 caracteres` :
            "";
      }
      case "candidateBio": {
        return this.personalInformationForm.get('candidateBio')!.hasError('isOnlyWhiteSpace') ?
          $localize`:@@nonemptybio:El texto ingresado no puede contener solo espacios` :
          (this.personalInformationForm.get('candidateBio')!.hasError('minlength') ||
            this.personalInformationForm.get('candidateBio')!.hasError('maxlength')) ?
            $localize`:@@invalidlengthbio:El texto ingresado debe tener entre 10 y 255 caracteres` :
            "";
      }
      default: {
        return "";
      }
    }
  }

  removeLanguage(language: string): void {
    const index = this.selectedLanguages.indexOf(language);

    if (index >= 0) {
      this.selectedLanguages.splice(index, 1);
    }
  }

  addLanguage(event: MatChipInputEvent): void {
    const value = (event.value || '').trim();

    // Add our fruit
    if (value) {
      this.selectedLanguages.push(value);
    }

    // Clear the input value
    event.chipInput!.clear();

  }

  selected(event: MatAutocompleteSelectedEvent): void {
    const index = this.selectedLanguages.indexOf(event.option.viewValue);

    if (index >= 0) {
      return;
    }

    this.selectedLanguages.push(event.option.viewValue);
    this.personalInformationForm.get('candidateLanguages')!.setValue(null);
  }

  private _filter(value: string): string[] {
    const filterValue = value.toLowerCase();

    return this.languages.filter(language => language.toLowerCase().includes(filterValue));
  }
}
