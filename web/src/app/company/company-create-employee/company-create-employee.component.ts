import { COMMA, ENTER } from '@angular/cdk/keycodes';
import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatAutocompleteSelectedEvent } from '@angular/material/autocomplete';
import { MatChipInputEvent } from '@angular/material/chips';
import { Observable, map, startWith } from 'rxjs';
import { CompanyService } from '../company.service';
import { Skill } from 'src/app/shared/skill';
import { Personality } from 'src/app/shared/Personality';
import { EmployeeCreationRequest } from '../Employee';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
@Component({
  selector: 'app-company-create-employee',
  templateUrl: './company-create-employee.component.html',
  styleUrls: ['./company-create-employee.component.css']
})
export class CompanyCreateEmployeeComponent implements OnInit {

  employeeCreateForm!: FormGroup;

  personalities!: Personality[]

  skills!: string[]
  objectSkills!: Skill[]
  selectedSkills: string[] = []
  filteredSkills!: Observable<string[]>;
  separatorKeysCodes: number[] = [ENTER, COMMA];

  constructor(
    @Inject(MAT_DIALOG_DATA) private data: any,
    public dialogRef: MatDialogRef<CompanyCreateEmployeeComponent>,
    private companyService: CompanyService,
    private formBuilder: FormBuilder
  ) { }

  ngOnInit() {

    this.employeeCreateForm = this.formBuilder.group({
      name: ["", [Validators.required]],
      position: ["", [Validators.required]],
      skills: ["", []],
      personality: ["", [Validators.required]]
    });

    this.skills = []
    
    this.companyService.getSkills().subscribe({
      next: (response) => {
        this.objectSkills = response.data
        this.skills = response.data.map(skill => skill.name)
        this.filteredSkills = this.employeeCreateForm.get('skills')!.valueChanges.pipe(
          startWith(null),
          map((skill: string | null) => (skill ? this._filter(skill) : this.skills.slice())),
        );
        this.checkSkillsValid()
      }
    })

    this.personalities = []
    this.companyService.getPersonalities().subscribe({
      next: (response) => {
        this.personalities = response.data
      }
    })
  }

  getErrorMessage(field: String) {
    switch (field) {

      case "name": {
        return $localize`:@@nonemptyemployeename:El nombre del empleado no puede ser vacío`
      }
      case "position": {
        return $localize`:@@nonemptypositionname:El cargo del empleado no puede ser vacío`
      }
      case "personality": {
        return $localize`:@@nonemptypersonality:Debe seleccionar una personalidad`
      }
      default: {
        return "";
      }
    }
  }

  employeeCreation() {
    let emploteeCreationRequest = new EmployeeCreationRequest(
      this.employeeCreateForm.get('name')?.value,
      this.employeeCreateForm.get('position')?.value,
      this.employeeCreateForm.get('personality')?.value,
      this.objectSkills.filter(x => this.selectedSkills?.includes(x?.name)).map(skill => skill.id)
    )

    this.companyService.postEmployee(this.data.token, emploteeCreationRequest).subscribe({
      error: (error) => {
        console.log("Error: " + error)
      }
    })

    this.dialogRef.close();
  }

  removeSkill(language: string): void {
    const index = this.selectedSkills.indexOf(language);

    if (index >= 0) {
      this.selectedSkills.splice(index, 1);
    }

    this.checkSkillsValid()
  }

  addSkill(event: MatChipInputEvent): void {
    const value = (event.value || '').trim();

    // Add our fruit
    if (value) {
      this.selectedSkills.push(value);
    }

    // Clear the input value
    event.chipInput!.clear();
  }

  selected(event: MatAutocompleteSelectedEvent): void {
    const index = this.selectedSkills.indexOf(event.option.viewValue);

    if (index < 0) {
      this.selectedSkills.push(event.option.viewValue);
      this.employeeCreateForm.get('skills')!.setValue(null);
    }
  }

  private _filter(value: string): string[] {
    const filterValue = value.toLowerCase();

    return this.skills.filter(skill => skill.toLowerCase().includes(filterValue));
  }

  checkSkillsValid() {
    this.employeeCreateForm.get("skills")?.markAsDirty
    this.employeeCreateForm.get("skills")?.markAsTouched
    this.employeeCreateForm.get("skills")?.setErrors({"Required": true})
    console.log(this.employeeCreateForm.get("skills")?.valid)
  }

  onCancel(){
    this.dialogRef.close();
  }
}


