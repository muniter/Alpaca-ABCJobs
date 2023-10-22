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
import { Country, CountryResponse } from 'src/app/shared/Country';
import { CandidateService } from '../candidate.service';
import { Language } from 'src/app/shared/Language';
import { PersonalInfo, SavePersonalInfoRequest } from '../candidate';
import { ActivatedRoute, Router } from '@angular/router';
import { AppRoutesEnum } from 'src/app/core/enums';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-candidate-profile',
  templateUrl: './candidate-profile.component.html',
  styleUrls: ['./candidate-profile.component.css']
})
export class CandidateProfileComponent implements OnInit {

  countries: Country[];
  objectLanguages: Language[];
  languages: string[];
  filteredLanguages!: Observable<string[]>;
  selectedLanguages: string[] = []
  personalInformationForm!: FormGroup;
  userPersonalInfo?: PersonalInfo;
  maxDate: Date;
  separatorKeysCodes: number[] = [ENTER, COMMA];
  token: string;
  personalInfoDisabled: boolean = true;

  constructor(private formBuilder: FormBuilder,
    private activatedRouter: ActivatedRoute,
    private router: Router,
    private candidateService: CandidateService,
    public datepipe: DatePipe) {

    this.token = ""
    if (!this.activatedRouter.snapshot.params['userToken']) {
      this.router.navigateByUrl(`${AppRoutesEnum.candidate}/${AppRoutesEnum.candidateLogin}`)
    } else {
      this.token = this.activatedRouter.snapshot.params['userToken'];
    }

    const now = new Date().getTime();
    this.maxDate = new Date(now)
    this.maxDate.setFullYear(this.maxDate.getFullYear() - 18);

    this.countries = []
    candidateService.getCountries().subscribe({
      next: (response) => this.countries = response.data
    })

    this.languages = []
    this.objectLanguages = []
    candidateService.getLanguages().subscribe({
      next: (response) => {
        this.objectLanguages = response.data
        this.languages = this.objectLanguages.map(lang => lang.name)
      }
    })
  }

  ngOnInit() {

    this.personalInformationForm = this.formBuilder.group(
      {
        birthdate: [, []],
        candidateCountry: [, []],
        candidateCity: [, []],
        candidateAddress: [, [Validators.maxLength(255), Validators.minLength(5), SharedCustomValidators.spaceOnlyValidator]],
        candidateMobile: [, [Validators.maxLength(15), Validators.minLength(2), Validators.pattern('^[0-9]+$'), SharedCustomValidators.spaceOnlyValidator]],
        candidateLanguages: [, []],
        candidateBio: [, [Validators.maxLength(255), Validators.minLength(10), SharedCustomValidators.spaceOnlyValidator]],
      }
    )

    this.userPersonalInfo = new PersonalInfo("", "", "", "", "", 0, "", "", "", "", "", []);
    this.candidateService.getPersonalInfo(this.token).subscribe({
      next: (response) => {
        this.userPersonalInfo = response.data

        let fixedDate: Date = new Date(this.userPersonalInfo.birth_date);
        let utcdate = new Date(fixedDate.getUTCFullYear(), fixedDate.getMonth(), fixedDate.getUTCDate())

        this.personalInformationForm.get('birthdate')?.setValue(utcdate);
        this.personalInformationForm.get('candidateCity')?.setValue(this.userPersonalInfo.city);
        this.personalInformationForm.get('candidateAddress')?.setValue(this.userPersonalInfo.address);
        this.personalInformationForm.get('candidateMobile')?.setValue(this.userPersonalInfo.phone);
        this.personalInformationForm.get('candidateBio')?.setValue(this.userPersonalInfo.biography);

        let country = this.countries.filter(cou => cou.num_code == this.userPersonalInfo!.country_code)?.at(0);
        this.personalInformationForm.get('candidateCountry')?.setValue(country);

        this.userPersonalInfo.languages.forEach(lan => this.selectedLanguages.push(lan.name));

        this.personalInformationForm?.updateValueAndValidity();
        this.personalInformationForm.disable();

        this.filteredLanguages = this.personalInformationForm.get('candidateLanguages')!.valueChanges.pipe(
          startWith(null),
          map((language: string | null) => (language ? this._filter(language) : this.languages.slice())),
        );
      }
    })
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

  savePersonalInfo() {

    let date:   Date = this.personalInformationForm.get('birthdate')?.value;
    console.log()
    let personalData = new SavePersonalInfoRequest(
      this.datepipe.transform(new Date(date.getFullYear(), date.getMonth(), date.getDate()), 'yyyy-MM-dd') || '',
      this.personalInformationForm.get('candidateCountry')?.value,
      this.personalInformationForm.get('candidateCity')?.value,
      this.personalInformationForm.get('candidateAddress')?.value,
      this.personalInformationForm.get('candidateMobile')?.value,
      this.personalInformationForm.get('candidateBio')?.value,
      this.objectLanguages.filter(x => this.selectedLanguages.includes(x.name)))

    this.candidateService.updatePersonalInfo(personalData, this.token).subscribe({
      complete: () => {
        this.disableForm();
      }
      })

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

  enableForm(){
    this.personalInfoDisabled = false;
    this.personalInformationForm.enable();
  }

  disableForm(){
    
    this.candidateService.getPersonalInfo(this.token).subscribe({
      next: (response) => {
        this.userPersonalInfo = response.data

        let fixedDate: Date = new Date(this.userPersonalInfo.birth_date);
        let utcdate = new Date(fixedDate.getUTCFullYear(), fixedDate.getMonth(), fixedDate.getUTCDate())

        this.personalInformationForm.get('birthdate')?.setValue(utcdate);
        this.personalInformationForm.get('candidateCity')?.setValue(this.userPersonalInfo.city);
        this.personalInformationForm.get('candidateAddress')?.setValue(this.userPersonalInfo.address);
        this.personalInformationForm.get('candidateMobile')?.setValue(this.userPersonalInfo.phone);
        this.personalInformationForm.get('candidateBio')?.setValue(this.userPersonalInfo.biography);

        let country = this.countries.filter(cou => cou.num_code == this.userPersonalInfo!.country_code)?.at(0);
        this.personalInformationForm.get('candidateCountry')?.setValue(country);

        this.selectedLanguages.splice(0);
        this.userPersonalInfo.languages.forEach(lan => this.selectedLanguages.push(lan.name));

        this.personalInformationForm?.updateValueAndValidity();
        this.personalInformationForm.disable();

        this.filteredLanguages = this.personalInformationForm.get('candidateLanguages')!.valueChanges.pipe(
          startWith(null),
          map((language: string | null) => (language ? this._filter(language) : this.languages.slice())),
        );
      }
    })

    this.personalInfoDisabled = true;
    this.personalInformationForm.disable();
  }

  private _filter(value: string): string[] {
    const filterValue = value.toLowerCase();

    return this.languages.filter(language => language.toLowerCase().includes(filterValue));
  }
}
