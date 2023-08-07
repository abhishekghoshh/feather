package com.github.feather.di

import com.github.feather.di.annotations.Inject
import com.github.feather.di.ClassInfoHelper.*
import com.github.feather.di.annotations.*
import org.slf4j.LoggerFactory

import java.lang.reflect.{Constructor, Field, Method, Parameter}
import scala.collection.mutable.ListBuffer
import scala.language.postfixOps

object Application:
  private val logger                     = LoggerFactory.getLogger(getClass)
  private val container: Container       = new Container
  private val configLoader: ConfigLoader = ConfigLoader.instance

  def get: Container = container

  def build(obj: AnyRef): Container =
    if null != obj then
      if container.isNotScanned then
        scan(container, obj.getClass.getPackageName)
        container.scanCompleted()
    container

  private def scan(container: Container, packageName: String): Unit =
    val classes = ClassInfoHelper.getAllClasses(packageName)
    scanAndLoadAllConfigurationPropertyClasses(
      container,
      classes.filter(cls => cls.isAnnotationPresent(classOf[ConfigurationProperty]))
    )
    scanAllConfigurationClasses(container, classes.filter(cls => cls.isAnnotationPresent(classOf[Configuration])))
    scanAllComponentClasses(container, classes.filter(cls => cls.isAnnotationPresent(classOf[Component])))
    container.load()

  private def scanInjectedParameter(
      sourceClass: Class[?],
      parameterSource: String,
      parameter: Parameter,
      transitiveDependencyMetadataList: ListBuffer[TransitiveDependencyMetadata]
    ): Unit =
    if parameter.isAnnotationPresent(classOf[Inject]) then
      val beanName = getBeanNameFromInjectedParameter(parameter)
      transitiveDependencyMetadataList.addOne(
        new TransitiveDependencyMetadata(beanName, parameter.getType)
      )
    else if !parameter.isAnnotationPresent(classOf[Value]) then
      throw new RuntimeException(
        s"$sourceClass $parameterSource can get only dependency or value from config"
      )

  private def scanAllInjectedFields(
      injectedField: Field,
      transitiveDependencyMetadataList: ListBuffer[TransitiveDependencyMetadata]
    ): Unit =
    val beanName = getBeanNameFromInjectedField(injectedField)
    transitiveDependencyMetadataList.addOne(
      new TransitiveDependencyMetadata(beanName, injectedField.getType)
    )

  private def scanAllComponentClasses(container: Container, componentClasses: List[Class[?]]): Unit =
    for componentClass <- componentClasses do
      val transitiveDependencyMetadataList = ListBuffer[TransitiveDependencyMetadata]()
      val componentName                    = getBeanNameFromComponentClass(componentClass)
      val constructors                     = componentClass.getDeclaredConstructors
      if constructors.length == 1 then
        constructors.head.getParameters.foreach(parameter =>
          scanInjectedParameter(componentClass, "constructor", parameter, transitiveDependencyMetadataList)
        )
      else
        val injectedConstructors =
          constructors.filter(constructor => constructor.isAnnotationPresent(classOf[Inject]))
        if injectedConstructors.length != 1 then
          throw new RuntimeException(
            s"$componentClass @Inject constructor count is ${injectedConstructors.length}, only 1 is allowed"
          )
        injectedConstructors.head.getParameters.foreach(parameter =>
          scanInjectedParameter(componentClass, "injected constructor", parameter, transitiveDependencyMetadataList)
        )
      componentClass.getDeclaredFields
        .filter(field => field.isAnnotationPresent(classOf[Inject]))
        .foreach(field => scanAllInjectedFields(field, transitiveDependencyMetadataList))
      componentClass.getDeclaredMethods
        .filter(method => method.isAnnotationPresent(classOf[Inject]))
        .foreach(injectedMethod =>
          injectedMethod.getParameters.foreach(injectedMethodParameter =>
            scanInjectedParameter(
              componentClass,
              s"${injectedMethod} method",
              injectedMethodParameter,
              transitiveDependencyMetadataList
            )
          )
        )
      container.register(
        new OneDependency(
          componentName,
          componentClass,
          transitiveDependencyMetadataList.length,
          null,
          DependencyType.Component,
          null
        ),
        transitiveDependencyMetadataList.toList
      )

  private def scanAllConfigurationClasses(
      container: Container,
      configurationClasses: List[Class[?]]
    ): Unit =
    for configurationClass <- configurationClasses do
      if !hasOnlyDefaultConstructor(configurationClass) then
        throw new RuntimeException(
          s"$configurationClass is a @Configuration class and only default constructor is allowed"
        )
      configurationClass.getDeclaredMethods
        .filter(method => method.isAnnotationPresent(classOf[Bean]))
        .foreach(method =>
          val transitiveDependencyMetadataList = ListBuffer[TransitiveDependencyMetadata]()
          val beanName                         = getBeanNameFromBeanMethod(method)
          method.getParameters.foreach(parameter =>
            scanInjectedParameter(
              configurationClass,
              s"${method.getName} method",
              parameter,
              transitiveDependencyMetadataList
            )
          )
          container.register(
            new OneDependency(
              beanName,
              method.getReturnType,
              transitiveDependencyMetadataList.length,
              null,
              DependencyType.Bean,
              configurationClass
            ),
            transitiveDependencyMetadataList.toList
          )
        )

  private def scanAndLoadAllConfigurationPropertyClasses(
      container: Container,
      configurationPropertyClasses: List[Class[?]]
    ): Unit =
    configurationPropertyClasses.foreach(configurationPropertyClass =>
      container.register(
        new OneDependency(
          configurationPropertyClass.getSimpleName.toLowerCase,
          configurationPropertyClass,
          0,
          configLoader.get(configurationPropertyClass),
          DependencyType.ConfigurationProperty,
          null
        ),
        null
      )
    )
end Application
