package com.github.users.dto


abstract class Response(responseType: String):
end Response

case class SuccessResponse(message: String, responseType: String = "success") extends Response(responseType)

case class ErrorResponse(message: String, responseType: String = "error") extends Response(responseType)
