import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import SharedCustomValidators from "../../shared/utils/shared-custom-validators"

@Component({
  selector: 'app-company-login',
  templateUrl: './company-login.component.html',
  styleUrls: ['./company-login.component.css']
})
export class CompanyLoginComponent implements OnInit {

  companyLoginForm!: FormGroup;

  constructor(private formBuilder: FormBuilder) { }

  ngOnInit() {
    this.companyLoginForm = this.formBuilder.group({
      companyEmail: ["", [Validators.required, Validators.email, Validators.minLength(5), 
                   Validators.maxLength(255), SharedCustomValidators.spaceOnlyValidator]],
      password: ["", [Validators.required, Validators.minLength(8), 
                      Validators.maxLength(20), SharedCustomValidators.spaceOnlyValidator]],});
  }

}

