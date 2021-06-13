import { HttpService } from './http.service';
import { StoreDataRequest, StoreDataResponse, StoreListRequest, StoreListResponse, } from './proto';

if (process.env.NODE_ENV === 'development') {
  require('../index.html');
}

export class Index {
  private httpService = new HttpService('http://127.0.0.1:8080');

  constructor() {
    document
      .getElementById('getStoreButton')
      .addEventListener('click', () => this.getStore());
    document
      .getElementById('getStoresButton')
      .addEventListener('click', () => this.getStores());
  }

  getStore(): void {
    this.httpService
      .request<StoreDataResponse>(StoreDataRequest.create({ id: `123` }))
      .then(
        res => alert(JSON.stringify(res)),
        error => alert(JSON.stringify(error))
      );
  }

  getStores(): void {
    this.httpService
      .request<StoreListResponse>(
        StoreListRequest.create({
          customer: 'Tom',
          pageable: { page: 2 },
        })
      )
      .then(
        res => alert(JSON.stringify(res)),
        error => alert(JSON.stringify(error))
      );
  }
}

new Index();
