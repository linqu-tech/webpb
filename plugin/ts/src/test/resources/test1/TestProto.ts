// Code generated by Webpb compiler, do not edit.
// https://github.com/linqu-tech/webpb

import * as Webpb from 'webpb';

import { Include2Proto } from './Include2Proto';
import { IncludeProto } from './IncludeProto';
export namespace TestProto {
  export enum Test3 {
    test3_1 = 'test3_1',
    test3_2 = 'test3_2',
    test3_3 = 'test3_3',
  }

  export enum Test5 {
    test5_1 = 'text1',
    test5_2 = 'text2',
    test5_3 = 'test5_3',
  }

    export interface IData {
    data1: string;
    data2?: number;
  }

  export class Data implements IData {
    data1!: string;
    data2?: number;
    webpbMeta: () => Webpb.WebpbMeta;

    private constructor(p?: IData) {
      Webpb.assign(p, this, []);
      this.webpbMeta = () => (p && {
        class: 'Data',
        method: '',
        context: '',
        path: ''
      }) as Webpb.WebpbMeta;
    }

    static create(properties: IData): Data {
      return new Data(properties);
    }
  }

  export interface ITest2 {
    test2: string;
    id: string;
    data: IData;
  }

  export class Test2 implements ITest2, Webpb.WebpbMessage {
    test2!: string;
    id!: string;
    data!: IData;
    webpbMeta: () => Webpb.WebpbMeta;

    private constructor(p?: ITest2) {
      Webpb.assign(p, this, []);
      this.webpbMeta = () => (p && {
        class: 'Test2',
        method: 'GET',
        context: '/test',
        path: `/test/${p.test2}${Webpb.query({
          id: p.id,
          data1: Webpb.getter(p, 'data.data1'),
          data2: Webpb.getter(p, 'data.data2'),
        })}`
      }) as Webpb.WebpbMeta;
    }

    static create(properties: ITest2): Test2 {
      return new Test2(properties);
    }
  }

  export interface ITest4 {
    test4: string;
  }

  export class Test4 implements ITest4 {
    test4!: string;
    webpbMeta: () => Webpb.WebpbMeta;

    private constructor(p?: ITest4) {
      Webpb.assign(p, this, []);
      this.webpbMeta = () => (p && {
        class: 'Test4',
        method: '',
        context: '',
        path: ''
      }) as Webpb.WebpbMeta;
    }

    static create(properties: ITest4): Test4 {
      return new Test4(properties);
    }
  }

  export interface ITest {
    test1: number;
    test2: IncludeProto.IMessage;
    test3: IncludeProto.Enum;
    test4?: ITest4;
    test5: { [k: string]: number };
    tests6: { [k: string]: IncludeProto.IMessage };
    test7: unknown;
    test8: Test.INestedTest;
    test9?: number;
    test10: string;
    test11: IncludeProto.IMessage[];
    test12: IncludeProto.Message.INested;
    test13: Include2Proto.IMessage[];
    test14: Include2Proto.Message.INested;
    test15: string;
    test16: Uint8Array;
    test17: Test.ITest17;
    test18: number;
    test19: string;
  }

  export class Test implements ITest, Webpb.WebpbMessage {
    test1!: number;
    test2!: IncludeProto.IMessage;
    test3!: IncludeProto.Enum;
    test4?: ITest4;
    test5!: { [k: string]: number };
    tests6!: { [k: string]: IncludeProto.IMessage };
    test7!: unknown;
    test8!: Test.INestedTest;
    test9?: number;
    test10!: string;
    test11!: IncludeProto.IMessage[];
    test12!: IncludeProto.Message.INested;
    test13!: Include2Proto.IMessage[];
    test14!: Include2Proto.Message.INested;
    test15!: string;
    test16!: Uint8Array;
    test17!: Test.ITest17;
    test18: number = 123;
    test19: string = "test19";
    webpbMeta: () => Webpb.WebpbMeta;

    private constructor(p?: ITest) {
      Webpb.assign(p, this, ["test1", "test9"]);
      this.webpbMeta = () => (p && {
        class: 'Test',
        method: 'GET',
        context: '/test',
        path: `/test/${p.test1}`
      }) as Webpb.WebpbMeta;
    }

    static create(properties: ITest): Test {
      return new Test(properties);
    }
  }

export namespace Test {
    export interface INestedTest {
      test1: number;
    }

    export class NestedTest implements INestedTest, Webpb.WebpbMessage {
      test1!: number;
      webpbMeta: () => Webpb.WebpbMeta;

      private constructor(p?: INestedTest) {
        Webpb.assign(p, this, []);
        this.webpbMeta = () => (p && {
          class: 'NestedTest',
          method: 'GET',
          context: '/test',
          path: `/test/nested/${p.test1}`
        }) as Webpb.WebpbMeta;
      }

      static create(properties: INestedTest): NestedTest {
        return new NestedTest(properties);
      }
    }

    export interface ITest17 {
      test: string;
    }

    export class Test17 implements ITest17 {
      test!: string;
      webpbMeta: () => Webpb.WebpbMeta;

      private constructor(p?: ITest17) {
        Webpb.assign(p, this, []);
        this.webpbMeta = () => (p && {
          class: 'Test17',
          method: '',
          context: '',
          path: ''
        }) as Webpb.WebpbMeta;
      }

      static create(properties: ITest17): Test17 {
        return new Test17(properties);
      }
    }
    }
}

