package com.github.neo4j.node

import org.neo4j.ogm.annotation.{GeneratedValue, Id, Property}

trait Neo4jNode:
  @Id
  protected var id: Long = _

  def getId: Long = id

  def setId(id: Long): Unit = this.id = id

  @Property(name = "creationTimeStamp")
  private var creationTimeStamp: String = _

  def getCreationTimeStamp: String = creationTimeStamp

  def setCreationTimeStamp(creationTimeStamp: String): Unit = this.creationTimeStamp = creationTimeStamp

  @Property(name = "lastUpdateTimeStamp")
  private var lastUpdateTimeStamp: String = _

  def getLastUpdateTimeStamp: String = lastUpdateTimeStamp

  def setLastUpdateTimeStamp(lastUpdateTimeStamp: String): Unit = this.lastUpdateTimeStamp = lastUpdateTimeStamp

end Neo4jNode

