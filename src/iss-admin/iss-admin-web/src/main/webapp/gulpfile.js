/**
 * Created by apple on 16/7/3.
 */
var gulp = require('gulp'),
    jshint = require('gulp-jshint'),
    fse = require('fs-extra'),
    rename = require('gulp-rename'),
    sass = require('gulp-sass'),
    concatcss = require('gulp-concat-css'),
    minifycss = require('gulp-minify-css'),
    argv = require('yargs').argv;

var isProduct = false;

// 语法检查
gulp.task('js-hint', function () {
    return gulp.src('src/*.js')
        .pipe(jshint())
        .pipe(jshint.reporter('default'));
});

// 拷贝依赖库
gulp.task("copy-dep", function() {
    gulp.src('node_modules/jquery/dist/jquery*.js').pipe(gulp.dest('dist'));
});

gulp.task("sass",function () {
    return gulp.src('src/**/*.sass')
        .pipe(sass())
        .pipe(concatcss('admin.css'))
        // .pipe(minifycss())
        .pipe(gulp.dest('dist'));
});

// 任务入口
gulp.task('default', function() {
    isProduct = argv.p;
    console.log('正在处理：' + (isProduct ? 'release' : 'debug') + '环境');
    gulp.start('sass');
    gulp.start('js-hint');
    gulp.start('copy-dep');
});