import { JSDOM } from "jsdom";

const jsdom = new JSDOM();
(global as any).document = jsdom.window.document;
(global as any).window = jsdom.window;
