import { Candidate, PreselectedCandidate } from "../candidate/candidate";
import { Country } from "../shared/Country";
import { Team } from "./Team";
import { Company } from "./company";

export class Position {
    id: number;
    name: string;
    description: string;
    open: boolean;
    company: Company;
    team: Team;
    country: Country | null;
    preselection: PreselectedCandidate[];

    public constructor(
        id: number,
        name: string,
        description: string,
        open: boolean,
        company: Company,
        team: Team,
        country: Country | null,
        preselection: PreselectedCandidate[]
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.open = open;
        this.company = company;
        this.team = team;
        this.country = country;
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
    country_code: Number | null;

    public constructor(
        name: string,
        description: string,
        team_id: Number,
        country_code: Number | null
    ) {
        this.name = name
        this.description = description
        this.team_id = team_id
        this.country_code = country_code
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
