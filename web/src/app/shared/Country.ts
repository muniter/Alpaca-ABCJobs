export class Country {
    num_code: Number;
    alpha_2_code: string;
    alpha_3_code: string;
    en_short_name: string;
    nationality: string;

    public constructor(
        num_code: Number,
        alpha_2_code: string,
        alpha_3_code: string,
        en_short_name: string,
        nationality: string
    ) {
        this.num_code = num_code
        this.alpha_2_code = alpha_2_code
        this.alpha_3_code = alpha_3_code
        this.en_short_name = en_short_name
        this.nationality = nationality
    }
}

export class CountryResponse {
    success: boolean;
    data: Country[];

    public constructor(
        success: boolean,
        data: Country[]
    ) {
        this.success = success,
            this.data = data
    }
}
