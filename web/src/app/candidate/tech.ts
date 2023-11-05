import { Skill } from "../shared/skill";

export class Tech {
  id: number;
  tech: Skill;
  /* raiting: number; */
  moreInfoTech: string;

  public constructor(
    id: number,
    tech: Skill, 
    /* raiting: number, */
    moreInfoTech: string
    ) {
    this.id = id;
    this.tech = tech
    /* this.raiting = raiting */
    this.moreInfoTech = moreInfoTech;
  }
}

export class TechServiceSchema {
  id: number;
  type: Skill;
  /* raiting: number; */
  description: string;

  public constructor(
    id: number,
    type: Skill,
    /* raiting: number, */
    description: string
  ) {
    this.id = id;
    this.type = type;
    /* this.raiting = raiting; */
    this.description = description;
  }
}

export class TechResponse {
  success: boolean;
  data: TechServiceSchema[];

  public constructor(
    success: boolean,
    data: TechServiceSchema[]
  ) {
    this.success = success;
    this.data = data
  }
}

export class TechRequestRow {
  type: number;
  description: string;

  public constructor(
    type: number, 
    description: string 
  ) {
    this.type = type;
    this.description = description;
  }
}

export class TechRequest {
  list: TechRequestRow[];

  public constructor(
    list: TechRequestRow[]
  ) {
    this.list = list;
  }
}

export const mapKeysTech: { [index: string]: string } = {
  "id": "id",
  "type": "tech",
  "raiting": "raiting",
  "description": "moreInfoTech",
};