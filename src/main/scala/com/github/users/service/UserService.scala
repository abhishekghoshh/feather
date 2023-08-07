package com.github.users.service

import com.github.users.dto.{ErrorResponse, Response, SuccessResponse, UserDTO}
import com.github.users.model.neo4j.User
import com.github.users.repositories.neo4j.UserRepository
import org.slf4j.LoggerFactory

class UserService(userRepository: UserRepository):
  private val logger = LoggerFactory.getLogger(getClass)

  def findAll(): List[UserDTO] = userRepository.findAll().map(user => UserDTO.created(user))

  def findById(id: String): UserDTO =
    val optionalUser: Option[User] = userRepository.findById(id.toLong)
    logger.info(s"user found for the uid : ${id} is ${optionalUser.isDefined}")
    if optionalUser.isDefined then UserDTO.created(optionalUser.get)
    else UserDTO.empty

  def create(userDTO: UserDTO): UserDTO =
    val user = User(userDTO.name, userDTO.age, userDTO.gender, userDTO.email)
    val uid  = userRepository.create(user)
    uid match
      case 0L => UserDTO.notCreated(user)
      case _  => UserDTO.created(user)

  def update(userDTO: UserDTO): UserDTO =
    val user = User(userDTO.id.get.toLong, userDTO.name, userDTO.age, userDTO.gender, userDTO.email)
    userRepository.update(user)
    if user.getId == 0L then UserDTO.empty
    else UserDTO.created(user)

  def delete(id: String): Response =
    if userRepository.delete(id.toLong) then SuccessResponse(s"user with ${id} is deleted")
    else ErrorResponse(s"user with ${id} is not deleted")

end UserService
