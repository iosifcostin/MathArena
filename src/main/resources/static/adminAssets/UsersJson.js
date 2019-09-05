$(document).ready(function() {
    checkPageSizes();
    changePageAndSize();
    searchUsersOnEnterKeyPressed();
    keepSearchParametersAfterPageRefresh();
});

function changePageAndSize() {
    $('#pageSizeSelect').change(function(evt) {
        let selectedProperty = $("#search-user-dropdown option:selected").text();
        let value = $("#searchUserBar").val();

        if (value != null && value !== "") {
            window.location.replace("/admin/users?usersProperty=" + selectedProperty + "&propertyValue=" + value + "&pageSize=" + this.value + "&page=1");
        }

        else{
            window.location.replace("/admin/users?pageSize=" + this.value + "&page=1");
        }
    });
}

function searchUsersOnEnterKeyPressed(){
    $("#searchUserBar").keypress(function (event) {
        if (event.which === 13) {
            searchUserByProperty();
        }
    });
}

function saveSearchParameters(e){
    let id = e.id;
    let val = e.value;
    localStorage.setItem(id, val);// Every time user writing something, the localStorage's value will override .
}

function keepSearchParametersAfterPageRefresh(){
    $("#searchUserBar").val(getSavedValueForTextBox("searchUserBar"));
    $("#search-user-dropdown").val(getSavedValueForDropDown("search-user-dropdown"));

    function getSavedValueForTextBox  (v){
        let usersPropertyParam = new URL(location.href).searchParams.get('usersProperty');
        if (localStorage.getItem(v) === null) {
            return "";
        }
        else if(usersPropertyParam === null){
            return "";
        }

        return localStorage.getItem(v);
    }

    function getSavedValueForDropDown(v){
        let propertyValue = new URL(location.href).searchParams.get('propertyValue');
        if (localStorage.getItem(v) === null) {
            return "ID";
        }
        else if(propertyValue === null){
            return "ID";
        }

        return localStorage.getItem(v);
    }
}

//If the page size option is greater than total number of elements (users) - disable it
function checkPageSizes() {
    let pageSizesToShow = $('#pageSizesToShow').data('pagesizestoshow');

    $("#pageSizeSelect option").each(function(i, option) {
        if($.inArray(parseInt(option.value), pageSizesToShow) ===-1){
            option.disabled = true;
        }
    });
}

function sortTable(n) {
    let table, rows, switching, i, x, y, shouldSwitch, dir, switchcount = 0;
    table = document.getElementById("user-table");
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

function searchUserByProperty() {
    let selectedProperty = $("#search-user-dropdown option:selected").text();
    let value = $("#searchUserBar").val();

    if (value != null && value !== "") {
        window.location.href = "/admin/users?usersProperty=" + selectedProperty + "&propertyValue=" + value;
    } else {
        window.location.href = "/admin/users";
    }
}


    // const getJsonUsers = "/admin/json-users";
    // const token = $('#_csrf').attr('content');
    // const header = $('#_csrf_header').attr('content');
    //
    // let userIdToDelete;
    // let rowIndexToDelete;
    //
    // $.ajaxSetup({
    //     headers: {
    //         'Content-Type':  'application/json',
    //         'Accept': 'application/json',
    //         'X-CSRF-TOKEN': token
    //     }
    // });

// function setRowIndexAndUserId(row, id) {
//     userIdToDelete = id;
//     rowIndexToDelete = row.parentNode.parentNode.rowIndex;
// }

// function closeModal(nameOfTheModal) {
//     $(nameOfTheModal).modal('toggle');
// }
//
// function deleteEntity(row, id) {
//     userIdToDelete = id;
//     rowIndexToDelete = row.parentNode.parentNode.rowIndex;
//
//     let deleteUserUrl = '/admin/json-users/delete/' + userIdToDelete;
//
//     $.ajax({
//         url: deleteUserUrl,
//         type: 'DELETE',
//         success: function () {
//
//             let table = $("#user-table");
//             table[0].deleteRow(rowIndexToDelete);
//
//             $('#alert-messages').append(
//                 "<div class='alert alert-success alert-dismissible fade show' role='alert'>"+
//                 "<button type='button' class='close' data-dismiss='alert' aria-label='Close'>"+
//                 "<span aria-hidden='true'>&times;</span> </button>"+
//                 "<strong>Well done!</strong> User has been deleted!!!"+
//                 "</div>"
//             );
//             // closeModal('#deleteModal');
//             // userIdToDelete = "";
//             // rowIndexToDelete = "";
//         }
//     });
// }



    //Old code:
    /*let getUsersByProperty = '/adminPage/json-users/search?usersProperty=' + selectedProperty + '&propertyValue=' + value;

    $.ajax({
        url: getUsersByProperty,
        type: 'GET',
        success: function (data, status, xhr) {

            let tableBody = $("#user-table-body");
            tableBody.empty();
            $.each(data, function (i, e) {
                let end = e.id + ");'";
                let del = "'setRowIndexAndUserId(this, " + end;
                let enabled;
                if (e.enabled === true) {
                    enabled = "<span style='color: green'>Enabled</span>"
                }
                else enabled = "<span style='color: red'>Disabled</span>";

                let row = $('<tr>').append(
                    $('<td>').text(e.id),
                    $('<td>').text(e.name),
                    $('<td>').text(e.surname),
                    $('<td>').text(e.username),
                    $('<td>').text(e.email),
                    $('<td>').append(enabled),
                    $('<td>').append(
                        "<a style='text-decoration: none; color:blue' href='/adminPage/users/" + e.id + "'" +
                        "class='editBtn' data-toggle='tooltip' data-placement='right' title='Edit user'>" +
                        "<i class='fa fa-edit'></i></a>"
                    ),
                    $('<td>').append(
                        "<a id='remove-link' style='text-decoration: none; color:red'" +
                        "data-toggle='modal' data-placement='right' title='Remove user' " +
                        "data-target='#deleteModal' onclick=" +
                        del + "><i class='fa fa-times' aria-hidden='true'></i></a>"
                    )
                );
                $('#user-table-body').append(row);
            });
        },
        error: function (jqXhr, textStatus, errorMessage) {
            let httpStatusCode = jqXhr.status;

            if(httpStatusCode === 404){
                $('#alert-messages').append(
                    "<div class='alert alert-info alert-dismissible fade show' role='alert'>"+
                    "<button type='button' class='close' data-dismiss='alert' aria-label='Close'>"+
                    "<span aria-hidden='true'>&times;</span> </button>"+
                    "Sorry, no matches found for "+ selectedProperty + " = " + value +
                    "</div>"
                );
            }
        }
    });*/
// }




