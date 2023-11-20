import { Personality } from "../shared/Personality";
import { Skill } from "../shared/skill";
import { TeamShort } from "./Team";

export class Employee {
    public constructor(
        public id: number,
        public name: string,
        public title: string,
        public personality: Personality,
        public skills: Skill[] = [],
        public evaluations: EmployeeEvaluation[] = [],
        public teams: TeamShort[] = [],
    ) {}
}

export class EmployeeEvaluation {
  public constructor(
    public id: number,
    public date: string,
    public result: number,
  ) {}
}

export class EmployeeResponse {
    success: boolean;
    data: Employee;

    public constructor(
        success: boolean,
        data: Employee
    ) {
        this.success = success;
        this.data = data
    }
}

export class EmployeesListResponse {
    success: boolean;
    data: Employee[];

    public constructor(
        success: boolean,
        data: Employee[]
    ) {
        this.success = success;
        this.data = data
    }
}

export class EmployeeCreationRequest {
    name: string;
    title: string;
    personality_id: number;
    skills: number[];

    public constructor(
        name: string,
        title: string,
        personality: Personality,
        skills: number[]
    ) {
        this.name = name;
        this.title = title;
        this.personality_id = personality ? personality.id : 0;
        this.skills = skills;
    }
}
