import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { CompanyPositionDetailComponent } from '../company-position-detail/company-position-detail.component';
import { CompanyService } from '../company.service';
import { Position } from '../Position';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { SelectCandidateRequest } from '../vacancy';

@Component({
  selector: 'app-company-position-close',
  templateUrl: './company-position-close.component.html',
  styleUrls: ['./company-position-close.component.css']
})
export class CompanyPositionCloseComponent implements OnInit {

  closePositionForm!: FormGroup;
  position: Position;
  token: string;

  constructor(
    @Inject(MAT_DIALOG_DATA) private data: any,
    public dialogRef: MatDialogRef<CompanyPositionDetailComponent>,
    private companyService: CompanyService,
    private formBuilder: FormBuilder
  ) {
    this.position = data.position
    this.token = data.token
  }

  ngOnInit() {
    this.closePositionForm = this.formBuilder.group({
      candidate: ["", [Validators.required]]
    });
  }

  selectCandidate() {
    let candidateId = this.closePositionForm.get('candidate')?.value.id_candidate
    let request = new SelectCandidateRequest(candidateId)

    this.companyService.selectCandidate(this.data.token, this.position.id, request).subscribe({
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
