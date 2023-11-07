export class Personality {
    id: number;
    name: string;

    public constructor(
        id: number,
        name: string
    ) {
        this.id = id
        this.name = name
    }
}

export class PersonalityResponse {
    success: boolean;
    data: Personality[];

    public constructor(
        success: boolean,
        data: Personality[]
    ) {
        this.success = success
        this.data = data
    }
}