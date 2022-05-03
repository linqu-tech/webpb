import { HttpService } from '@scripts';
import axios from 'axios';
import { StoreVisitRequest, StoreVisitResponse } from '@proto/StoreProto';

jest.mock('axios');

describe('http.service', () => {
  const RESPONSE_DATA = StoreVisitResponse.create({
    greeting: 'Welcome, Tom',
    store: {
      id: '11',
      name: '22',
      city: '33',
    },
  });

  it('should request success', () => {
    const httpService = new HttpService('https://abc');
    axios.request = jest.fn().mockResolvedValue({
      status: 200,
      data: RESPONSE_DATA,
    });
    httpService
      .request<StoreVisitResponse>(
        StoreVisitRequest.create({ customer: 'Tom', id: '123' })
      )
      .then(
        (res) => expect(res).toMatchObject(RESPONSE_DATA),
        (error) => console.log(error)
      );
  });

  it('should request failed', () => {
    const httpService = new HttpService('https://abc');
    axios.request = jest.fn().mockResolvedValue({
      status: 500,
      data: RESPONSE_DATA,
    });
    httpService
      .request<StoreVisitResponse>(
        StoreVisitRequest.create({ customer: 'Tom', id: '123' })
      )
      .then(
        (res) => expect(res).toMatchObject(RESPONSE_DATA),
        (error) => console.log(error)
      );
  });

  it('given error with response when request then log success', () => {
    const httpService = new HttpService('https://abc');
    axios.request = jest.fn().mockRejectedValue({
      response: {
        status: 500,
        data: JSON.stringify({ error: 'invalid' }),
      },
    });
    httpService
      .request<StoreVisitResponse>(
        StoreVisitRequest.create({ customer: 'Tom', id: '123' })
      )
      .then(
        (res) => expect(res).toMatchObject(RESPONSE_DATA),
        (error) => console.log(error)
      );
  });

  it('given error without response when request then log success', () => {
    const httpService = new HttpService('https://abc');
    axios.request = jest.fn().mockRejectedValue('ERROR');
    httpService
      .request<StoreVisitResponse>(
        StoreVisitRequest.create({ customer: 'Tom', id: '123' })
      )
      .then(
        (res) => expect(res).toMatchObject(RESPONSE_DATA),
        (error) => console.log(error)
      );
  });
});
