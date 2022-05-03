import { HttpService, Main } from '@scripts';
import { StoreListResponse, StoreVisitResponse } from '@proto/StoreProto';

describe('main', () => {
  const createElement = (id: string) => {
    const element = document.createElement('input');
    element.setAttribute('id', id);
    document.body.appendChild(element);
  };

  beforeAll(() => {
    createElement('visitStoreButton');
    createElement('getStoresButton');
  });

  it('when click visitStoreButton then get store success', () => {
    HttpService.prototype.request = jest
      .fn()
      .mockResolvedValue({ value: '11' });
    new Main();
    const element = document.getElementById('visitStoreButton') as HTMLElement;
    return element.click();
  });

  it('when click getStoresButton then get store list success', () => {
    HttpService.prototype.request = jest
      .fn()
      .mockResolvedValue({ value: '11' });
    new Main();
    const element = document.getElementById('getStoresButton') as HTMLElement;
    return element.click();
  });

  it('should get store success', () => {
    const res = StoreVisitResponse.create({
      greeting: 'Welcome, Tom',
      store: {
        city: '33',
        id: '11',
        name: '22',
      },
    });
    HttpService.prototype.request = jest.fn().mockResolvedValue(res);
    const main = new Main();
    main.visitStore();
    expect(HttpService.prototype.request).toBeCalledTimes(1);
  });

  it('should get store failed', () => {
    HttpService.prototype.request = jest
      .fn()
      .mockRejectedValue({ error: 'ERROR' });
    const main = new Main();
    main.visitStore();
    expect(HttpService.prototype.request).toBeCalledTimes(1);
  });

  it('should get stores success', () => {
    const res = StoreListResponse.create({
      paging: { page: 1, size: 10, totalCount: 123, totalPage: 13 },
      stores: [],
    });
    HttpService.prototype.request = jest.fn().mockResolvedValue(res);
    const main = new Main();
    main.getStores();
    expect(HttpService.prototype.request).toBeCalledTimes(1);
  });

  it('should get store failed', () => {
    HttpService.prototype.request = jest
      .fn()
      .mockRejectedValue({ error: 'ERROR' });
    const main = new Main();
    main.getStores();
    expect(HttpService.prototype.request).toBeCalledTimes(1);
  });

  it('given dom inputs when get store then return store', () => {
    createElement('storeId');
    createElement('customer');
    createElement('pageIndex');
    createElement('pageSize');
    HttpService.prototype.request = jest
      .fn()
      .mockRejectedValue({ error: 'ERROR' });
    const main = new Main();
    main.visitStore();
    main.getStores();
    expect(HttpService.prototype.request).toBeCalledTimes(2);
  });
});
