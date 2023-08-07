package com.github.users.controllers.filter

import com.linecorp.armeria.common.{HttpRequest, HttpResponse}
import com.linecorp.armeria.server.{DecoratingHttpServiceFunction, HttpService, ServiceRequestContext}
import org.slf4j.{LoggerFactory, MDC}

import java.util
import java.util.UUID

class OnePerRequestFilter extends DecoratingHttpServiceFunction:
  private val logger = LoggerFactory.getLogger(getClass)

  @throws[Exception]
  def serve(delegate: HttpService, ctx: ServiceRequestContext, req: HttpRequest): HttpResponse =
    MDC.setContextMap(buildContextMap(ctx))
    val startTime = System.currentTimeMillis
    var response = delegate.serve(ctx, req)
    val endTime = System.currentTimeMillis
    logger.info(s"ResponseTime=${endTime - startTime}ms")
    response

  private def buildContextMap(ctx: ServiceRequestContext): java.util.HashMap[String, String] =
    val context = java.util.HashMap[String, String]()
    val uuid = UUID.randomUUID.toString
    context.put("uuid", uuid)
    context.put("method", ctx.method().toString)
    context.put("uri", ctx.path())
    context
end OnePerRequestFilter
