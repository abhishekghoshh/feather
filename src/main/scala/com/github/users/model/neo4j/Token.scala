package com.github.users.model.neo4j

import com.github.neo4j.node.Neo4jNode
import org.neo4j.ogm.annotation.{NodeEntity, Property}

class Token @NodeEntity() extends Neo4jNode:
  @Property(name = "value")
  private var value: String = _

  @Property(name = "tokeType")
  private var tokenType: String = _

  def this(value: String, tokeType: String) =
    this()
    this.value = value
    this.tokenType = tokeType

  def getValue: String = value

  def setValue(value: String): Unit = this.value = value

  def getTokenType: String = tokenType

  def setTokenType(tokenType: String): Unit = this.tokenType = tokenType
end Token
