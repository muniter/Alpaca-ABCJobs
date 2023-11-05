import { Skill } from "../shared/skill";

export class Exam {
  id: number;
  skill: Skill;
  numberOfQuestions: number;

  public constructor(
    id: number,
    skill: Skill,
    numberOfQuestion: number
  ) {
    this.id = id;
    this.skill = skill;
    this.numberOfQuestions = numberOfQuestion;
  }
}

export class ExamResponse {
  success: string;
  data: Exam[];

  public constructor(
    success: string,
    data: Exam[]
  ) {
    this.success = success;
    this.data = data;
  }
}

export class ExamResult {
  id: number;
  exam: Exam;
  idCandidato: number;
  result: number;
  completed: boolean;

  public constructor(
    id: number,
    exam: Exam,
    idCandidato: number,
    result: number,
    completed: boolean
  ) {
    this.id = id;
    this.exam = exam;
    this.idCandidato = idCandidato;
    this.result = result;
    this.completed = completed;
  }
}

export class ExamResultResponse {
  success: string;
  data: ExamResult[];

  public constructor(
    success: string,
    data: ExamResult[]
  ) {
    this.success = success;
    this.data = data;
  }
}