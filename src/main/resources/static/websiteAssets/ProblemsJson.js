checkPageSizes();
changePageAndSize();
searchProblemsOnEnterKeyPressed();


function openProblemModal(id){

    // $("ul#messageArea > li").remove();
    // $.getJSON("/getProblem?id=" + id , function (data) {
        // $('#descriptionImage' + id).attr('src', 'data:image/png;base64,' + data.descriptionDro);
        // $('#resultImage' + id).attr('src', 'data:image/png;base64,' + data.resultDto)
        // alert(data)
        // $.each(data, function (key, value) {
        //     // $('#messageArea').append();
        //     $('#descriptionImage' + id).attr('src', 'data:image/png;base64,' + value['descriptionDto']);
        //     $('#resultImage' + id).attr('src', 'data:image/png;base64,' + value['resultDto'])
        //
        // });
    // });

    $('#modal' + id).modal('show')

    // $.ajax({
    //     url: "/getProblem?id=" + id
    //
    // }).done(function (response) {
    //     $('#descriptionImage' + id).attr('src', 'data:image/png;base64,' + response.descriptionDro);
    //     $('#resultImage' + id).attr('src', 'data:image/png;base64,' + response.resultDto);
    //     $('#modal' + id).modal('show')
    // });
}
//If the page size option is greater than total number of elements (users) - disable it
function checkPageSizes() {
    var pageSizesToShow = $('#pageSizesToShow').data('pagesizestoshow');

    $("#pageSizeSelect option").each(function (i, option) {
        if ($.inArray(parseInt(option.value), pageSizesToShow) === -1) {
            option.disabled = true;
        }
    });
}

function changePageAndSize() {
    let selectedProperty = $("#search-problem-dropdown option:selected").text();
    let value = $("#searchProblemBar").val();

    $('#pageSizeSelect').change(function (evt) {
        window.location.replace("/problems?problemsProperty=" + selectedProperty + "&propertyValue=" + value + "&pageSize=" + this.value + "&page=1");

    });
}

function searchProblemsOnEnterKeyPressed(){
    $("#searchProblemBar").keypress(function (event) {
        if (event.which === 13) {
            searchProblemByProperty();
        }
    });
}

function searchProblemByProperty() {
    let selectedProperty = $("#search-problem-dropdown option:selected").val();
    // let selectedProperty = $("#search-problem-dropdown").val();
    let value = $("#searchProblemBar").val();

    if (value != null && value !== "") {
        window.location.href = "/problems?problemsProperty=" + selectedProperty + "&propertyValue=" + value;
    }

    else {
        window.location.href = "/problems";
    }
}

function sortTable(n) {
    let table, rows, switching, i, x, y, shouldSwitch, dir, switchcount = 0;
    table = document.getElementById("problemsTable");
    switching = true;
    //Set the sorting direction to ascending:
    dir = "asc";
    /*Make a loop that will continue until
    no switching has been done:*/
    while (switching) {
        //start by saying: no switching is done:
        switching = false;
        rows = table.getElementsByTagName("TR");
        /*Loop through all table rows (except the
        first, which contains table headers):*/
        for (i = 1; i < (rows.length - 1); i++) {
            //start by saying there should be no switching:
            shouldSwitch = false;
            /*Get the two elements you want to compare,
            one from current row and one from the next:*/
            x = rows[i].getElementsByTagName("TD")[n];
            y = rows[i + 1].getElementsByTagName("TD")[n];
            /*check if the two rows should switch place,
            based on the direction, asc or desc:*/

            if (dir == "asc") {
                //if user clicks on id column, compare numbers
                if (n === 0) {
                    //compare numbers
                    if (Number(x.innerHTML) > Number(y.innerHTML)) {
                        //if so, mark as a switch and break the loop:
                        shouldSwitch = true;
                        break;
                    }
                }
                else if (x.innerHTML.toLowerCase() > y.innerHTML.toLowerCase()) {
                    //if so, mark as a switch and break the loop:
                    shouldSwitch= true;
                    break;
                }
            } else if (dir == "desc") {
                //if user clicks on id column, compare numbers
                if (n === 0) {
                    //compare numbers
                    if (Number(x.innerHTML) < Number(y.innerHTML)) {
                        //if so, mark as a switch and break the loop:
                        shouldSwitch = true;
                        break;
                    }
                }
                else if (x.innerHTML.toLowerCase() < y.innerHTML.toLowerCase()) {
                    //if so, mark as a switch and break the loop:
                    shouldSwitch = true;
                    break;
                }
            }
        }
        if (shouldSwitch === true) {
            /*If a switch has been marked, make the switch
            and mark that a switch has been done:*/
            rows[i].parentNode.insertBefore(rows[i + 1], rows[i]);
            switching = true;
            //Each time a switch is done, increase this count by 1:
            switchcount ++;
        } else {
            /*If no switching has been done AND the direction is "asc",
            set the direction to "desc" and run the while loop again.*/
            if (switchcount == 0 && dir == "asc") {
                dir = "desc";
                switching = true;
            }
        }
    }
}



