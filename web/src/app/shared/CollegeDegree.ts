export class CollegeDegree {
  name: String;

  public constructor(
    name: String
  ) {
    this.name = name
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
