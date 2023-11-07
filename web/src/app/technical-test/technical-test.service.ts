import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { ExamResponse, ExamResultResponse } from './exam';
import { Answer, QuizResponse } from './quiz';

@Injectable({
  providedIn: 'root'
})
export class TechnicalTestService {
  private backAssessmentsUrl = environment.baseUrl + 'evaluaciones';

  constructor(private http: HttpClient) { }

  getHeader(token: string) {
    return {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    };
  }

  getExams(token: string): Observable<ExamResponse> {
    const headers = this.getHeader(token);
    return this.http.get<ExamResponse>(`${this.backAssessmentsUrl}/exam`, { headers })
  }

  getExamsResult(token: string): Observable<ExamResultResponse> {
    const headers = this.getHeader(token);
    return this.http.get<ExamResultResponse>(`${this.backAssessmentsUrl}/exam-result`, { headers })
  }

  startExam(examId: number, token: string): Observable<QuizResponse> {
    const headers = this.getHeader(token)
    return this.http.post<QuizResponse>(`${this.backAssessmentsUrl}/exam-result/${examId}/start`, {}, { headers })
  }

  answerQuestion(resultId: number | undefined, answer: Answer, token: string): Observable<QuizResponse> {
    const headers = this.getHeader(token)
    return this.http.post<QuizResponse>(`${this.backAssessmentsUrl}/exam-result/${resultId}/answer`, answer, { headers })
  }

}
