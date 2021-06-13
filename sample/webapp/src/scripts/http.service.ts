import { WebpbMessage } from 'webpb';

export class HttpService {
  constructor(private baseUrl: string) {}

  request<T extends WebpbMessage>(request: WebpbMessage): Promise<T> {
    const meta = request.webpbMeta();

    const xhr = new XMLHttpRequest();
    const url = `${this.baseUrl}${meta.path}`;
    xhr.open(meta.method, url, true);
    xhr.setRequestHeader('Content-Type', 'application/json; charset=UTF-8');
    const promise = new Promise<T>((resolve, reject) => {
      xhr.onload = () => {
        if (xhr.status >= 200 && xhr.status < 400) {
          const response = JSON.parse(xhr.response) as T;
          resolve && resolve(response);
        } else {
          reject && reject({ error: `Failed when request: ${url}` });
        }
      };
      xhr.onerror = () => {
        reject && reject({ error: `Failed when request: ${url}` });
      };
    });
    xhr.send(JSON.stringify(request));
    return promise;
  }
}
