// Code generated by Webpb compiler, do not edit.
// https://github.com/linqu-tech/webpb

import * as Webpb from 'webpb';

export namespace Test10Proto {
  export interface ITest {
    test1: number;
  }

  export class Test implements ITest {
    test1!: number;
    webpbMeta: () => Webpb.WebpbMeta;

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

    static fromAlias(data: Record<string, any>): Test {
      return Test.create(data as any);
    }

    toWebpbAlias(): any {
      return this;
    }
  }
}
