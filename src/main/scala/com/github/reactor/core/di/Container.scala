package com.github.reactor.core.di

import com.github.reactor.core.di.Application.getClass
import com.github.reactor.core.di.annotations.{Inject, PostConstruct, Value}
import com.github.reactor.core.di.util.ClassInfoHelper.{getBeanNameFromComponentClass, getBeanNameFromInjectedParameter}
import com.github.reactor.core.di.util.{ClassInfoHelper, ConfigLoader}
import com.github.reactor.core.neo4j.repository.{Neo4jConnector, Neo4jRepository}
import org.slf4j.LoggerFactory

import java.lang.reflect.Constructor
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.jdk.CollectionConverters.*

class Container:
  private val logger       = LoggerFactory.getLogger(getClass)
  private val configLoader = ConfigLoader.instance
  private var scanned      = false

  def isNotScanned: Boolean = !scanned

  def scanCompleted(): Unit = scanned = true

  private val dependencies    = mutable.Map[String, OneDependency]()
  private val dependencyGraph = mutable.Map[String, List[TransitiveDependencyMetadata]]()

  implicit val oneDependencyOrdering: Ordering[OneDependency] = Ordering.by(_.negativeDependencyCount)
  private val dependenciesTree                                = mutable.PriorityQueue[OneDependency]()

  def register[T](
      oneDependency: OneDependency,
      transitiveDependencyMetadataList: List[TransitiveDependencyMetadata]
    ): Container =
    dependencies(oneDependency.name) = oneDependency
    dependencyGraph(oneDependency.name) = transitiveDependencyMetadataList
    dependenciesTree.addOne(oneDependency)
    this

  def load(): Container =
    while dependenciesTree.nonEmpty do load(dependenciesTree.dequeue(), DependencyPathTracker())
    this

  def load(oneDependency: OneDependency, dependencyPath: DependencyPathTracker): Unit =
    logger.info(s"Loading dependency with ${oneDependency.name} ${oneDependency.classType.getName}")
    oneDependency.dependencyType match
      case DependencyType.Component => loadComponent(oneDependency, dependencies, dependencyGraph, dependencyPath)
      case DependencyType.Bean      => loadBean(oneDependency, dependencies, dependencyGraph, dependencyPath)
      case _                        => logger.info("configuration property is always a leaf dependency")

  private def loadComponent(
      oneDependency: OneDependency,
      dependencies: mutable.Map[String, OneDependency],
      dependencyGraph: mutable.Map[String, List[TransitiveDependencyMetadata]],
      dependencyPath: DependencyPathTracker
    ): Unit =
    val fullDependencyName = oneDependency.name
    if dependencyPath.contains(fullDependencyName) then
      throw new RuntimeException(
        s"Cyclic dependency found starting from bean name : ${fullDependencyName} , " +
          s"class name : ${oneDependency.classType.getName} with dependency path :  ${dependencyPath.dependencyPath}"
      )
    dependencyPath.addOne(fullDependencyName)
    resolveTransitiveDependencies(dependencies, dependencyGraph, fullDependencyName, dependencyPath)
    val constructors                = oneDependency.classType.getDeclaredConstructors
    var constructor: Constructor[?] = null
    if constructors.length == 1 then constructor = constructors.head
    else constructor = constructors.filter(ctr => ctr.isAnnotationPresent(classOf[Inject])).head
    constructor.setAccessible(true)
    val constructorDependencies = ListBuffer[AnyRef]()
    for constructorParameter <- constructor.getParameters do
      if constructorParameter.isAnnotationPresent(classOf[Inject]) then
        constructorDependencies.addOne(dependencies(getBeanNameFromInjectedParameter(constructorParameter)).value)
      else
        println(s"constructorParameter.getType ${constructorParameter.getType}")
        println(
          s"loading parameter ${configLoader.get(constructorParameter.getAnnotation(classOf[Value]).name(), constructorParameter.getType)}"
        )
        constructorDependencies.addOne(
          configLoader.get(constructorParameter.getAnnotation(classOf[Value]).name(), constructorParameter.getType)
        )
    logger.info(
      s"Creating object of ${fullDependencyName} with class name : ${oneDependency.classType.getName} " +
        s"with parameters ${constructorDependencies.head.getClass}"
    )
    val componentObject = constructor.newInstance(constructorDependencies.toArray*)

    for injectedField <- oneDependency.classType.getDeclaredFields
        .filter(field => field.isAnnotationPresent(classOf[Inject]))
    do
      val fieldBeanName = ClassInfoHelper.getBeanNameFromInjectedField(injectedField)
      if !injectedField.getType.isAssignableFrom(dependencies(fieldBeanName).classType) then
        throw new RuntimeException(
          s"field ${injectedField.getName} with class type ${injectedField.getType.getName} " +
            s"can not be casted from ${dependencies(fieldBeanName).classType}"
        )
      injectedField.setAccessible(true)
      injectedField.set(componentObject, dependencies(fieldBeanName).value)

    for injectedMethod <- oneDependency.classType.getDeclaredMethods
        .filter(method => method.isAnnotationPresent(classOf[Inject]))
    do
      val injectedMethodDependencies = ListBuffer[AnyRef]()
      for injectedMethodParameter <- injectedMethod.getParameters do
        if injectedMethodParameter.isAnnotationPresent(classOf[Inject]) then
          injectedMethodDependencies.addOne(dependencies(getBeanNameFromInjectedParameter(injectedMethodParameter)).value)
        else
          println(s"injectedMethodParameter.getType ${injectedMethodParameter.getType}")
          println(
            s"loading parameter ${configLoader.get(injectedMethodParameter.getAnnotation(classOf[Value]).name(), injectedMethodParameter.getType)}"
          )
          injectedMethodDependencies.addOne(
            configLoader.get(injectedMethodParameter.getAnnotation(classOf[Value]).name(), injectedMethodParameter.getType)
          )
        injectedMethod.setAccessible(true)
        injectedMethod.invoke(componentObject, injectedMethodDependencies.toArray*)
    oneDependency.classType.getDeclaredMethods
      .filter(method => method.isAnnotationPresent(classOf[PostConstruct]))
      .foreach(method =>
        method.setAccessible(true)
        method.invoke(componentObject)
      )
    oneDependency.set(componentObject)
    dependencyPath.remove(fullDependencyName)

  private def loadBean(
      oneDependency: OneDependency,
      dependencies: mutable.Map[String, OneDependency],
      dependencyGraph: mutable.Map[String, List[TransitiveDependencyMetadata]],
      dependencyPath: DependencyPathTracker
    ): Unit = print("")

  private def resolveTransitiveDependencies(
      dependencies: mutable.Map[String, OneDependency],
      dependencyGraph: mutable.Map[String, List[TransitiveDependencyMetadata]],
      fullDependencyName: String,
      dependencyPath: DependencyPathTracker
    ): Unit =
    val transitiveDependencyMetadata = dependencyGraph(fullDependencyName)
    for oneTransitiveDependencyMetadata <- transitiveDependencyMetadata do
      if !dependencies.contains(oneTransitiveDependencyMetadata.name) then
        throw new RuntimeException(s"dependency not found with name : ${oneTransitiveDependencyMetadata.name}")
      if !oneTransitiveDependencyMetadata.classType.isAssignableFrom(dependencies(oneTransitiveDependencyMetadata.name).classType) then
        throw new RuntimeException(
          s"${dependencies(oneTransitiveDependencyMetadata.name).classType.getName} " +
            s"can not be cast into " +
            s"${oneTransitiveDependencyMetadata.classType.getName}"
        )
    for oneTransitiveDependencyMetadata <- transitiveDependencyMetadata do
      if !dependencies(oneTransitiveDependencyMetadata.name).isResolved then
        load(dependencies(oneTransitiveDependencyMetadata.name), dependencyPath)

end Container

class TransitiveDependencyMetadata(
    val name: String,
    val classType: Class[?]):

  override def toString: String =
    s"name : ${name} , classType : ${classType.getName}"
end TransitiveDependencyMetadata

class OneDependency(
    val name: String,
    val classType: Class[?],
    _dependencyCount: Int,
    var value: AnyRef,
    val dependencyType: DependencyType,
    originatingClass: Class[?]):

  def negativeDependencyCount: Int = -1 * _dependencyCount

  def set(value: AnyRef): Unit =
    if value == null then throw new RuntimeException(s"Do not try to set null value for dependency with name : ${name}")
    if !value.getClass.isAssignableFrom(classType) then
      throw new RuntimeException(
        s"trying to assign ${value.getClass.getName} for ${classType.getName}  for dependency with name : ${name}"
      )
    this.value = value

  def isResolved: Boolean = value != null

  override def toString: String =
    s"name : ${name} , classType : ${classType.getName} , " +
      s"dependencyCount : $_dependencyCount , " +
      s"dependencyType : $dependencyType , originatingClass : $originatingClass}"

end OneDependency

enum DependencyType:
  case Component, Bean, ConfigurationProperty, Value
end DependencyType

class DependencyPathTracker:
  private val dependencyPathSet  = mutable.Set[String]()
  private val dependencyPathList = ListBuffer[String]()

  def addOne(dependencyName: String): Unit =
    dependencyPathSet.addOne(dependencyName)
    dependencyPathList.addOne(dependencyName)

  def remove(dependencyName: String): Unit =
    dependencyPathSet.remove(dependencyName)
    dependencyPathList.remove(dependencyPathList.length - 1)

  def contains(dependencyName: String): Boolean = dependencyPathSet.contains(dependencyName)

  def dependencyPath: String = dependencyPathList.mkString("->")
end DependencyPathTracker
