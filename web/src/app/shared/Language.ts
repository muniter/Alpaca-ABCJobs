export class Language {
    id: string;
    name: string;

    public constructor(
        id: string,
        name: string
    ) {
        this.id = id
        this.name = name
    }
}

export class LanguageResponse {
    success: boolean;
    data: Language[];

    public constructor(
        success: boolean,
        data: Language[]
    ) {
        this.success = success
        this.data = data
    }
}