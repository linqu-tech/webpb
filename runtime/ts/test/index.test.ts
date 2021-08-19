import { assign, getter, query, toAlias } from '../src';

describe('index', () => {
  it('should assign to null success', () => {
    expect(() => assign(null, {})).not.toThrow();
  });

  it('should assign to object success', () => {
    const dest = {};
    const src = { a: 1 };
    assign(src, dest);
    expect(dest).toMatchObject(src);
  });

  it('should assign to object with omitted success', () => {
    const dest = {};
    const src = { a: 1, b: 2 };
    assign(src, dest, ['b']);
    expect(dest).toMatchObject({ a: 1 });
  });

  it('should getter return null when data is null or undefined', () => {
    expect(getter(null, 'a')).toEqual(null);
    expect(getter(undefined, 'a')).toEqual(null);
  });

  it('should getter return null when data not object', function () {
    expect(getter('', 'a')).toEqual(null);
    expect(getter([], 'a')).toEqual(null);
    expect(getter(0, 'a')).toEqual(null);
  });

  it('should getter return value success', function () {
    expect(getter({ a: 1 }, 'a')).toEqual(1);
    expect(getter({ a: { b: 1 } }, 'a.b')).toEqual(1);
    expect(getter({ a: 1 }, 'b')).toEqual(null);
  });

  it('should format query success', function () {
    expect(query('?', {})).toEqual('');
    expect(query('&', {})).toEqual('');
    expect(query('', { a: 1 })).toEqual('a=1');
    expect(query('?', { a: 1 })).toEqual('?a=1');
    expect(query('?', { a: 1, b: 2 })).toEqual('?a=1&b=2');
    expect(query('?', { a: 1, b: 2, c: null, d: undefined, e: '' })).toEqual('?a=1&b=2');
    expect(query('?', { a: [], b: 2 })).toEqual('?b=2');
    expect(query('?', { a: [1] })).toEqual('?a=1');
    expect(query('?', { a: [1, 2] })).toEqual('?a=1%2C2');
  });

  it('should format query ignore function', function () {
    expect(query('', { a: 1, b: () => 'hello' })).toEqual('a=1');
  });

  it('should to alias success', function () {
    const b: Record<string, unknown> = { b: 2, c: 3 };
    b['toAlias'] = () => toAlias(b, { b: 'b_' });
    const a: Record<string, unknown> = { a: 1, b: b, c: 3 };
    expect(toAlias(a, { a: 'a_' })).toMatchObject({
      a_: 1, b: { b_: 2, c: 3 }, c: 3
    });
    expect(toAlias(a, {})).toMatchObject({
      a: 1, b: { b_: 2, c: 3 }, c: 3
    });
  });

  it('should to alias change nothing', function () {
    expect(toAlias(null, {})).toEqual(null);
    expect(toAlias('', {})).toEqual('');
    expect(toAlias([], {})).toMatchObject([]);
    expect(toAlias([1, 2], {})).toMatchObject([1, 2]);
  });
});
