import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Team } from '../Team';
import { CompanyCreateEmployeeComponent } from '../company-create-employee/company-create-employee.component';
import { CompanyService } from '../company.service';
import { PositionCreateRequest } from '../Position';

@Component({
  selector: 'app-company-create-position',
  templateUrl: './company-create-position.component.html',
  styleUrls: ['./company-create-position.component.css']
})
export class CompanyCreatePositionComponent implements OnInit {

  positionCreateForm!: FormGroup;
  allTeams!: Team[]

  constructor(
    @Inject(MAT_DIALOG_DATA) private data: any,
    public dialogRef: MatDialogRef<CompanyCreateEmployeeComponent>,
    private companyService: CompanyService,
    private formBuilder: FormBuilder) { }

  ngOnInit() {
    this.positionCreateForm = this.formBuilder.group({
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

  positionCreation() {
    let positionCreationRequest = new PositionCreateRequest(
      this.positionCreateForm.get('name')?.value,
      this.positionCreateForm.get('description')?.value,
      this.positionCreateForm.get('team')?.value.id
    )

    this.companyService.postPosition(this.data.token, positionCreationRequest).subscribe({
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
