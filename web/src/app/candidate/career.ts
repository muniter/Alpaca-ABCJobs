import { CollegeDegree } from "../shared/CollegeDegree";

export class Career {
  id: number;
  collegeDegree: CollegeDegree;
  careerTitle: string;
  school: string;
  careerStart: number;
  careerEnd: number | null;
  achievement: string;

  public constructor(
    id: number,
    collegeDegree: CollegeDegree, 
    careerTitle: string,
    school: string, 
    careerStart: number,
    careerEnd: number | null,
    achievement: string
    ) {
    this.id = id;
    this.collegeDegree = collegeDegree
    this.careerTitle = careerTitle;
    this.school = school;
    this.careerStart = careerStart;
    this.careerEnd = careerEnd;
    this.achievement = achievement;
  }
}

export class CareerServiceSchema {
  id: number;
  type: CollegeDegree | number;
  title: string;
  institution: string;
  start_year: number;
  end_year: number | null;
  achievement: string | null;

  public constructor(
    id: number,
    type: CollegeDegree | number, 
    title: string,
    institution: string, 
    start_year: number,
    end_year: number | null,
    achievement: string | null
    ) {
    this.id = id;
    this.type = type
    this.title = title;
    this.institution = institution;
    this.start_year = start_year;
    this.end_year = end_year;
    this.achievement = achievement;
  }
}

export class CareerResponse {
  success: boolean;
  data: CareerServiceSchema[];

  public constructor(
    success: boolean,
    data: CareerServiceSchema[]
  ) {
    this.success = success;
    this.data = data
  }
}

export const mapKeysCareer: { [index: string]: string } = {
  "id": "id",
  "type": "collegeDegree",
  "title": "careerTitle",
  "institution": "school",
  "start_year": "careerStart",
  "end_year": "careerEnd",
  "achievement": "achievement" 
};