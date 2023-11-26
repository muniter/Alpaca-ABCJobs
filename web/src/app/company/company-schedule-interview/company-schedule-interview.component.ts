import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { CompanyPositionDetailComponent } from '../company-position-detail/company-position-detail.component';
import { CompanyService } from '../company.service';
import { Position } from '../Position';
import { ScheduleInterviewRequest } from '../vacancy';
import { MomentDateAdapter } from '@angular/material-moment-adapter';
import { DateAdapter, MAT_DATE_LOCALE, MAT_DATE_FORMATS } from '@angular/material/core';
import { CUSTOM_DATE_FORMAT } from 'src/app/shared/Format';

@Component({
  selector: 'app-company-schedule-interview',
  templateUrl: './company-schedule-interview.component.html',
  styleUrls: ['./company-schedule-interview.component.css'],
  providers: [
    {
      provide: DateAdapter,
      useClass: MomentDateAdapter,
      deps: [MAT_DATE_LOCALE],
    },
    {provide: MAT_DATE_FORMATS, useValue: CUSTOM_DATE_FORMAT},
  ]
})
export class CompanyScheduleInterviewComponent implements OnInit {

  scheduleInterviewForm!: FormGroup;
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
    this.scheduleInterviewForm = this.formBuilder.group({
      date: ["", [Validators.required]],
      hour: ["", [Validators.required]]
    });
  }

  scheduleInterview() {
    let date: Date = new Date(this.scheduleInterviewForm.get('date')?.value);
    let hour: Date = this.scheduleInterviewForm.get('hour')?.value;
    let splitHour = hour.toString().split(':');

    let fullDateTime = new Date(date.getFullYear(), date.getMonth(), date.getDate(), Number(splitHour[0]), Number(splitHour[1]), 0)

    var request: ScheduleInterviewRequest = new ScheduleInterviewRequest(fullDateTime.toISOString());

    this.companyService.scheduleInterview(this.token, this.position.id, request).subscribe({
      error: (error) => {
        console.log("Error: " + error)
      },
      complete: () => {
        this.dialogRef.close();
      }
      
    });
  }

  onCancel() {
    this.dialogRef.close();
  }
}
