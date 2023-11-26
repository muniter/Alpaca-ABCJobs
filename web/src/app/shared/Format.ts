export const CUSTOM_DATE_FORMAT = {
    parse: {
      dateInput: 'LL',
    },
    display: {
      dateInput: localStorage.getItem('dateFormat') ?? 'DD/MM/YYYY',
      monthYearLabel: 'MMMM YYYY',
      dateA11yLabel: 'YYYY/DD/MM',
      monthYearA11yLabel: 'MMMM YYYY',  
    },
  };