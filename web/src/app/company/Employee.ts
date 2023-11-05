import { Personality } from "../shared/Personality";
import { Skill } from "../shared/skill";

export class Employee {
    id: number;
    name: string;
    title: string;
    personality: Personality;
    skills: Skill[]

    public constructor(
        id: number,
        name: string,
        title: string,
        personality: Personality,
        skills: Skill[]
    ) {
        this.id = id
        this.name = name
        this.title = title
        this.personality = personality
        this.skills = skills
    }
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
