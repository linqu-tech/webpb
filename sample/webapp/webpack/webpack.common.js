const Path = require('path');
const { CleanWebpackPlugin } = require('clean-webpack-plugin');
const HtmlWebpackPlugin = require('html-webpack-plugin');

module.exports = {
  entry: {
    app: Path.resolve(__dirname, '../src/scripts/index.ts'),
    styles: Path.resolve(__dirname, '../src/styles/index.scss'),
  },
  module: {
    rules: [
      {
        include: /node_modules/,
        test: /\.mjs$/,
        type: 'javascript/auto',
      },
      {
        // eslint-disable-next-line max-len
        test: /\.(ico|jpg|jpeg|png|gif|eot|otf|webp|svg|ttf|woff|woff2)(\?.*)?$/,
        use: {
          loader: 'file-loader',
          options: {
            name: '[path][name].[ext]',
          },
        },
      },
    ],
  },
  optimization: {
    splitChunks: {
      chunks: 'all',
      name: false,
    },
  },
  output: {
    filename: 'js/[name].js',
    path: Path.join(__dirname, '../dist'),
  },
  plugins: [
    new CleanWebpackPlugin({
      cleanStaleWebpackAssets: false,
    }),
    new HtmlWebpackPlugin({
      template: Path.resolve(__dirname, '../src/index.html'),
    }),
  ],
  resolve: {
    alias: {
      '@proto': Path.resolve(
        __dirname,
        '../build/generated/source/proto/main/ts'
      ),
      '@scripts': Path.resolve(__dirname, '../src/scripts'),
      build: Path.resolve(__dirname, '../build'),
    },
    extensions: ['.ts', '.js'],
    preferRelative: true,
  },
};
