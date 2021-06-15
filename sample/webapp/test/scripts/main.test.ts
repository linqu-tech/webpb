import { HttpService, Main } from '@scripts';
import { StoreProto } from '@proto';
import StoreVisitResponse = StoreProto.StoreVisitResponse;
import StoreListResponse = StoreProto.StoreListResponse;

describe('main', () => {

  const createElement = (id: string) => {
    const element = document.createElement('input');
    element.setAttribute('id', id);
    document.body.appendChild(element);
  };

  beforeAll(() => {
    createElement('getStoreButton');
    createElement('getStoresButton');
  });

  it('when click getStoreButton then get store success', () => {
    HttpService.prototype.request = jest.fn().mockResolvedValue({ value: '11' });
    new Main();
    const element = document.getElementById('getStoreButton');
    return element.click();
  });

  it('when click getStoresButton then get store list success', () => {
    HttpService.prototype.request = jest.fn().mockResolvedValue({ value: '11' });
    new Main();
    const element = document.getElementById('getStoresButton');
    return element.click();
  });

  it('should get store success', () => {
    const res = StoreVisitResponse.create({
      greeting: 'Welcome, Tom',
      store: {
        id: '11',
        name: '22',
        city: '33'
      }
    });
    HttpService.prototype.request = jest.fn().mockResolvedValue(res);
    const main = new Main();
    main.getStore();
    expect(HttpService.prototype.request).toBeCalledTimes(1);
  });

  it('should get store failed', () => {
    HttpService.prototype.request = jest.fn().mockRejectedValue({ error: 'ERROR' });
    const main = new Main();
    main.getStore();
    expect(HttpService.prototype.request).toBeCalledTimes(1);
  });

  it('should get stores success', () => {
    const res = StoreListResponse.create({
      paging: { page: 1, size: 10, totalCount: 123, totalPage: 13 },
      stores: []
    });
    HttpService.prototype.request = jest.fn().mockResolvedValue(res);
    const main = new Main();
    main.getStores();
    expect(HttpService.prototype.request).toBeCalledTimes(1);
  });

  it('should get store failed', () => {
    HttpService.prototype.request = jest.fn().mockRejectedValue({ error: 'ERROR' });
    const main = new Main();
    main.getStores();
    expect(HttpService.prototype.request).toBeCalledTimes(1);
  });
});
