// Code generated by Webpb compiler, do not edit.
// https://github.com/linqu-tech/webpb

import * as Webpb from 'webpb';

export namespace Test4Proto {
  export interface ITest {
    test1: string;
  }

  export class Test implements ITest {
    test1!: string;
    webpbMeta: () => Webpb.WebpbMeta;
    toWebpbAlias = () => this;

    private constructor(p?: ITest) {
      Webpb.assign(p, this, []);
      this.webpbMeta = () => (p && {
        class: 'Test',
        method: '',
        context: '',
        path: ''
      }) as Webpb.WebpbMeta;
    }

    static create(properties: ITest): Test {
      return new Test(properties);
    }
  }
}
