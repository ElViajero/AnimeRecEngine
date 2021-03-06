/**
 * Created by Pafker on 5/5/2015.
 */
var animeResponse = function (evt) {
    var status = evt.target.status;
    if (evt.target.readyState == 4) {
        if (status == '200') {
            console.log(evt.target.responseText);
            var result = JSON.parse(evt.target.responseText);
            if (result["success"] == "true") {
                var content = "";
                var i;
                for (i = 0; i < result["contentList"].length; i++) {
                    if(i%3 == 0)
                    {
                        content += "<div class='row'>";
                    }
                    content += animeDiv(result["contentList"][i]);
                    if(i%3 == 2) {
                        content += "</div>";
                    }
                }
                if(i%3 !=2)
                {
                    content+= "</div>";
                }
                $("#results-box").html(content);
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

var animeDiv = function(obj){
    var div = "<div class='col-md-4 text-center'><div class='well'><a target='_blank' style='color:inherit;text-decoration:inherit;' href='";
    div += "http://myanimelist.net/"+obj["animeLink"]+"'><h3>";
    div += obj["animeTitle"];
    div +="</h3><div class='img-holder text-center'>";
//    div +="<img class='img-holder' src='"+obj["img"]+"'></div>"
    div += "<img class ='img-holder' src='noimage.png'></div>"
    //div += obj["desc"].substr(0,80) +"...";
    div += "Not collecting descriptions yet".substr(0,80) +"...";
    if(parseFloat(obj["score"]) > 1)
    {
        div += "<br>Predicted rating for you:"+obj["score"];
    }
    div += "</a></div></div>";
    return div;
};