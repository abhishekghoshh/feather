package com.github.users.controllers.converter

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.users.model.neo4j.User
import com.linecorp.armeria.common.AggregatedHttpRequest
import com.linecorp.armeria.server.ServiceRequestContext
import com.linecorp.armeria.server.annotation.RequestConverterFunction
import org.slf4j.LoggerFactory

import java.lang.reflect.ParameterizedType
import scala.reflect.ClassTag
import scala.reflect.runtime.universe.*

abstract class RequestConverter[T](implicit val classTag: ClassTag[T]) extends RequestConverterFunction:

  private val logger = LoggerFactory.getLogger(getClass)

  private val mapper: ObjectMapper = new ObjectMapper
  private val classType: Class[_] = classTag.runtimeClass

  override def convertRequest(ctx: ServiceRequestContext,
                              request: AggregatedHttpRequest,
                              expectedResultType: Class[?],
                              expectedParameterizedResultType: ParameterizedType
                             ): AnyRef =
    logger.info("I am in RequestConverter")
    if expectedResultType eq classType then
      return mapper.readValue(request.contentUtf8, classType)
    RequestConverterFunction.fallthrough

end RequestConverter

class UserRequestConverter extends RequestConverter[User]