// Code generated by Webpb compiler, do not edit.
// https://github.com/linqu-tech/webpb

import * as Webpb from 'webpb';

export namespace IgnoredProto {
  export interface IIgnoreTest {
    test1: number;
  }

  export class IgnoreTest implements IIgnoreTest {
    test1!: number;
    webpbMeta: () => Webpb.WebpbMeta;
    toWebpbAlias = () => this;

    private constructor(p?: IIgnoreTest) {
      Webpb.assign(p, this, []);
      this.webpbMeta = () => (p && {
        class: 'IgnoreTest',
        method: '',
        context: '',
        path: ''
      }) as Webpb.WebpbMeta;
    }

    static create(properties: IIgnoreTest): IgnoreTest {
      return new IgnoreTest(properties);
    }
  }
}
