/**
 * Created by Pafker on 5/5/2015.
 */
var getList = function () {
    var xhr = new XMLHttpRequest();
    xhr.addEventListener('readystatechange', getListListener);
    xhr.open("POST", encodeURI("/AnimeRecommendationEngine/RequestHandler"), true);
    var params = "";
    var info = {};
    info["requestId"] = "Anime";
    info["requestType"] = "getWatched";
    info["userId"] = $("#username1").val();
    usernameSaver = info["userId"];
    params += JSON.stringify(info);
    xhr.send(params);
    $(this).one("click", getList);
};

var getListListener = function (evt) {
    var status = evt.target.status;
    if (evt.target.readyState == 4) {
        if (status == '200') {
            console.debug(evt.target.responseText);
            var result = JSON.parse(evt.target.responseText);
            console.debug("result");
            if (result["success"] == "true") {
                var dropdown = "<br><select id='drop' class='form-control'>";
                for (var i = 0; i < result["contentList"].length; i++) {
                    dropdown += "<option value='" + result["contentList"][i]["animeId"] + "'>" + result["contentList"][i]["animeTitle"] + "</option>";
                }
                dropdown += "</select><input id='getSimilarAnime' type='button' value='Get Similar Anime' class='btn btn-default'>";
                $("#dropdown-holder").html(dropdown);
                $("#getSimilarAnime").one("click",getSimilarAnime);
            }
            else {
                $("#results-box").html("<h1 style='color:red'>" + result["errorMessage"] + "</h1>");
                $("#dropdown-holder").html("");
            }
        }
        else {
            $("#results-box").html("<h1 style='color:red'>Connection could not be established</h1>");
            $("dropdown-holder").html("");
        }
    }
};
