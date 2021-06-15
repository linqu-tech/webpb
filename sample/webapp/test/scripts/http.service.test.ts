import { HttpService } from '@scripts';
import axios from 'axios';
import { StoreProto } from '@proto';
import StoreVisitResponse = StoreProto.StoreVisitResponse;
import StoreVisitRequest = StoreProto.StoreVisitRequest;

jest.mock("axios");

describe("http.service", () => {

  const RESPONSE_DATA = StoreVisitResponse.create({
    greeting: 'Welcome, Tom',
    store: {
      id: '11',
      name: '22',
      city: '33'
    }
  });

  it("should request success", () => {
    const httpService = new HttpService('https://abc');
    axios.request = jest.fn().mockResolvedValue({
      status: 200,
      data: RESPONSE_DATA
    });
    httpService
      .request<StoreVisitResponse>(StoreVisitRequest.create({ customer: 'Tom', id: '123' }))
      .then(
        res => expect(res).toMatchObject(RESPONSE_DATA),
        error => console.log(error)
      );
  });

  it("should request failed", () => {
    const httpService = new HttpService('https://abc');
    axios.request = jest.fn().mockResolvedValue({
      status: 500,
      data: RESPONSE_DATA
    });
    httpService
      .request<StoreVisitResponse>(StoreVisitRequest.create({ customer: 'Tom', id: '123' }))
      .then(
        res => expect(res).toMatchObject(RESPONSE_DATA),
        error => console.log(error)
      );
  });

  it("should request on rejected", () => {
    const httpService = new HttpService('https://abc');
    axios.request = jest.fn().mockRejectedValue(RESPONSE_DATA);
    httpService
      .request<StoreVisitResponse>(StoreVisitRequest.create({ customer: 'Tom', id: '123' }))
      .then(
        res => expect(res).toMatchObject(RESPONSE_DATA),
        error => console.log(error)
      );
  });
});
