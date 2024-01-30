package com.github.users.controllers.filter

import com.linecorp.armeria.common.{HttpRequest, HttpResponse}
import com.linecorp.armeria.server.{DecoratingHttpServiceFunction, HttpService, ServiceRequestContext}
import org.slf4j.LoggerFactory

class ControllerDecorator extends DecoratingHttpServiceFunction:
  private val logger = LoggerFactory.getLogger(getClass)

  override def serve(delegate: HttpService, ctx: ServiceRequestContext, req: HttpRequest): HttpResponse =
    logger.info("I am in controller decorator")
    delegate.serve(ctx, req)
end ControllerDecorator

object app extends App:
end app
