export class Job {
  id: number;
  role: string;
  company: string;
  description: string;
  skills: any;
  jobStart: number;
  jobEnd: number | null;

  public constructor(
    id: number,
    role: string,
    company: string,
    description: string,
    skills: any,
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
  /* start_date: number;
  end_date: number | null; */
  start_date: string;
  end_date: string | null;

  public constructor(
    id: number,
    role: string,
    company: string,
    description: string,
    skills: any,
    /* start_date: number,
    end_date: number | null */
    start_date: string,
    end_date: string | null
  ) {
    this.id = id;
    this.role = role
    this.company = company;
    this.description = description;
    this.skills = skills;
    this.start_date = start_date;
    this.end_date = end_date;
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
  "start_date": "jobStart",
  "end_date": "jobEnd"
};
