import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { CompanyCreateEmployeeComponent } from '../company-create-employee/company-create-employee.component';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { CompanyService } from '../company.service';
import { Employee } from '../Employee';
import { MatOptionSelectionChange } from '@angular/material/core';
import { MatSelectChange } from '@angular/material/select';
import { TeamCreateRequest } from '../Team';

@Component({
  selector: 'app-company-create-team',
  templateUrl: './company-create-team.component.html',
  styleUrls: ['./company-create-team.component.css']
})
export class CompanyCreateTeamComponent implements OnInit {

  teamCreateForm!: FormGroup;
  repeatedMembers: boolean = false;

  allEmployees!: Employee[]
  selectedEmployees!: Employee[]

  constructor(
    @Inject(MAT_DIALOG_DATA) private data: any,
    public dialogRef: MatDialogRef<CompanyCreateEmployeeComponent>,
    private companyService: CompanyService,
    private formBuilder: FormBuilder) { }

  ngOnInit() {
    this.teamCreateForm = this.formBuilder.group({
      name: ["", [Validators.required]]
    });

    this.allEmployees = []
    this.selectedEmployees = []
    this.companyService.getEmployees(this.data.token).subscribe({
      next: (response) => {
        this.allEmployees = response.data
      }
    })
  }

  teamCreation() {
    let teamCreationRequest = new TeamCreateRequest(
      this.teamCreateForm.get('name')?.value,
      this.selectedEmployees.filter(x => x != null).map(x => x.id)
    )

    this.companyService.postTeam(this.data.token, teamCreationRequest).subscribe({
      error: (error) => {
        console.log("Error: " + error)
      }
    })

    this.dialogRef.close();
  }

  onCancel() {
    this.dialogRef.close();
  }

  handleSelection(event: MatSelectChange, index: number) {

    if (event.value != null && index == this.selectedEmployees.length) {
      this.selectedEmployees.push(event.value)
    }

    this.selectedEmployees[index] = event.value

    this.checkRepeated()
  }

  checkRepeated() {
    this.repeatedMembers = (new Set(this.selectedEmployees).size !== this.selectedEmployees.length)
  }
}
