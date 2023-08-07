package com.github.users.repositories.neo4j

import com.github.neo4j.repository.Neo4jRepository
import com.github.users.model.neo4j.User
import org.neo4j.ogm.cypher.{ComparisonOperator, Filter}
import org.slf4j.LoggerFactory

class UserRepository(neo4jRepository: Neo4jRepository):

  private val logger = LoggerFactory.getLogger(getClass)

  def findAll(): List[User] = neo4jRepository.findAll(classOf[User])

  def findById(id: Long): Option[User] = neo4jRepository.findById(classOf[User], id)

  def create(user: User): Long =
    val filter                 = new Filter("email", ComparisonOperator.EQUALS, user.getEmail)
    val optionalExperimentNode = neo4jRepository.findOne(classOf[User], filter)
    logger.info(s"optionalExperimentNode is $optionalExperimentNode")
    optionalExperimentNode match
      case Some(_) => 0L
      case None    => neo4jRepository.create(user)

  def update(user: User): User = neo4jRepository.updateById(user)

  def delete(id: Long): Boolean = neo4jRepository.deleteById(classOf[User], id)

end UserRepository
