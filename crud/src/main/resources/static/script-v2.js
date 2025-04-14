$(document).ready(function() {
  initializeApp();
});

function initializeApp() {
  loadUsers();
  attachAddUserHandler();
  M.AutoInit();
  initiateModal();
}


function initiateModal() {
    $('.modal').modal();

    const modalStateMapping = {
        "#view_blogs_nav_btn": [true, false, false],
        "#blogs_form_nav_btn": [false, true, false],
        "#user_update_form_nav_btn": [false, false, true]
    };

    Object.keys(modalStateMapping).forEach(selector => {
        $(selector).on('click', function(event) {
            event.preventDefault();
            changeModalState(...modalStateMapping[selector]);
        });
    });
}

function changeModalState(viewBlogs, addUpdateBlogs, updateUser) {
    const userId = $("#add_update_blogs_form").data("userId");
    loadUpdateAddUpdateSticky(userId, null);

    toggleModalSection("#view_blogs_nav_btn", "#user_blogs_container", viewBlogs);
    toggleModalSection("#blogs_form_nav_btn", "#add_update_blogs_form", addUpdateBlogs);
    toggleModalSection("#user_update_form_nav_btn", "#user_update_form", updateUser);
}

function toggleModalSection(navButtonSelector, containerSelector, isActive) {
    if (isActive) {
        $(navButtonSelector).parent().addClass("active");
        $(containerSelector).css("display", "block");
    } else {
        $(navButtonSelector).parent().removeClass("active");
        $(containerSelector).css("display", "none");
    }
}