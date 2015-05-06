/**
 * Created by Pafker on 5/5/2015.
 */
/**
 * Created by Pafker on 5/5/2015.
 */
var getUserAnime = function () {
    var xhr = new XMLHttpRequest();
    xhr.addEventListener('readystatechange', animeResponse);
    xhr.open("POST", "animeList.json", true);
    var params = "data=";
    var info = {};
    info["username"] = $("#username4");
    params += JSON.stringify(info);
    xhr.send(params);
    $(this).one("click", getUserAnime);
};