package com.github.reactor.armeria.controllers

import com.github.reactor.domain.dto.{ExperimentDTO, UserDTO}
import com.github.reactor.domain.service.UserService
import com.linecorp.armeria.common.{HttpResponse, HttpStatus}
import com.linecorp.armeria.server.annotation.*

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
