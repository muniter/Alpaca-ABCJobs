export class CollegeDegree {
  id: number;
  name: String;

  public constructor(
    id: number,
    name: String
  ) {
    this.id = id;
    this.name = name;
  }
}

export class CollegeDegreeResponse {
  success: boolean;
  data: CollegeDegree[];

  public constructor(
    success: boolean,
    data: CollegeDegree[]
  ) {
    this.success = success;
    this.data = data
  }
}
