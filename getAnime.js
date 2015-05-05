/**
 * Created by Pafker on 5/5/2015.
 */
var getAnime = function () {

    var xhr = new XMLHttpRequest();
    xhr.addEventListener('readystatechange', animeResponse);
    xhr.open("POST", "animeList.json", true);
    var params = "data=";
    var info = {};
    info["username"] = $("#username2");
    params += JSON.stringify(info);
    xhr.send(params);
    $(this).one("click", getAnime);
};