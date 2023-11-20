import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { CompanyCreateEmployeeComponent } from '../company-create-employee/company-create-employee.component';
import { CompanyService } from '../company.service';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Team } from '../Team';
import { ProjectCreateRequest } from '../Project';

@Component({
  selector: 'app-company-create-project',
  templateUrl: './company-create-project.component.html',
  styleUrls: ['./company-create-project.component.css']
})
export class CompanyCreateProjectComponent implements OnInit {

  projectCreateForm!: FormGroup;
  allTeams!: Team[]

  constructor(
    @Inject(MAT_DIALOG_DATA) private data: any,
    public dialogRef: MatDialogRef<CompanyCreateEmployeeComponent>,
    private companyService: CompanyService,
    private formBuilder: FormBuilder) { }

  ngOnInit() {
    this.projectCreateForm = this.formBuilder.group({
      name: ["", [Validators.required]],
      description: ["", [Validators.required]],
      team: ["", [Validators.required]],
    });

    this.allTeams = []
    this.companyService.getTeams(this.data.token).subscribe({
      next: (response) => {
        this.allTeams = response.data
      }
    })
  }

  projectCreation() {
    let projectCreationRequest = new ProjectCreateRequest(
      this.projectCreateForm.get('name')?.value,
      this.projectCreateForm.get('description')?.value,
      this.projectCreateForm.get('team')?.value.id
    )

    this.companyService.postProject(this.data.token, projectCreationRequest).subscribe({
      error: (error) => {
        console.log("Error: " + error)
      }
    })

    this.dialogRef.close();
  }

  onCancel() {
    this.dialogRef.close();
  }
}
