export class Search {
  country_code: number | null;
  technical_info_types: number[] | null;
  languages: string[] | null;
  least_academic_level: number | null;
  study_areas: string[] | null;
  skills: number[] | null;

  public constructor(
    country_code: number | null,
    technical_info_types: number[] | null,
    languages: string[] | null,
    least_academic_level: number | null,
    study_areas: string[] | null,
    skills: number[] | null
  ){
    this.country_code = country_code;
    this.technical_info_types = technical_info_types;
    this.languages = languages;
    this.least_academic_level = least_academic_level;
    this.study_areas = study_areas;
    this.skills = skills
  }
}
