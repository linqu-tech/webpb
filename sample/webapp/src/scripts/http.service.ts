import axios, { Method } from 'axios';
import { WebpbMessage } from 'webpb';
import { logger } from './logger';

export class HttpService {
  constructor(private baseUrl: string) {
  }

  request<T extends WebpbMessage>(request: WebpbMessage): Promise<T> {
    logger.reset();
    const meta = request.webpbMeta();
    const url = `${this.baseUrl}${meta.path}`;
    logger.log(`====> Request (${url}):`);
    logger.stringify(request);
    return new Promise((resolve, reject) =>
      axios
        .request({
          baseURL: this.baseUrl,
          data: JSON.stringify(request),
          headers: { 'Content-Type': 'application/json; charset=UTF-8' },
          method: meta.method as Method,
          url: meta.path,
        })
        .then((res) => {
          console.log(res);
          logger.log('\n====> Response:');
          logger.stringify(res.data);
          resolve && resolve(res.data);
        })
        .catch((error) => {
          logger.log('\n====> Error:');
          if (error.response) {
            const res = error.response;
            logger.stringify({
              data: res.data,
              headers: res.headers,
              status: res.status,
            });
          } else {
            logger.stringify(error);
          }
          reject && reject({ error: `Failed when request: ${url}` });
        })
    );
  }
}
