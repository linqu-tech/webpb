module.exports = {
  env: {
    es6: true,
    jest: true,
    node: true,
  },
  extends: [
    "plugin:@typescript-eslint/recommended",
    "plugin:prettier/recommended",
  ],
  parser: "@typescript-eslint/parser",
  plugins: ["import", "import-quotes", "no-null"],
  root: true,
  rules: {
    "@typescript-eslint/camelcase": "off",
    "@typescript-eslint/member-delimiter-style": "off",
    "@typescript-eslint/no-use-before-define": "off",
    "@typescript-eslint/semi": "off",
    "import-quotes/import-quotes": [1, "single"],
    "import/newline-after-import": "error",
    "import/order": [
      "error",
      {
        groups: [
          ["builtin", "external"],
          "internal",
          ["index", "parent", "sibling"],
        ],
      },
    ],
    "max-len": [
      "error",
      {
        code: 80,
        ignoreStrings: true,
        ignoreUrls: true,
      },
    ],
    "require-jsdoc": [
      "error",
      {
        require: {
          ArrowFunctionExpression: false,
          ClassDeclaration: false,
          FunctionDeclaration: false,
          FunctionExpression: false,
          MethodDefinition: false,
        },
      },
    ],
    semi: "off",
    "sort-keys": ["error"],
  },
}
