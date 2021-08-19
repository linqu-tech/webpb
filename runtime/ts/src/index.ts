export interface WebpbMessage {
  webpbMeta(): WebpbMeta;

  toWebpbAlias(): any;
}

export interface WebpbMeta {
  class: string;

  method: string;

  context: string;

  path: string;
}

export function assign(src: any, dest: any, omitted?: string[]): void {
  if (src && typeof src === 'object' && typeof dest === 'object') {
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

export function query(pre: string, params: { [key: string]: any }): string {
  let str = '';
  for (const key of Object.keys(params)) {
    const v = params[key];
    if (v === null || v === undefined || v === '' || typeof v === 'function') {
      continue;
    }
    const encoded = encodeURIComponent(v);
    if (encoded) {
      str += `${pre}${key}=${encodeURIComponent(v)}`;
      pre = '&';
    }
  }
  return str;
}

export function toAlias(data: any, aliases: { [key: string]: string }): any {
  if (!data || typeof data !== 'object' || Array.isArray(data)) {
    return data;
  }
  const obj: Record<string, unknown> = {};
  for (const key of Object.keys(data)) {
    const value = data[key];
    const toAlias = value['toAlias'];
    if (typeof toAlias === 'function') {
      obj[key] = toAlias();
    } else if (aliases && aliases[key]) {
      obj[aliases[key]] = value;
    } else {
      obj[key] = value;
    }
  }
  return obj;
}
