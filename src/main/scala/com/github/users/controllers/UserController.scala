package com.github.users.controllers

import com.github.users.controllers.advice.GlobalExceptionHandler
import com.github.users.dto.UserDTO
import com.github.users.service.UserService
import com.linecorp.armeria.common.{HttpResponse, HttpStatus}
import com.linecorp.armeria.server.annotation.*
import com.linecorp.armeria.server.annotation.decorator.CorsDecorator

@ExceptionHandler(value = classOf[GlobalExceptionHandler])
@PathPrefix(value = "/user")
@CorsDecorator(origins = Array("*"), credentialsAllowed = true)
class UserController(userService: UserService):
  @Get("/")
  def findAll: HttpResponse = HttpResponse.ofJson(HttpStatus.OK, userService.findAll())

  @Get("/:id")
  def findById(@Param("id") id: String): HttpResponse =
    HttpResponse.ofJson(HttpStatus.OK, userService.findById(id))

  @Post("/")
  @RequestConverter(classOf[JacksonRequestConverterFunction])
  def create(userDTO: UserDTO): HttpResponse =
    HttpResponse.ofJson(HttpStatus.CREATED, userService.create(userDTO))

  @Put("/")
  @RequestConverter(classOf[JacksonRequestConverterFunction])
  def update(userDTO: UserDTO): HttpResponse =
    HttpResponse.ofJson(HttpStatus.OK, userService.update(userDTO))

  @Delete("/:id")
  @RequestConverter(classOf[JacksonRequestConverterFunction])
  def delete(@Param("id") id: String): HttpResponse =
    HttpResponse.ofJson(HttpStatus.OK, userService.delete(id))
end UserController
