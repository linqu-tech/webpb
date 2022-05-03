const { pathsToModuleNameMapper } = require('ts-jest');
const { compilerOptions } = require('./tsconfig');

module.exports = {
  bail: true,
  collectCoverage: true,
  collectCoverageFrom: ['src/**/*.ts'],
  coverageThreshold: {
    global: {
      branches: 100,
      functions: 100,
      lines: 100,
      statements: 100,
    },
  },
  errorOnDeprecated: true,
  moduleNameMapper: pathsToModuleNameMapper(compilerOptions.paths, {
    prefix: '<rootDir>/',
  }),
  preset: 'ts-jest',
  rootDir: './',
  setupFiles: ['./test/setup/jsdom.ts'],
  testEnvironment: 'node',
  testMatch: ['<rootDir>/test/**/*(*.)@(test).ts'],
};
