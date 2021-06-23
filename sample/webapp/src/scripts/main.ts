import { StoreProto } from '@proto';
import { HttpService } from './http.service';
import StoreVisitResponse = StoreProto.StoreVisitResponse;
import StoreVisitRequest = StoreProto.StoreVisitRequest;
import StoreListResponse = StoreProto.StoreListResponse;
import StoreListRequest = StoreProto.StoreListRequest;

export class Main {
  private httpService = new HttpService('http://127.0.0.1:8080');

  constructor() {
    Main.addClickListener('visitStoreButton', () => this.visitStore());
    Main.addClickListener('getStoresButton', () => this.getStores());
  }

  private static addClickListener(id: string, listener: () => void) {
    const element = document.getElementById(id);
    element && element.addEventListener('click', listener);
  }

  visitStore(): void {
    const storeIdElement = this.getInput('storeId');
    const storeId = storeIdElement?.value ?? '12345';
    const customerElement = this.getInput('customer');
    const customer = customerElement?.value ?? 'Tom';
    this.httpService
      .request<StoreVisitResponse>(
        StoreVisitRequest.create({ customer: customer, id: storeId })
      )
      .then(
        (res) => console.log(res),
        (error) => console.log(error)
      );
  }

  getStores(): void {
    const indexElement = this.getInput('pageIndex');
    const pageIndex = Number(indexElement?.value ?? '1');
    const sizeElement = this.getInput('pageSize');
    const pageSize = Number(sizeElement?.value ?? '3');
    this.httpService
      .request<StoreListResponse>(
        StoreListRequest.create({
          pageable: { page: pageIndex, size: pageSize },
        })
      )
      .then(
        (res) => console.log(res),
        (error) => console.log(error)
      );
  }

  private getInput(id: string): HTMLInputElement {
    return document.getElementById(id) as HTMLInputElement;
  }
}

new Main();
