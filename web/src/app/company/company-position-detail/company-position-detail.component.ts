import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Position } from '../Position';
import { FormArray, FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { CompanyService } from '../company.service';
import { SaveScoresRequest, Score } from '../vacancy';

@Component({
  selector: 'app-company-position-detail',
  templateUrl: './company-position-detail.component.html',
  styleUrls: ['./company-position-detail.component.css']
})
export class CompanyPositionDetailComponent implements OnInit {

  positionGradeForm!: FormGroup;
  position: Position;

  constructor(
    @Inject(MAT_DIALOG_DATA) private data: any,
    public dialogRef: MatDialogRef<CompanyPositionDetailComponent>,
    private companyService: CompanyService,
    private formBuilder: FormBuilder
  ) {
    this.position = data.position
  }

  ngOnInit() {
    this.positionGradeForm = new FormGroup({
      formArray: this.formBuilder.array([])
    })

    const controlArray = this.positionGradeForm.get('formArray') as FormArray;

    Object.keys(this.position.preselection).map(Number).forEach((i) => {
      controlArray.push(
        this.formBuilder.group({
          grade: [this.position.preselection[i].result, [Validators.min(0), Validators.max(100), Validators.pattern("^[0-9]*$")]]
        })
      )
    })
  }

  isInvalid(index: number) {

    const controlArray = this.positionGradeForm.get('formArray') as FormArray;
    return controlArray.controls[index].invalid

  }

  saveScores() {
    const scores: Score[] = []

    const controlArray = this.positionGradeForm.get('formArray') as FormArray;

    Object.keys(this.position.preselection).map(Number).forEach((i) => {
      let score = controlArray.controls[i].value.grade
      let id = this.position.preselection[i].id_candidate

      if (score != null && score != "") {
        scores.push(new Score(id, score))
      }

    });
    
    this.companyService.saveScores(this.data.token, this.position.id, new SaveScoresRequest(scores)).subscribe({
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
