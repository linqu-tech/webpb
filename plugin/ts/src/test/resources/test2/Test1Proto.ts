// Code generated by Webpb compiler, do not edit.
// https://github.com/linqu-tech/webpb

import * as Webpb from 'webpb';

export namespace Test1Proto {
  export interface ITest {
  }

  export class Test implements ITest {
    webpbMeta: () => Webpb.WebpbMeta;

    private constructor() {
      this.webpbMeta = () => ({
        class: 'Test',
        method: '',
        context: '',
        path: ''
      }) as Webpb.WebpbMeta;
    }

    static create(): Test {
      return new Test();
    }

    static fromAlias(_data?: Record<string, any>): Test {
      return Test.create();
    }

    toWebpbAlias(): any {
      return this;
    }
  }
}
