export class Vacancy {
  id: number;
  name: string;

  public constructor(
    id: number,
    name: string
  ) {
    this.id = id;
    this.name = name;
  }
}

export class VacancyResponse {
  success: boolean;
  data: Vacancy[];

  public constructor(
    success: boolean,
    data: Vacancy[]
  ) {
    this.success = success;
    this.data = data
  }
}

export class VacancyRequest {
  id_candidate: number;

  public constructor(
    id_candidate: number
  ) {
    this.id_candidate = id_candidate;
  }
}

export class SaveScoresRequest {
  scores: Score[];

  public constructor(
    scores: Score[]
  ) {
    this.scores = scores;
  }
}

export class Score {
  id_candidate: number | null;
  result: number | null;

  public constructor(
    id_candidate: number | null,
    result: number | null
  ) {
    this.id_candidate = id_candidate;
    this.result = result;
  }
}

export class SelectCandidateRequest {
  id_candidate: number | null;

  public constructor(
    id_candidate: number | null
  ) {
    this.id_candidate = id_candidate;
  }
}
