package com.github.reactor.core.neo4j.repository

import com.github.reactor.core.neo4j.node.Neo4jNode
import org.neo4j.driver.Logging
import org.neo4j.ogm.config.Configuration
import org.neo4j.ogm.cypher.{ComparisonOperator, Filter, Filters}
import org.neo4j.ogm.drivers.bolt.driver.BoltDriver
import org.neo4j.ogm.drivers.bolt.driver.BoltDriver.CONFIG_PARAMETER_BOLT_LOGGING
import org.neo4j.ogm.session.{Neo4jSession, Session, SessionFactory}
import org.neo4j.ogm.transaction.Transaction
import org.slf4j.{Logger, LoggerFactory}

import java.text.SimpleDateFormat
import java.util.Date
import scala.collection.mutable.ListBuffer
import scala.jdk.CollectionConverters.*

class Neo4jRepository(
    url: String,
    username: String,
    password: String,
    database: String,
    packageName: String)
    extends Neo4jConnector(url, username, password, database, packageName)
    with AutoCloseable:

  private var logger: Logger                  = _
  private var dateFormatter: SimpleDateFormat = _

  override def build(): Neo4jRepository =
    super.build()
    logger = LoggerFactory.getLogger(getClass)
    dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sssZ")
    this

  private def formattedDate(date: Date): String = dateFormatter.format(date)

  private def currentTime: Date = new Date()

  private def createNewId[T <: Neo4jNode]: Long = System.currentTimeMillis()

  private def withAdditionalProperties[T <: Neo4jNode](data: T): Unit =
    if data.getId == 0L then data.setId(createNewId)
    val time = formattedDate(currentTime)
    data.setCreationTimeStamp(time)
    data.setLastUpdateTimeStamp(time)

  private def withAdditionalProperties[T <: Neo4jNode](data: T, existingData: T): Unit =
    data.setLastUpdateTimeStamp(formattedDate(currentTime))
    data.setCreationTimeStamp(existingData.getCreationTimeStamp)

  def findAll[T <: Neo4jNode](className: Class[T]): List[T] =
    withSession: session =>
      session.loadAll(className).asScala.toList

  def findById[T <: Neo4jNode](className: Class[T], id: Long): Option[T] =
    withSession: session =>
      val result = session.loadAll(className, filterForIdEquality(id))
      if result.asScala.isEmpty then Option.empty
      else Some(result.asScala.head)

  def updateById[T <: Neo4jNode](data: T): T =
    if data.getId == 0L then return data
    val existingData = findById(data.getClass, data.getId)
    if existingData.isEmpty then return clearId(data)
    withAdditionalProperties(data, existingData.get)
    withTransaction: session =>
      session.save(data)
      data

  def create[T <: Neo4jNode](data: T): Long =
    withAdditionalProperties(data)
    withTransaction: session =>
      session.save(data)
      data.getId

  def findOne[T <: Neo4jNode](className: Class[T], filter: Filter): Option[T] =
    val result = findAll(className, filter)
    if result.isEmpty then Option.empty
    else Some(result.head)

  def findAll[T <: Neo4jNode](className: Class[T], filter: Filter): List[T] =
    withSession: session =>
      session.loadAll(className, filter).asScala.toList

  def findOne[T <: Neo4jNode](className: Class[T], filters: Filters): Option[T] =
    val result = findAll(className, filters)
    if result.isEmpty then Option.empty
    else Some(result.head)

  def findAll[T <: Neo4jNode](className: Class[T], filters: Filters): List[T] =
    withSession: session =>
      session.loadAll(className, filters).asScala.toList

  def deleteById[T <: Neo4jNode](className: Class[T], id: Long): Boolean =
    withTransaction: session =>
      val optionalData = findById(className, id)
      if optionalData.isDefined then
        session.delete(
          className,
          java.util.List.of(filterForIdEquality(id)),
          false
        )
        true
      else false

  private def filterForIdEquality(id: Long) = new Filter("id", ComparisonOperator.EQUALS, id)

  private def clearId[T <: Neo4jNode](data: T): T =
    data.setId(0L)
    data

end Neo4jRepository
