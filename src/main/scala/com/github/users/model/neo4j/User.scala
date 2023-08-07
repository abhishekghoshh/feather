package com.github.users.model.neo4j

import com.github.neo4j.node.Neo4jNode
import org.neo4j.ogm.annotation.{NodeEntity, Property}

class User @NodeEntity() extends Neo4jNode:

  def this(name: String, age: Int, gender: String, email: String) =
    this()
    this.name = name
    this.age = age
    this.gender = gender
    this.email = email

  def this(id: Long, name: String, age: Int, gender: String, email: String) =
    this(name, age, gender, email)
    super.setId(id)

  @Property(name = "name")
  private var name: String = _

  @Property(name = "age")
  private var age: Int = _

  @Property(name = "gender")
  private var gender: String = _

  @Property(name = "email")
  private var email: String = _

  def getName: String = name

  def setName(name: String): Unit = this.name = name

  def getAge: Int = age

  def setAge(age: Int): Unit = this.age = age

  def getGender: String = gender

  def setGender(gender: String): Unit = this.gender = gender

  def getEmail: String = email

  def setEmail(email: String): Unit = this.email = email
end User