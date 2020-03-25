$(document).ready(() => {
    $(document).ready(function () {
        $("#exampleFormControlTextarea").keyup(function (e) {

                    //disable the submit button
                    var comment = $.trim($("#exampleFormControlTextarea").val());
                    if (comment == "") {
                        $("#btnSubmit").attr("disabled", true);
                    } else {
                        $("#btnSubmit").attr("disabled", false);
                    }
                    return true;
                });

    })
})
