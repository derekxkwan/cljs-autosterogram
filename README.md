# cljs-autostereogram

An implementation of **Displaying 3D Images: Algorithms for Single Image Random Dot Stereograms** by Harold W. Thimbleby, Ian H. Witten, and Stuart Inglis in `ClojureScript` and `Quil`.


I've previously implemented this in my `pjava-autostereogram` project for an upcoming art installation and am porting it to cljs (also for said installation).


## Progress

Haven't gotten quite there yet, have to implement actual algorithm...

## Usage

Run `lein figwheel` in your terminal. Wait for a while until you see `Successfully compiled "resources/public/js/main.js"`. Open [localhost:3449](http://localhost:3449) in your browser.

You can use this while developing your sketch. Whenever you save your source files the browser will automatically refresh everything, providing you with quick feedback. For more information about Figwheel, check the [Figwheel repository on GitHub](https://github.com/bhauman/lein-figwheel).

## Publishing your sketch

Before you publish your sketch, run `lein cljsbuild once optimized`. This will compile your code and run Google Closure Compiler with advanced optimizations. Take `resources/public/index.html` and `resources/public/js/main.js` and upload them to server of your choice.

## License

gpl v 3
