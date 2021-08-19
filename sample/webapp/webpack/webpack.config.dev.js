const Path = require('path');
const Webpack = require('webpack');
const { merge } = require('webpack-merge');
const StylelintPlugin = require('stylelint-webpack-plugin');

const common = require('./webpack.common.js');

module.exports = merge(common, {
  devServer: {
    headers: {
      'Access-Control-Allow-Headers':
        'X-Requested-With, content-type, Authorization',
      'Access-Control-Allow-Methods': 'GET, POST, PUT, DELETE, PATCH, OPTIONS',
      'Access-Control-Allow-Origin': '*',
    },
    hot: true,
    inline: true,
    port: 4200,
  },
  devtool: 'eval-cheap-source-map',
  mode: 'development',
  module: {
    rules: [
      {
        enforce: 'pre',
        exclude: /node_modules/,
        loader: 'eslint-loader',
        options: {
          emitWarning: true,
        },
        test: /\.(js)$/,
      },
      {
        loader: 'html-loader',
        test: /\.html$/i,
      },
      {
        exclude: /node_modules/,
        loader: 'babel-loader',
        test: /\.(js)$/,
      },
      {
        exclude: /node_modules/,
        loader: 'ts-loader',
        test: /\.(ts)$/,
      },
      {
        test: /\.s?css$/i,
        use: [
          'style-loader',
          {
            loader: 'css-loader',
            options: {
              sourceMap: true,
            },
          },
          'postcss-loader',
          'sass-loader',
        ],
      },
    ],
  },
  output: {
    chunkFilename: 'js/[name].chunk.js',
  },
  plugins: [
    new Webpack.DefinePlugin({
      'process.env.NODE_ENV': JSON.stringify('development'),
    }),
    new StylelintPlugin({
      files: Path.join('src', '**/*.s?(a|c)ss'),
    }),
  ],
});
