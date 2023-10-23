/* tslint:disable:no-unused-variable */

import { TestBed, async, inject } from '@angular/core/testing';
import { UserService } from './user.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { UserSettingsDetail } from './user';

describe('Service: User', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [UserService]
    });
  });

  it('should ...', inject([UserService], (service: UserService) => {
    expect(service).toBeTruthy();
  }));
  

  it('should get config', inject([UserService, HttpClientTestingModule], (service: UserService, client: HttpClientTestingModule) => {

    //spyOn(client, 'get<>').and.stub();

    service.getConfig("aa").subscribe(value => {
      expect(value).toBeDefined();
    });

  }));
  

  it('should set config', inject([UserService, HttpClientTestingModule], (service: UserService, client: HttpClientTestingModule) => {

    //spyOn(client, 'get<>').and.stub();

    service.setConfig("aa", new UserSettingsDetail("","","")).subscribe(value => {
      expect(value).toBeDefined();
    });

  }));
});
