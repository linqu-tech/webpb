[![CI](https://github.com/linqu-tech/webpb/actions/workflows/ci.yml/badge.svg)](https://github.com/linqu-tech/webpb/actions/workflows/ci.yml)
[![Coverage Status](https://coveralls.io/repos/github/linqu-tech/webpb/badge.svg?branch=master)](https://coveralls.io/github/linqu-tech/webpb?branch=master)
[![License](http://img.shields.io/:license-apache-brightgreen.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

# webpb

Generate api definitions for web from protocol buffers file

## Run samples

Checkout source code from the repo

### Spring sample

Run command in project root directory:

```shell
./gradlew sample:spring:bootRun
```

or in Windows

```shell
./gradlew.bat sample:spring:bootRun
```

### Webapp sample

Install [node.js](https://nodejs.org/en/) first, then use `npm` or `yarn` to run the command.

```shell
cd sample/webapp
npm install
npm start
```

Open webapp link http://localhost:4200

## Contributing

### Conventional Commits

Check if your commit messages meet the [conventional commit format](https://conventionalcommits.org).

The conventional config extends from [config-conventional](https://github.com/conventional-changelog/commitlint/tree/master/%40commitlint/config-conventional).

- `type` is as default
- The following is the list of supported scopes:
  - `aggregation`
  - `libs.commons`
  - `libs.tests`
  - `libs.utilities`
  - `libs`
  - `plugins.dump`
  - `plugins.java`
  - `plugins.ts`
  - `plugins`
  - `proto`
  - `runtime.java`
  - `runtime.processor`
  - `runtime.ts`
  - `sample`
  - `webpb`

### Create commit by [Commitizen](https://github.com/commitizen-tools/commitizen)

Run `npm install` in root directory, then use `npm run cz` or just `git commit` to create commit.

