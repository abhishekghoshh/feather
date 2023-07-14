package com.github.reactor.domain.dto

import com.github.reactor.domain.neo4j.model.User

case class UserDTO(
    id: Option[String],
    creationTimeStamp: Option[String],
    lastUpdateTimeStamp: Option[String],
    name: String,
    age: Int,
    gender: String,
    email: String)

object UserDTO:
  def created(user: User): UserDTO =
    UserDTO(
      Some(s"${user.getId}"),
      Some(s"${user.getCreationTimeStamp}"),
      Some(s"${user.getLastUpdateTimeStamp}"),
      user.getName,
      user.getAge,
      user.getGender,
      user.getEmail
    )

  def notCreated(user: User): UserDTO =
    UserDTO(
      Option.empty,
      Option.empty,
      Option.empty,
      user.getName,
      user.getAge,
      user.getGender,
      user.getEmail
    )
  def empty: UserDTO = UserDTO(Option.empty, Option.empty, Option.empty, null, 0, null, null)
end UserDTO
