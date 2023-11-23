import { Component, OnInit } from '@angular/core';
import { CandidateInterview } from '../CandidateInterview';
import { CandidateService } from '../candidate.service';
import { ActivatedRoute, Router } from '@angular/router';
import { AppRoutesEnum } from 'src/app/core/enums';

@Component({
  selector: 'app-candidate-interviews',
  templateUrl: './candidate-interviews.component.html',
  styleUrls: ['./candidate-interviews.component.css']
})
export class CandidateInterviewsComponent implements OnInit {

  token: string;
  scheduledInterviews!: CandidateInterview[]
  finishedInterviews!: CandidateInterview[]

  constructor(
    private candidateService: CandidateService,
    private activatedRouter: ActivatedRoute,
    private router: Router
  ) {
    this.token = ""
    if (!this.activatedRouter.snapshot.params['userToken']) {
      this.router.navigateByUrl(`${AppRoutesEnum.candidate}/${AppRoutesEnum.candidateLogin}`)
    } else {
      this.token = this.activatedRouter.snapshot.params['userToken'];
    }
  }

  ngOnInit() {
    this.scheduledInterviews = [];
    this.finishedInterviews = [];

    this.candidateService.getInterviews(this.token).subscribe({
      next: (response) => {
        this.scheduledInterviews = response.data.filter(x => new Date(x.interview_date).valueOf() - Date.now().valueOf() >= 0);
        this.finishedInterviews = response.data.filter(x => new Date(x.interview_date).valueOf() - Date.now().valueOf() < 0);
      }
    });
  }

}
