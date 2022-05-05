import { Logger } from '../../src/scripts/logger';

const element = document.createElement('div') as HTMLDivElement;
element.setAttribute('id', 'logger');
document.body.appendChild(element);

describe('logger', () => {
  it('when reset logger then logger is empty', () => {
    const logger = new Logger();
    logger.reset();
    expect(element.innerText).toMatch('');
  });

  it('when log then logger with text', () => {
    const logger = new Logger();
    logger.log('hello');
    expect(element.innerText).toMatch('hello');
  });
});
