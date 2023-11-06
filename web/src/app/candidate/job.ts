import { Skill } from "../shared/skill";

export class Job {
  id: number;
  role: string;
  company: string;
  description: string;
  skills: Skill[];
  jobStart: number;
  jobEnd: number | null;

  public constructor(
    id: number,
    role: string,
    company: string,
    description: string,
    skills: Skill[],
    jobStart: number,
    jobEnd: number | null
  ) {
    this.id = id;
    this.role = role
    this.company = company;
    this.description = description;
    this.skills = skills;
    this.jobStart = jobStart;
    this.jobEnd = jobEnd;
  }
}

export class JobServiceSchema {
  id: number;
  role: string;
  company: string;
  description: string;
  skills: any;
  start_year: number;
  end_year: number | null;

  public constructor(
    id: number,
    role: string,
    company: string,
    description: string,
    skills: any,
    start_year: number,
    end_year: number | null
  ) {
    this.id = id;
    this.role = role
    this.company = company;
    this.description = description;
    this.skills = skills;
    this.start_year = start_year;
    this.end_year = end_year;
  }
}

export class JobResponse {
  success: boolean;
  data: JobServiceSchema[];

  public constructor(
    success: boolean,
    data: JobServiceSchema[]
  ) {
    this.success = success;
    this.data = data
  }
}

export const mapKeysJob: { [index: string]: string } = {
  "id": "id",
  "role": "role",
  "company": "company",
  "description": "description",
  "skills": "skills", 
  "start_year": "jobStart",
  "end_year": "jobEnd"
};
