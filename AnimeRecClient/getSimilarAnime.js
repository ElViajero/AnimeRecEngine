/**
 * Created by Pafker on 5/5/2015.
 */
var getSimilarAnime = function () {
    var xhr = new XMLHttpRequest();
    xhr.addEventListener('readystatechange', animeResponse);
    xhr.open("POST", encodeURI("http://10.188.187.131:8080/AnimeRecommendationEngine/RequestHandler"), true);
    var params = "";
    var info = {};
    info["requestId"] = "Anime";
    info["requestType"] = "getSimilar";
    info["userId"] = $("#username1").val();
    info["animeId"] = $("#drop").val();
    params += JSON.stringify(info);
    xhr.send(params);
    $(this).one("click", getSimilarAnime);
};

