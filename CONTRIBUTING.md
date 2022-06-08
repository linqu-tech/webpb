# Contributing

## Conventional Commits

Check if your commit messages meet the [conventional commit format](https://conventionalcommits.org).

The conventional config extends from [config-conventional](https://github.com/conventional-changelog/commitlint/tree/master/%40commitlint/config-conventional).

### Type

Must be one of the following:

- **build**: Changes that affect the build system or external dependencies (example scopes**: gulp, broccoli, npm)
- **chore**: Other changes that don't modify src or test files
- **ci**: Changes to our CI configuration files and scripts (example scopes**: Travis, Circle, BrowserStack, SauceLabs)
- **docs**: Documentation only changes
- **feat**: A new feature
- **fix**: A bug fix
- **perf**: A code change that improves performance
- **refactor**: A code change that neither fixes a bug nor adds a feature
- **revert**: Reverts a previous commit
- **style**: Changes that do not affect the meaning of the code (white-space, formatting, missing semi-colons, etc)
- **test**: Adding missing tests or correcting existing tests

## Create a commit

Run `npm install` in root directory, then you will get [Commitizen](https://github.com/commitizen-tools/commitizen) installed.

Use `npm run cz` or `npx cz` create a commit.

## Workflow validation

Commit message will be validated by workflow. If the validation is fail, amend the commit and rerun validation action.
