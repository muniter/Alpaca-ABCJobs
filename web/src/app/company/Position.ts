import { Candidate, PreselectedCandidate } from "../candidate/candidate";
import { Team } from "./Team";
import { Company } from "./company";

export class Position {
    id: number;
    name: string;
    description: string;
    open: boolean;
    company: Company;
    team: Team;
    preselection: PreselectedCandidate[];

    public constructor(
        id: number,
        name: string,
        description: string,
        open: boolean,
        company: Company,
        team: Team,
        preselection: PreselectedCandidate[]
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.open = open;
        this.company = company;
        this.team = team;
        this.preselection = preselection
    }
}

export class PositionsListResponse {
    success: boolean;
    data: Position[];

    public constructor(
        success: boolean,
        data: Position[]
    ) {
        this.success = success;
        this.data = data
    }
}

export class PositionCreateRequest {
    name: string;
    description: string;
    team_id: Number;

    public constructor(
        name: string,
        description: string,
        team_id: Number
    ) {
        this.name = name
        this.description = description
        this.team_id = team_id
    }
}

export class PositionCreateResponse {
    success: boolean;
    data: Position;

    public constructor(
        success: boolean,
        data: Position
    ) {
        this.success = success;
        this.data = data
    }
}
