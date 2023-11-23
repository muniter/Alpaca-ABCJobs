export class CandidateInterview {
    id_vacancy: number;
    name: string;
    company: string;
    interview_date: string;
    completed: boolean;
    result: number;

    public constructor(
        id_vacancy: number,
        name: string,
        company: string,
        interview_date: string,
        completed: boolean,
        result: number
    ) {
        this.id_vacancy = id_vacancy;
        this.name = name;
        this.company = company;
        this.interview_date = interview_date;
        this.completed = completed;
        this.result = result;
    }
}

export class CandidateInterviewsResponse {
    success: boolean;
    data: CandidateInterview[];

    public constructor(
        success: boolean,
        data: CandidateInterview[]
    ) {
        this.success = success;
        this.data = data
    }
}