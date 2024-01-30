package com.github.users.controllers

import com.github.users.controllers.advice.GlobalExceptionHandler
import com.github.users.controllers.converter.UserRequestConverter
import com.github.users.controllers.filter.ControllerDecorator
import com.github.users.dto.UserDTO
import com.github.users.service.UserService
import com.linecorp.armeria.common.HttpResponse.ofJson
import com.linecorp.armeria.common.HttpStatus.{CREATED, OK}
import com.linecorp.armeria.common.{HttpResponse, HttpStatus}
import com.linecorp.armeria.server.annotation.*
import com.linecorp.armeria.server.annotation.decorator.CorsDecorator

@ExceptionHandler(value = classOf[GlobalExceptionHandler])
@PathPrefix(value = "/user")
@CorsDecorator(origins = Array("*"), credentialsAllowed = true)
class UserController(userService: UserService):
  @Get("/")
  @Decorator(classOf[ControllerDecorator])
  def findAll: HttpResponse = ofJson(OK, userService.findAll())

  @Get("/:id")
  def findById(@Param("id") id: String): HttpResponse =
    ofJson(OK, userService.findById(id))

  @Post("/")
  @RequestConverter(classOf[UserRequestConverter])
  def create(userDTO: UserDTO): HttpResponse =
    ofJson(CREATED, userService.create(userDTO))

  @Put("/")
  @RequestConverter(classOf[JacksonRequestConverterFunction])
  def update(userDTO: UserDTO): HttpResponse =
    ofJson(OK, userService.update(userDTO))

  @Delete("/:id")
  @RequestConverter(classOf[JacksonRequestConverterFunction])
  def delete(@Param("id") id: String): HttpResponse =
    ofJson(OK, userService.delete(id))
end UserController
