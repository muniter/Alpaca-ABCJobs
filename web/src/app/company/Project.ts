import { Team } from "./Team";

export class Project {
    id: number;
    name: string;
    description: string;
    team: Team;

    public constructor(
        id: number,
        name: string,
        description: string,
        team: Team
    ) {
        this.id = id
        this.name = name
        this.description = description
        this.team = team
    }
}

export class ProjectsListResponse {
    success: boolean;
    data: Project[];

    public constructor(
        success: boolean,
        data: Project[]
    ) {
        this.success = success;
        this.data = data
    }
}

export class ProjectCreateRequest {
    name: string;
    description: string;
    id_team: Number;

    public constructor(
        name: string,
        description: string,
        id_team: Number
    ) {
        this.name = name
        this.description = description
        this.id_team = id_team
    }
}

export class ProjectCreateResponse {
    success: boolean;
    data: Project;

    public constructor(
        success: boolean,
        data: Project
    ) {
        this.success = success;
        this.data = data
    }
}