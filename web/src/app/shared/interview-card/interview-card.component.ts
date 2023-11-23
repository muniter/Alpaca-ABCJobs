import { formatDate } from '@angular/common';
import { Component, Inject, Input, LOCALE_ID, OnInit } from '@angular/core';
import { CandidateInterview } from 'src/app/candidate/CandidateInterview';

@Component({
  selector: 'app-interview-card',
  templateUrl: './interview-card.component.html',
  styleUrls: ['./interview-card.component.css']
})
export class InterviewCardComponent implements OnInit {

  @Input() interview: CandidateInterview | undefined;

  interviewDay: string | undefined;
  interviewMonthYear: string | undefined;
  interviewHour: string | undefined;
  companyName: string | undefined;

  constructor(
    @Inject(LOCALE_ID) public locale: string
  ) {
  }

  ngOnInit() {
    if (this.interview) {
      let date = new Date(this.interview?.interview_date)

      this.interviewDay =  formatDate(date, 'dd', this.locale)
      this.interviewMonthYear = formatDate(date, 'MMM yyyy', this.locale)
      this.interviewHour = formatDate(date, 'HH:mm', this.locale)
      this.companyName = this.interview.company
    }
  }

}
