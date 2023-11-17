import { HttpErrorResponse } from '@angular/common/http';
import { Component, Inject } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Employee } from 'src/app/company/Employee';
import { CompanyService } from 'src/app/company/company.service';

@Component({
  selector: 'app-company-hired-evaluation',
  templateUrl: './company-hired-evaluation.component.html',
  styleUrls: ['./company-hired-evaluation.component.css']
})
export class CompanyHiredEvaluationComponent {

  public token: string;
  public employee: Employee;
  public date = new Date();
  public period: string;

  public form = this.formBuilder.group({
    result: ['', [Validators.required, Validators.min(0), Validators.max(100)]],
    global: [''],
  });

  public constructor(
    @Inject(MAT_DIALOG_DATA) public data: { employee: Employee, token: string },
    public dialogRef: MatDialogRef<CompanyHiredEvaluationComponent>,
    public companyService: CompanyService,
    public formBuilder: FormBuilder,
  ) {
    this.employee = data.employee;
    this.token = data.token;
    const date = new Date();
    this.period = `${date.getMonth() + 1}/${date.getFullYear()}`;
  }

  public onCancel() {
    this.dialogRef.close();
  }

  public onSave() {
    this.saveEvaluation();
  }

  public getErrorMessage(field: string): string | null {
    const errors = this.form.get(field)?.errors;
    if (!errors) {
      return null;
    }

    if (errors['required']) {
      return $localize`:@@hired_error_required:Este campo es requerido`;
    }
    if (errors['min']) {
      return $localize`:@@hired_error_min:El valor mínimo es ${errors['min'].min}`;
    }
    if (errors['max']) {
      return $localize`:@@hired_error_max:El valor máximo es ${errors['max'].max}`;
    }
    if (errors['server']) {
      return errors['server'];
    }

    return null
  }

  public saveEvaluation() {
    if (this.form.valid) {
      const result = parseInt(this.form.value.result!);
      const dateObj = new Date();
      const date = dateObj.toISOString().split('T')[0];
      this.companyService.postEmployeeEvaluation(this.token, this.employee.id, { result, date })
        .subscribe({
          next: () => {
            this.dialogRef.close();
          },
          error: (e) => {
            const resp = e as HttpErrorResponse;
            const errors = resp?.error?.errors;
            if (errors && typeof errors === 'object') {
              for (const key in errors) {
                const error = errors[key];
                if (error && typeof error === 'string') {
                  let errorKey = key;
                  switch (key) {
                    case 'date':
                      errorKey = 'global';
                      break;
                  }
                  const control = this.form.get(errorKey)
                  if (control) {
                    control.setErrors({ server: error });
                  }
                }
              }
            }
          }
        })
    }
  }
}
