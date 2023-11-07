export class Skill {
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

export class SkillResponse {
  success: boolean;
  data: Skill[];

  public constructor(
    success: boolean,
    data: Skill[]
  ) {
    this.success = success;
    this.data = data
  }
}
