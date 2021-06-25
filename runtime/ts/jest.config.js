module.exports = {
  bail: true,
  collectCoverage: true,
  collectCoverageFrom: ['src/**/*.ts'],
  errorOnDeprecated: true,
  preset: 'ts-jest',
  rootDir: './',
  testEnvironment: 'node',
  testMatch: ['<rootDir>/test/**/*(*.)@(test).ts'],
  coverageThreshold: {
    global: {
      branches: 100,
      functions: 100,
      lines: 100,
      statements: 100,
    },
  },
};
