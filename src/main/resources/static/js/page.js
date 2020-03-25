

var selectedDocuments = new Set();

var title;
function handTextInput(element){
    let comment = element.value;
    let index = element.tabIndex;
    let btn = $(".btnSubmit")[index];
    if (comment == "") {
        btn.setAttribute("disabled", true);
    } else {
        btn.removeAttribute("disabled");
    }
}

$(document).ready(() => {
    $(document).ready(function () {

        title = $("#pageTitle").text();

        $('.tab-link').on("click", (event) =>{
            var tabs = Array.from(event.target.parentElement.parentElement.children)
            tabs.forEach((v,i) => v.classList.remove("active"));
            event.target.parentElement.classList.add("active");
        })

        $('#tabBtn-0')[0].children[0].click();

        $("#addTabBtn").on("click", (event) =>{
        let origin = window.location.origin;
        let url = origin;
        if (origin.includes("java-bootcamp")) {
            url += '/page';
        }
        url += '/' + title;;

            let tabName = $("#tabNameInput").val();
                        $.ajax({
                            url: url,
                            type: 'POST',
                            data: tabName,
                            contentType: "application/json",
                            success: result => {
                                window.location = url;
                            }
                        });
        })

        $(".edit-button").on("click", (event) => {
            var editElement = $("#edit-" + event.target.name);
            var viewElement = $("#view-" + event.target.name);
            editElement.show();
            viewElement.hide();
        })

        $(".edit-cancel-button").on("click", (event) => {
            var editElement = $("#edit-" + event.target.name);
            var viewElement = $("#view-" + event.target.name);
            viewElement.show();
            editElement.hide();
        })

        $(".edit-save-button").on("click", (event) => {
            let blockId = event.target.name;
            let tabIndex = event.target.tabIndex;
            let name = $("#name-" + blockId).val();
            let text = $("#text-" + blockId).val();
            let updateRequest = {
                username: name,
                content: text
            };
            let origin = window.location.origin;
            let url = origin;
            if (origin.includes("java-bootcamp")) {
                url += '/page';
            }
            let currentUrl = url + '/' + title;
            url = currentUrl + "/"+tabIndex+ "/" + blockId;
            $.ajax({
                url: url,
                type: 'PUT',
                data: JSON.stringify(updateRequest),
                contentType: "application/json",
                success: result => {
                     window.location = currentUrl;
                }, error: result => {
                    $("#name-error-" + blockId).show();
                }
            });
        })
    });
})