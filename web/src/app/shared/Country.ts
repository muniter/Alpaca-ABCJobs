export class Country {
    num_code: Number;
    alpha_2_code: String;
    alpha_3_code: String;
    en_short_name: String;
    nationality: String;

    public constructor(
        num_code: Number,
        alpha_2_code: String,
        alpha_3_code: String,
        en_short_name: String,
        nationality: String
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
