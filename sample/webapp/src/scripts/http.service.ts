import axios, { Method } from 'axios';
import { WebpbMessage } from 'webpb';
import { logger } from './logger';

export class HttpService {
  constructor(private baseUrl: string) {}

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
          if (res.status >= 200 && res.status < 400) {
            logger.log('\n====> Response:');
            logger.stringify(res.data);
            resolve && resolve(res.data);
          } else {
            logger.log('\n====> Error:');
            logger.stringify(res.data);
            reject && reject(res.data);
          }
        })
        .catch((error) => {
          logger.log('\n====> Error:');
          logger.stringify(error);
          reject && reject({ error: `Failed when request: ${url}` });
        })
    );
  }
}
