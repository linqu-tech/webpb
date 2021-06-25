export interface WebpbMessage {
  webpbMeta(): WebpbMeta;
}

export interface WebpbMeta {
  class: string;

  method: string;

  context: string;

  path: string;
}

export function assign(src: any, dest: any, omitted?: string[]): void {
  if (src) {
    for (let ks = Object.keys(src), i = 0; i < ks.length; ++i) {
      if (src[ks[i]] != undefined && !isOmitted(ks[i], omitted)) {
        dest[ks[i]] = src[ks[i]];
      }
    }
  }
}

function isOmitted(k: string, omitted: string[] | undefined): boolean {
  if (!omitted) {
    return false;
  }
  for (const o of omitted) {
    if (o === k) {
      return true;
    }
  }
  return false;
}

export function getter(data: any, path: string): any {
  if (data === null || data === undefined) {
    return null;
  }
  if (typeof data !== 'object') {
    return null;
  }
  for (const k of path.split('.')) {
    data = data[k];
    if (data === null || data === undefined) {
      return null;
    }
  }
  return data;
}

export function query(pre: '?' | '&', params: { [key: string]: any }): string {
  let str = '';
  for (const key in params) {
    const v = params[key];
    if (v === null || v === undefined || v === '' || v.length === 0) {
      continue;
    }
    str += `${pre}${key}=${encodeURIComponent(v)}`;
    pre = '&';
  }
  return str;
}
