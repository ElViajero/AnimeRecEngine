/**
 * Created by Pafker on 5/5/2015.
 */
var getList = function () {
    var xhr = new XMLHttpRequest();
    xhr.addEventListener('readystatechange', getListListener);
    xhr.open("POST", "watchedAnime.json", true);
    var params = "data=";
    var info = {};
    info["username"] = $("#username1").val();
    usernameSaver = $("#username1").val();
    params += JSON.stringify(info);
    xhr.send(params);
    $(this).one("click", getList);
};

var getListListener = function (evt) {
    var status = evt.target.status;
    if (evt.target.readyState == 4) {
        if (status == '200') {
            var result = JSON.parse(evt.target.responseText);
            if (result["success"] == true) {
                var dropdown = "<br><select id='drop' class='form-control'>";
                for (var i = 0; i < result["content"].length; i++) {
                    dropdown += "<option value='" + result["content"][i]["id"] + "'>" + result["content"][i]["name"] + "</option>";
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
            $("#results-box").html("<h1 style='color:red'>Connection could not be established</h1>")
            $("dropdown-holder").html("");
        }
    }
};