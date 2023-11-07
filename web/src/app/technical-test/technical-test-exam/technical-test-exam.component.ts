import { Component, Inject, OnInit } from '@angular/core';
import { Exam } from '../exam';
import { MAT_DIALOG_DATA, MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { AppRoutesEnum } from 'src/app/core/enums';
import { TechnicalTestService } from '../technical-test.service';
import { Answer, Quiz } from '../quiz';

@Component({
  selector: 'app-technical-test-exam',
  templateUrl: './technical-test-exam.component.html',
  styleUrls: ['./technical-test-exam.component.css']
})
export class TechnicalTestExamComponent implements OnInit {

  token: string;
  dialog!: MatDialog;
  theme: string;
  exam: Exam;
  quiz?: Quiz;
  completedQuiz: boolean = false;
  messageError: string | null = null;

  constructor(
    private router: Router,
    private technicalTestService: TechnicalTestService,
    @Inject(MAT_DIALOG_DATA) public data: {token: string, dialog: MatDialog, theme: string, quiz: Exam}
  ) { 
    this.token = data.token;
    this.dialog = data.dialog;
    this.theme = data.theme;
    this.exam = data.quiz;
  }

  ngOnInit() {
    this.validateToken(this.token)
    this.startExam();
  }

  validateToken(token:string|null) {
    this.token = "";
    if(!token || token == "") {
      this.dialog?.closeAll();
      this.router.navigateByUrl(`${AppRoutesEnum.candidate}/${AppRoutesEnum.candidateLogin}`)
    } 
    this.token = token!;
  }

  startExam() {
    this.technicalTestService.startExam(this.exam.id, this.token).subscribe({
      error: (exception) => {
        this.messageError = $localize`:@@errorstartexam:El servicio de examenes no se encuentra disponible, por favor intente más tarde`;
        setTimeout(() => { this.messageError = null; this.dialog.closeAll() }, 3000);
      },
      next: (response) => {
        this.quiz = response.data;
      }
    })
  }

  answerQuestion(answer: Answer) {
    this.technicalTestService.answerQuestion(this.quiz?.id_result, answer, this.token).subscribe({
      error: (exception) => {
        this.messageError = $localize`:@@errornextquestion:El servicio de examenes no se encuentra disponible, por favor intente más tarde`;
        setTimeout(() => { this.messageError = null; }, 3000);
      },
      next: (response) => {
        this.quiz = response.data;
        if(this.quiz.result) {
          this.completedQuiz = true;
        }
      }
    })
  }

}
