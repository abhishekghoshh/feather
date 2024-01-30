package com.github.users.controllers.filter

import com.github.users.model.neo4j.Token
import com.linecorp.armeria.common.{HttpRequest, HttpResponse}
import com.linecorp.armeria.server.{DecoratingHttpServiceFunction, HttpService, ServiceRequestContext}
import org.slf4j.LoggerFactory

class UserFilter extends DecoratingHttpServiceFunction:
  private val logger = LoggerFactory.getLogger(getClass)

  @throws[Exception]
  def serve(delegate: HttpService, ctx: ServiceRequestContext, req: HttpRequest): HttpResponse =
    val updatedRequest = req.withHeaders(
      req.headers().toBuilder.addObject("token", Token("token-value", "Bearer"))
    )
    delegate.serve(ctx, req)


end UserFilter
