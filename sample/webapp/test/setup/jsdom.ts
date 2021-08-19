import { JSDOM } from 'jsdom';

const jsdom = new JSDOM();
(global as { document: unknown }).document = jsdom.window.document;
(global as { window: unknown }).window = jsdom.window;
