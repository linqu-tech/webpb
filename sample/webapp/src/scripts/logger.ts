export class Logger {
  private readonly logger: HTMLDivElement;

  constructor() {
    this.logger = document.getElementById('logger') as HTMLDivElement;
  }

  reset(): void {
    this.logger && (this.logger.innerText = '');
  }

  log(log: string): void {
    this.logger && (this.logger.innerText += `${log}\n`);
  }

  stringify(obj: unknown): void {
    this.log(JSON.stringify(obj, null, 4));
  }
}

export const logger = new Logger();
