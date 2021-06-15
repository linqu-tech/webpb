import { HttpService } from './http.service';
import {
  CommonProto, StoreProto
} from '@proto';
import PageablePb = CommonProto.PageablePb;
import StoreVisitResponse = StoreProto.StoreVisitResponse;
import StoreVisitRequest = StoreProto.StoreVisitRequest;
import StoreListResponse = StoreProto.StoreListResponse;
import StoreListRequest = StoreProto.StoreListRequest;

export class Main {
  private httpService = new HttpService('http://127.0.0.1:8080');

  constructor() {
    Main.addClickListener('getStoreButton', () => this.getStore());
    Main.addClickListener('getStoresButton', () => this.getStores());
  }

  private static addClickListener(id: string, listener: () => void) {
    const pageable: PageablePb = PageablePb.create({});
    console.log(pageable);
    const element = document.getElementById(id);
    element && element.addEventListener('click', listener);
  }

  getStore(): void {
    this.httpService
      .request<StoreVisitResponse>(
        StoreVisitRequest.create({ customer: 'Tom', id: '123' })
      )
      .then(
        (res) => console.log(res),
        (error) => console.log(error)
      );
  }

  getStores(): void {
    this.httpService
      .request<StoreListResponse>(
        StoreListRequest.create({ pageable: { page: 2, size: 3 } })
      )
      .then(
        (res) => console.log(res),
        (error) => console.log(error)
      );
  }
}

new Main();
