/**
 * Created by Pafker on 5/5/2015.
 */
/**
 * Created by Pafker on 5/5/2015.
 */
var getUserAnime = function () {
    var xhr = new XMLHttpRequest();
    xhr.addEventListener('readystatechange', animeResponse);

    xhr.open("POST", encodeURI("http://10.188.187.131:8080/AnimeRecommendationEngine/RequestHandler"), true);
    var params = "";
    var info = {};
    info["requestId"] = "Anime";
    info["requestType"] = "getWeightedAnimePredictions";
    info["userId"] = $("#username4").val();
    params += JSON.stringify(info);
    console.log(params);
    xhr.send(params);
    $(this).one("click", getUserAnime);
};