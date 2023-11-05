import { Component, OnInit } from '@angular/core';
import { Exam, ExamResult } from '../exam';
import { TechnicalTestService } from '../technical-test.service';
import { ActivatedRoute, Router } from '@angular/router';
import { AppRoutesEnum } from 'src/app/core/enums';
import { MatDialog } from '@angular/material/dialog';
import { TechnicalTestExamComponent } from '../technical-test-exam/technical-test-exam.component';

@Component({
  selector: 'app-technical-test-candidate',
  templateUrl: './technical-test-candidate.component.html',
  styleUrls: ['./technical-test-candidate.component.css']
})
export class TechnicalTestCandidateComponent implements OnInit {

  suggestedQuizzes: Exam[] = [];
  completedQuizzes: ExamResult[] = [];
  token: string = "";

  constructor(
    private activatedRouter: ActivatedRoute,
    private router: Router,
    public dialog: MatDialog,
    private technicalTestService: TechnicalTestService
  ) {
    this.validateToken(this.activatedRouter.snapshot.params['userToken']);
  }
  
  validateToken(token:string) {
    this.token = "";
    if (!token) {
      this.router.navigateByUrl(`${AppRoutesEnum.candidate}/${AppRoutesEnum.candidateLogin}`)
    } else {
      this.token = this.activatedRouter.snapshot.params['userToken'];
    }
  }

  ngOnInit() {
    this.getExams();
  }

  getExams() {
    this.technicalTestService.getExamsResult(this.token).subscribe({
      next: (response) => {
        this.completedQuizzes = response.data;
        this.technicalTestService.getExams(this.token).subscribe({
          next: (response) => {
            this.suggestedQuizzes = response.data;
            const completedIds = this.completedQuizzes.map((examResult) => examResult.exam.id);
            this.suggestedQuizzes = this.suggestedQuizzes.filter(
                                      (exam) => !completedIds.includes(exam.id) )
          }
        })
      }
    })
  }

  startExamDialog(quiz: Exam) {
    const dialog = this.dialog.open(TechnicalTestExamComponent, {
      data: { token: this.token, dialog: this.dialog, theme: 'candidate-theme', quiz: quiz },
      width: '40%'
    });

    dialog.afterClosed().subscribe(result => {
      this.getExams();
    });
  }

}
