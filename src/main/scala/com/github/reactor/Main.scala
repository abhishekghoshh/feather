package com.github.reactor

import com.github.reactor.armeria.controllers.UserController
import com.github.reactor.armeria.filter.OnePerRequestFilter
import com.github.reactor.core.di.Application
import com.github.reactor.core.neo4j.repository.Neo4jRepository
import com.github.reactor.domain.repositories.UserRepository
import com.github.reactor.domain.service.UserService
import com.linecorp.armeria.common.multipart.Multipart
import com.linecorp.armeria.common.{HttpHeaders, HttpMethod, HttpRequest, HttpResponse}
import com.linecorp.armeria.scala.ExecutionContexts.sameThread
import com.linecorp.armeria.scala.implicits.*
import com.linecorp.armeria.server.{Server, ServerBuilder, Service, ServiceRequestContext}
import com.typesafe.config.{Config, ConfigFactory}

import java.util.concurrent.CompletableFuture
import javax.inject.*
import scala.concurrent.{Await, Future}

object Main:

  def main(args: Array[String]): Unit =
    Application.build(this)
    implicit val ec: scala.concurrent.ExecutionContext = sameThread
    val server = Server
      .builder()
      .http(8080)
      .decorator(new OnePerRequestFilter())
      .annotatedService(
        "/users",
        userController
      )
      .build()
    server.start.join()

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
