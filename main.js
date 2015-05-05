/**
 * Created by Pafker on 5/4/2015.
 */
var usernameSaver = "";
$(document).ready(function(){
    standardize();
    var x ={};
    $("#getList").one("click",getList);
    $("#getUsers").one("click", getUsers);
    $("#getAnime").one("click", getAnime);
    $("#getUsersAnime").one("click", getUserAnime);
})