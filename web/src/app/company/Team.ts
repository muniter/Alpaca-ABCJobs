import { Employee } from "./Employee";
import { Company } from "./company";

export class Team {
    id: number;
    name: string;
    company: Company;
    employees: Employee[]

    public constructor(
        id: number,
        name: string,
        company: Company,
        employees: Employee[]
    ) {
        this.id = id
        this.name = name
        this.company = company
        this.employees = employees
    }
}

export class TeamShort {
  public constructor(
    public id: number,
    public name: string,
  ) {}
}

export class TeamsListResponse {
    success: boolean;
    data: Team[];

    public constructor(
        success: boolean,
        data: Team[]
    ) {
        this.success = success;
        this.data = data
    }
}

export class TeamCreateRequest {
    name: string;
    employees: number[];

    public constructor(
        name: string,
        employees: number[]
    ) {
        this.name = name;
        this.employees = employees
    }
}

export class TeamCreateResponse {
    success: boolean;
    data: Team;

    public constructor(
        success: boolean,
        data: Team
    ) {
        this.success = success;
        this.data = data
    }
}
