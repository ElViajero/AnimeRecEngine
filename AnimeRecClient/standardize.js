/**
 * Created by Pafker on 5/5/2015.
 */
var standardize = function(){
    boxes = $('.well');
    maxWidth = Math.max.apply(
        Math, boxes.map(function() {
            return $(this).width();
        }).get());
    $(".img-holder").width(maxWidth-5);
    $(".img-holder").height(maxWidth-5);
    boxes = $('.well');
    maxHeight = Math.max.apply(
        Math, boxes.map(function() {
            return $(this).height();
        }).get());
    boxes.height(maxHeight);
};