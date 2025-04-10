$(document).ready(function(){
  M.AutoInit();
});



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
    console.log("I am here")
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