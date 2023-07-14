package com.github.reactor.armeria.controllers.advice

import com.linecorp.armeria.common.{HttpRequest, HttpResponse}
import com.linecorp.armeria.server.ServiceRequestContext
import com.linecorp.armeria.server.annotation.ExceptionHandlerFunction

class GlobalExceptionHandler extends ExceptionHandlerFunction:
  override def handleException(serviceRequestContext: ServiceRequestContext,
                               httpRequest: HttpRequest, cause: Throwable): HttpResponse =
    ExceptionHandlerFunction.fallthrough
end GlobalExceptionHandler
