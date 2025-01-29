const path = require('path');

module.exports = {
    entry: './src/index.tsx', // TypeScript 사용 시 .tsx 확장자 사용
    output: {
        path: path.resolve(__dirname, 'dist'),
        filename: '[name].[contenthash].js', // 콘텐츠 해시를 사용하여 캐싱 최적화
    },
    resolve: {
        extensions: ['.tsx', '.ts', '.js'], // 지원하는 확장자 설정
        alias: {
            '@': path.resolve(__dirname, 'src'), // 별칭 설정
        },
    },
    module: {
        rules: [
            {
                test: /\.tsx?$/,
                use: 'babel-loader',
                exclude: /node_modules/,
            },
            {
                test: /\.css$/,
                use: ['style-loader', 'css-loader'],
            },
            {
                test: /\.(png|jpe?g|gif|svg)$/i,
                use: [
                    {
                        loader: 'url-loader',
                        options: {
                            limit: 8192,
                        },
                    },
                ],
            },
            // HTML 로더 추가 (필요한 경우)
            {
                test: /\.html$/,
                use: ['html-loader'],
            },
        ],
    },
    plugins: [
        // 플러그인 추가 (예: HtmlWebpackPlugin)
        new HtmlWebpackPlugin({
            template: './public/index.html',
        }),
    ],
};