import { da } from "@faker-js/faker";
import { ExamResult } from "./exam";

export class Answer {
  id: number;
  id_question: number;
  answer: string;

  public constructor(
    id: number, 
    id_question: number,
    answer: string
  ) {
    this.id = id;
    this.id_question = id_question;
    this.answer = answer
  }
}

export class Question {
  id: number;
  id_exam: number;
  question: string;
  difficulty: number;
  answers: Answer[];

  public constructor(
    id: number,
    id_exam: number,
    question: string,
    difficulty: number,
    answers: Answer[]
  ) {
    this.id = id;
    this.id_exam = id_exam;
    this.question = question;
    this.difficulty = difficulty;
    this.answers = answers
  }
}

export class Quiz {
  id_result: number;
  id_exam: number;
  next_question: Question | null;
  result: ExamResult | null;

  constructor(
    id_result: number,
    id_exam: number,
    next_question: Question | null,
    result: ExamResult | null
  ) {
    this.id_result = id_result;
    this.id_exam = id_exam;
    this.next_question = next_question;
    this.result = result
  }
}

export class QuizResponse {
  id: number;
  data: Quiz;

  constructor(
    id: number,
    data: Quiz
  ) {
    this.id = id;
    this.data = data
  }
}