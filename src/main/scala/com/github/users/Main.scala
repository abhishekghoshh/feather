package com.github.users

import com.github.feather.di.Application
import com.github.neo4j.repository.Neo4jRepository
import com.github.users.controllers.UserController
import com.github.users.controllers.filter.OnePerRequestFilter
import com.github.users.repositories.neo4j.UserRepository
import com.github.users.service.UserService
import com.linecorp.armeria.scala.ExecutionContexts.sameThread
import com.linecorp.armeria.server.{DecoratingHttpServiceFunction, Server}
import com.typesafe.config.{Config, ConfigFactory}

object Main:

  def main(args: Array[String]): Unit =
    Application.build(this)
    implicit val ec: scala.concurrent.ExecutionContext = sameThread
    val server = Server
      .builder()
      .http(8080)
      .decorator(requestFilter)
      .annotatedService(userController)
      .build()
    server.start.join()

  private def requestFilter: DecoratingHttpServiceFunction = new OnePerRequestFilter()

  private def userController: UserController =
    val config: Config = ConfigFactory.load()
    val url            = config.getString("neo4j.url")
    val username       = config.getString("neo4j.username")
    val password       = config.getString("neo4j.password")
    val database       = config.getString("neo4j.database")
    val packageName    = config.getString("neo4j.package-name")
    val neo4jRepository =
      new Neo4jRepository(url, username, password, database, packageName).build()
    val userRepository = new UserRepository(neo4jRepository)
    val userService    = new UserService(userRepository)
    new UserController(userService)

end Main
