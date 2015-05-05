/**
 * Created by Pafker on 5/5/2015.
 */
/**
 * Created by Pafker on 5/5/2015.
 */
var getUsers = function(){
    var xhr = new XMLHttpRequest();
    xhr.addEventListener('readystatechange', userResponse);
    xhr.open("POST", "userList.json", true);
    var params = "data=";
    var info = {};
    info["username"] = $("#username3").val();
    usernameSaver = info["username"];
    params += JSON.stringify(info);
    xhr.send(params);
    $(this).one("click", getUsers);
};

var userResponse = function (evt) {
    var status = evt.target.status;
    if (evt.target.readyState == 4) {
        if (status == '200') {
            var result = JSON.parse(evt.target.responseText);
            if (result["success"] == true) {
                var content = "";
                var i;
                for (i = 0; i < result["content"].length; i++) {
                    if(i%3 == 0)
                    {
                        content += "<div class='row'>";
                    }
                    content += userDiv(result["content"][i]);
                    if(i%3 == 2) {
                        content += "</div>";
                    }
                }
                if(i%3 !=2)
                {
                    content+= "</div>";
                }
                $("#results-box").html(content);
                $(".user").one("click", userGet);
                standardize();
            }
            else {
                $("#results-box").html("<h1 style='color:red'>" + result["errorMessage"] + "</h1>");
            }
        }
        else {
            $("#results-box").html("<h1 style='color:red'>Connection could not be established</h1>")
        }
    }
};

var userDiv = function(obj){
    var div = "<div class='col-md-4 text-center'><div class='well'><a target='_blank' style='color:inherit;text-decoration:inherit;' href='";
    div += obj["link"]+"'><h3>";
    div += obj["name"];
    div +="</h3><div class='img-holder text-center'>";
    div +="<img class='img-holder' src='"+obj["img"]+"'></div></a><br>"
    div += "<input type='button' value='Get Suggestions' val='"+obj["name"]+"' class='user btn btn-default'";
    div += "</div></div>";
    return div;
};

var userGet = function()
{
    var xhr = new XMLHttpRequest();
    xhr.addEventListener('readystatechange', animeResponse);
    xhr.open("POST", "animeList.json", true);
    var params = "data=";
    var info = {};
    info["username"] = usernameSaver;
    info["otherUser"] = $(this).attr("val");
    params += JSON.stringify(info);
    xhr.send(params);
    $(this).one("click", userGet);
}