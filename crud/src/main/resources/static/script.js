$(document).ready(function(){
  loadUsers()
  attachAddUserHandler();
  M.AutoInit();
  initiateModal()
});
function initiateModal(){
    $('.modal').modal()
    $("#view_blogs_nav_btn").on('click',function(event){
            event.preventDefault();
            changeModalState(true,false,false)
    })
    $("#blogs_form_nav_btn").on('click',function(event){
        event.preventDefault();
        changeModalState(false,true,false)
    })
    $("#user_update_form_nav_btn").on('click',function(event){
        event.preventDefault();
        changeModalState(false,false,true)
    })
}

function changeModalState(flag1,flag2,flag3){
    const userId = $("#add_update_blogs_form").data("userId");
    loadUpdateAddUpdateSticky(userId,null)
    if(flag1){
        $("#view_blogs_nav_btn").parent().addClass("active");
        $("#user_blogs_container").css("display", "block");
    }else{
        $("#view_blogs_nav_btn").parent().removeClass("active");
        $("#user_blogs_container").css("display", "none");
    }
    if(flag2){
        $("#blogs_form_nav_btn").parent().addClass("active");
        $("#add_update_blogs_form").css("display", "block");
    }else{
        $("#blogs_form_nav_btn").parent().removeClass("active");
        $("#add_update_blogs_form").css("display", "none");
    }
    if(flag3){
        $("#user_update_form_nav_btn").parent().addClass("active");
        $("#user_update_form").css("display", "block");
    }else{
        $("#user_update_form_nav_btn").parent().removeClass("active");
        $("#user_update_form").css("display", "none");
    }
}


function attachAddUserHandler(){
    $("#new_user_submit").on('click',function(event){
        event.preventDefault();
        var name=$("#new_user_name").val();
        var email=$("#new_user_email").val();
        var age=$("#new_user_age").val();
        var imgUrl=$("#new_user_image").val();
        new RestCall()
                    .url('/api/v1/users')
                    .httpMethod("POST")
                    .request({
                        name: name,
                        email: email,
                        age: parseInt(age),
                        imgUrl: imgUrl
                    })
                    .fireRestCall((parameters,statusCode,response,responseHeaders)=>{
                        loadUsers()
                        clearNewUserForm()
                    },(parameters,statusCode,response,responseHeaders)=>{
                        M.toast({html: response.message})
                    })
    })
}
function clearNewUserForm(){
    $("#new_user_name").val("");
    $("#new_user_email").val("");
    $("#new_user_age").val("");
    $("#new_user_image").val("");
}


function loadUsers(){
    new RestCall()
            .url('/api/v1/users')
            .httpMethod("GET")
            .fireRestCall((parameters,statusCode,response,responseHeaders)=>{
                loadUsersList(response)
            },(parameters,statusCode,response,responseHeaders)=>{
                M.toast({html: response.message})
            })
}
function loadUsersList(responseList) {
    let html = "";
    responseList.forEach(function(user) {
        html += loadOneUserInformation(user)
    });
    $("#users_ul").html(html);
}

function loadOneUserInformation(user){
    return `
           <li class="collection-item avatar" >
               <img src="${user.imgUrl || 'https://png.pngtree.com/png-clipart/20231019/original/pngtree-user-profile-avatar-png-image_13369988.png'}"
               alt="" class="circle">
               <span class="title">${user.name}</span>
               <p>${user.email}</p>
               <p>Age: <span>${user.age}</span></p>
               <a id=${user.id} class="secondary-content waves-effect waves-light btn modal-trigger open_modal" onclick=openModal(${user.id})><i
                       class="material-icons">open_in_new</i></a>
           </li>
    `;
}

function openModal(userId){
    $('#blogs_modal').modal('open');
    loadBlogs(userId);
}

function loadBlogs(userId){
    changeModalState(true,false,false)
    $("#blog_container").html("");
    new RestCall()
            .url('/api/v1/users/'+userId+'/blogs')
            .httpMethod("GET")
            .fireRestCall((parameters,statusCode,response,responseHeaders)=>{
                loadModalContent(response)
            },(parameters,statusCode,response,responseHeaders)=>{
                M.toast({html: response.message})
            })
}

function loadModalContent(response){
    loadAllBlogs(response)
    loadUpdateUserInformationForm(response)
    loadUpdateAddUpdateSticky(response.id,null)
}

function loadUpdateAddUpdateSticky(userId,blog){
    html = `
        <div class="row">
            <form class="col s12">
                <div class="row">
                    <div class="input-field col s12">
                        <input id="insert_or_update_blog_name" type="text" class="validate" value="${blog ? blog.title : ''}" required>
                        <label for="insert_or_update_blog_name" class="${blog ? 'active' : ''}">Title</label>
                    </div>
                </div>
                <div class="row">
                    <div class="input-field col s12">
                        <textarea id="insert_or_update_blog_content" class="materialize-textarea" data-length="1200" required>${blog ? blog.content : ''}</textarea>
                        <label for="insert_or_update_blog_content" class="${blog ? 'active' : ''}">Content</label>
                    </div>
                </div>
                <div class="row">
                    <div class="input-field col s12">
                        <input id="insert_or_update_blog_image" class="validate" type="text" value="${blog ? blog.imgUrl : 'https://materializecss.com/images/sample-1.jpg'}" required>
                        <label for="insert_or_update_blog_image" class="active">Image link</label>
                    </div>
                </div>
                <div class="row">
                    <div class="input-field col s4">
                        <a class="waves-effect waves-light btn ${blog ? 'disabled' : ''}" onclick=callAddBlogForUser(${userId})><i class="material-icons left" >add</i>Add</a>
                    </div>
                    <div class="input-field col s4">
                        <a class="waves-effect waves-light btn ${blog ? '' : 'disabled'}" onclick=callUpdateBlogForUser(${userId},${blog ? blog.id : -1})><i class="material-icons left" >update</i>Update</a>
                    </div>
                </div>
            </form>
        </div>
    `
    $("#add_update_blogs_form").html(html);
    $("#add_update_blogs_form").data("userId", userId).html(html);
}

function callAddBlogForUser(userId){
    var title=$("#insert_or_update_blog_name").val();
    var content=$("#insert_or_update_blog_content").val();
    var imgUrl=$("#insert_or_update_blog_image").val();
    new RestCall()
                .url('/api/v1/users/'+userId+'/blogs')
                .httpMethod("POST")
                .request({
                    title: title,
                    content: content,
                    imgUrl: imgUrl
                })
                .fireRestCall((parameters,statusCode,response,responseHeaders)=>{
                    loadBlogs(userId);
                },(parameters,statusCode,response,responseHeaders)=>{
                    M.toast({html: response.message})
                })
}

function callUpdateBlogForUser(userId,blogId){
    var title=$("#insert_or_update_blog_name").val();
    var content=$("#insert_or_update_blog_content").val();
    var imgUrl=$("#insert_or_update_blog_image").val();
    console.log({
                title: title,
                content: content,
                imgUrl: imgUrl
            })
    new RestCall()
                .url('/api/v1/users/'+userId+'/blogs/'+blogId)
                .httpMethod("PUT")
                .request({
                    title: title,
                    content: content,
                    imgUrl: imgUrl
                })
                .fireRestCall((parameters,statusCode,response,responseHeaders)=>{
                    loadBlogs(userId);
                },(parameters,statusCode,response,responseHeaders)=>{
                    M.toast({html: response.message})
                })
}


function loadAllBlogs(response){
    innerHtml = ""
    response.blogs.forEach(function(blog) {
        innerHtml += loadOneBlog(response.id,blog);
    })
    fullHtml = `
        <div class="row">
            ${innerHtml}
        </div>
    `
    $("#user_blogs_container").html(fullHtml);
}
function loadOneBlog(userId,blog){
    console.log("image url is ${blog.imgUrl}")
    console.log(blog)
    return `
        <div class="col s4 m4 l4">
            <div class="card">
                <div class="card-image waves-effect waves-block waves-light">
                    <img class="activator"
                         src="${blog.imgUrl || 'https://materializecss.com/images/sample-1.jpg'}"
                         alt="">
                </div>
                <div class="card-content">
                    <span class="card-title activator grey-text text-darken-4">${blog.title}<i
                            class="material-icons right">more_vert</i></span>
                    <p>${(blog.content || 'No description available.').substring(0, 20)}...</p>
                </div>
                <div class="card-reveal">
                    <div style="display:block" id="card-content-view-${blog.id}">
                        <span class="card-title grey-text text-darken-4">${blog.title}<i
                                class="material-icons right">close</i></span>
                        <p>${blog.content || 'No content available.'}</p>
                    </div>
                </div>
                <div class="card-action">
                    <a class="waves-effect waves-light btn-small" onclick="editBlog(${userId},${blog.id},'${blog.title}','${blog.content}','${blog.imgUrl}')"><i
                            class="material-icons left">edit</i></a>
                    <a class="waves-effect waves-light btn-small" onclick="deleteBlog(${userId},${blog.id})"><i class="material-icons left">delete</i></a>
                </div>
            </div>
        </div>
        `
}

function editBlog(userId,blogId,blogTitle,blogContent,blogImgUrl){
    changeModalState(false,true,false)
    blog = {
             id: blogId,
             title: blogTitle,
             content: blogContent,
             imgUrl: blogImgUrl
           }
    loadUpdateAddUpdateSticky(userId,blog)
}

function deleteBlog(userId,blogId){
    new RestCall()
                .url('/api/v1/users/'+userId+'/blogs/'+blogId)
                .httpMethod("DELETE")
                .fireRestCall((parameters,statusCode,response,responseHeaders)=>{
                    loadBlogs(userId);
                },(parameters,statusCode,response,responseHeaders)=>{
                    M.toast({html: response.message})
                })
}




function loadUpdateUserInformationForm(user) {
    const html = `
    <div class="row">
        <form class="col s12">
            <div class="row">
                <div class="input-field col s12">
                    <input id="update_user_name" type="text" class="validate" required value="${user.name}">
                    <label for="update_user_name" class="active">Name</label>
                </div>
            </div>
            <div class="row">
                <div class="input-field col s12">
                    <input id="update_user_email" type="email" class="validate" required value="${user.email}">
                    <label for="update_user_email" class="active">Email</label>
                </div>
            </div>
            <div class="row">
                <div class="input-field col s12">
                    <input id="update_user_age" class="validate" type="number" required value="${user.age}">
                    <label for="update_user_age" class="active">Age</label>
                </div>
            </div>
            <div class="row">
                <div class="input-field col s12">
                    <input id="update_user_image" class="validate" type="text" required value="${user.imgUrl}">
                    <label for="update_user_image" class="active">Image link</label>
                </div>
            </div>
            <div class="row">
                <div class="input-field col s3">
                    <a class="waves-effect waves-light btn" onclick="updateUserInformation(${user.id})">
                        <i class="material-icons left">update</i>Update
                    </a>
                </div>
                <div class="input-field col s3">
                    <a class="waves-effect waves-light btn" onclick="deleteUserInformation(${user.id})">
                        <i class="material-icons left">delete</i>delete
                    </a>
                </div>
            </div>
        </form>
    </div>
    `;
    $("#user_update_form").html(html);
}

function updateUserInformation(userId){
    const name = $("#update_user_name").val();
    const email = $("#update_user_email").val();
    const age = $("#update_user_age").val();
    const imgUrl = $("#update_user_image").val();
    new RestCall()
                .url('/api/v1/users/'+userId)
                .httpMethod("PUT")
                .request({
                    name: name,
                    email: email,
                    age: parseInt(age),
                    imgUrl: imgUrl
                })
                .fireRestCall((parameters,statusCode,response,responseHeaders)=>{
                    $('.modal').modal("close")
                    loadUsers();
                },(parameters,statusCode,response,responseHeaders)=>{
                    M.toast({html: response.message})
                })
}

function deleteUserInformation(userId){
    new RestCall()
        .url('/api/v1/users/'+userId)
        .httpMethod("DELETE")
        .fireRestCall((parameters,statusCode,response,responseHeaders)=>{
            $('.modal').modal("close")
            loadUsers();
        },(parameters,statusCode,response,responseHeaders)=>{
            M.toast({html: response.message})
        })
}


class RestCall {
  constructor() {
    this.initiate();
  }
  initiate() {
    this.requestEntity = {
      HTTP_METHOD: "GET",
      URL: null,
      CONTENT_TYPE: "application/json; charset=utf-8",
      REQUEST: {},
      HEADERS: {}
    }
    this.actions = {
      SUCCESS_PARAMETERS: {},
      FAILURE_PARAMETERS: {},
      ON_SUCCESS: null,
      ON_FAILURE: null
    }
  }
  url(_url) {
    if (null != _url && "" != _url.trim()) {
      this.requestEntity.URL = _url.trim()
    }
    return this;
  }
  httpMethod(_httpMethod) {
    if (null != _httpMethod && "" != _httpMethod.trim()) {
      this.requestEntity.HTTP_METHOD = _httpMethod.trim().toUpperCase();
    }
    return this;
  }
  contentType(_contentType) {
    if (null != _contentType && "" != _contentType.trim()) {
      this.requestEntity.CONTENT_TYPE = _contentType.trim();
    }
    return this;
  }
  request(_request) {
    if (null != _request) {
      this.requestEntity.REQUEST = _request;
    }
    return this;
  }
  headers(_headers) {
    if (null != _headers) {
      this.requestEntity.HEADERS = _headers;
    }
    return this;
  }
  successParameters(_successParameters) {
    if (null != _successParameters) {
      this.actions.SUCCESS_PARAMETERS = _successParameters;
    }
    return this;
  }
  failureParameters(_failureParameters) {
    if (null != _failureParameters) {
      this.actions.FAILURE_PARAMETERS = _failureParameters;
    }
    return this;
  }
  fireRestCall(_onSuccess, _onFailure) {
    if (null != _onSuccess) {
      this.actions.ON_SUCCESS = _onSuccess;
    }
    if (null != _onFailure) {
      this.actions.ON_FAILURE = _onFailure;
    }
    this.performRestCall(this.requestEntity, this.actions,this.getResponseHeaders);
    this.initiate()
  }
  getResponseHeaders(jqXHR){
    jqXHR.responseHeaders = {};
    var headers = jqXHR.getAllResponseHeaders();
    headers = headers.split("\n");
    headers.forEach(function (header) {
      header = header.split(": ");
      var key = header.shift();
      if (key.length == 0) return
      key = key.toLowerCase();
      jqXHR.responseHeaders[key] = header.join(": ").slice(0, -1) ;
    });
    return jqXHR.responseHeaders;
  }
  performRestCall(requestEntity, actions,getResponseHeaders) {
    if (!requestEntity.HTTP_METHOD || "GET" ==requestEntity.HTTP_METHOD) {
      $.ajax({
        url: requestEntity.URL,
        type: "GET",
        contentType: requestEntity.CONTENT_TYPE,
        cache: false,
        headers: requestEntity.HEADERS,
        timeout: 50000,
        success: function(response,type,xhrResponse) {
          let headers=null;
          if(getResponseHeaders){
            headers=getResponseHeaders(xhrResponse);
          }
          if (actions && actions["ON_SUCCESS"]) {
            actions.ON_SUCCESS({
              RESPONSE_TYPE: type,
              INPUT_PARAMETERS: actions["SUCCESS_PARAMETERS"],
              REQUEST_ENTITY: requestEntity
            },xhrResponse.status, response,headers)
          }
        },
        error: function(error,type) {
          let headers=null;
          if(getResponseHeaders){
            headers=getResponseHeaders(error);
          }
          if (actions && actions["ON_FAILURE"]) {
            actions.ON_FAILURE({
              RESPONSE_TYPE: type,
              INPUT_PARAMETERS: actions["FAILURE_PARAMETERS"],
              REQUEST_ENTITY: requestEntity
            },error.status, error.responseJSON,headers);
          }
        }
      });
    } else {
      let request = JSON.stringify({});
      if (requestEntity && null != requestEntity.REQUEST) {
        request = JSON.stringify(requestEntity.REQUEST)
      }
      $.ajax({
        url: requestEntity.URL,
        type: requestEntity.HTTP_METHOD,
        contentType: requestEntity.CONTENT_TYPE,
        data: request,
        headers: requestEntity.HEADERS,
        cache: false,
        timeout: 50000,
        success: function(response,type,xhrResponse) {
          let headers=null;
          if(getResponseHeaders){
            headers=getResponseHeaders(xhrResponse);
          }
          if (actions && actions["ON_SUCCESS"]) {
            actions.ON_SUCCESS({
              RESPONSE_TYPE: type,
              INPUT_PARAMETERS: actions["SUCCESS_PARAMETERS"],
              REQUEST_ENTITY: requestEntity
            },xhrResponse.status, response,headers)
          }
        },
        error: function(error,type) {
          let headers=null;
          if(getResponseHeaders){
            headers=getResponseHeaders(error);
          }
          if (actions && actions["ON_FAILURE"]) {
            actions.ON_FAILURE({
              RESPONSE_TYPE: type,
              INPUT_PARAMETERS: actions["FAILURE_PARAMETERS"],
              REQUEST_ENTITY: requestEntity
            },error.status, error.responseJSON,headers);
          }
        }
      });
    }
  }
}

/*
new RestCall()
        .url('https://jsonplaceholder.typicode.com/todos/1')
        .httpMethod("GET")
        .request({
            applicationName:applicationName
        })
        .headers({
            consumerOrg:"custom"
        })
        .successParameters({
            value:"success"
        })
        .failureParameters({
            value:"failure"
        })
        .fireRestCall((parameters,statusCode,response,responseHeaders)=>{
            console.log(parameters);
            console.log(statusCode)
            console.log(response)
            console.log(responseHeaders)
        },(parameters,statusCode,response,responseHeaders)=>{
            console.log(parameters);
            console.log(statusCode)
            console.log(response)
            console.log(responseHeaders)
        })
*/