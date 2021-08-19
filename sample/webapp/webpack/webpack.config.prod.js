const Webpack = require('webpack');
const { merge } = require('webpack-merge');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');

const common = require('./webpack.common.js');

module.exports = merge(common, {
  bail: true,
  devtool: 'source-map',
  mode: 'production',
  module: {
    rules: [
      {
        exclude: /node_modules/,
        test: /\.js$/,
        use: 'babel-loader',
      },
      {
        exclude: /node_modules/,
        test: /\.ts$/,
        use: 'ts-loader',
      },
      {
        test: /\.s?css/i,
        use: [
          MiniCssExtractPlugin.loader,
          'css-loader',
          'postcss-loader',
          'sass-loader',
        ],
      },
    ],
  },
  output: {
    chunkFilename: 'js/[name].[chunkhash:8].chunk.js',
    filename: 'js/[name].[chunkhash:8].js',
  },
  plugins: [
    new Webpack.DefinePlugin({
      'process.env.NODE_ENV': JSON.stringify('production'),
    }),
    new MiniCssExtractPlugin({
      filename: 'bundle.css',
    }),
  ],
  stats: 'errors-only',
});
