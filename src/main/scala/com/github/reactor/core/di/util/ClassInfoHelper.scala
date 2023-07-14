package com.github.reactor.core.di.util

import com.github.reactor.core.di.annotations.{Bean, Component, Inject}
import org.slf4j.LoggerFactory

import java.io.File
import java.lang.reflect.{Constructor, Field, Method, Parameter}
import scala.collection.mutable.ListBuffer

object ClassInfoHelper:

  private val logger = LoggerFactory.getLogger(getClass)

  def getBeanNameFromComponentClass(className: Class[?]): String =
    if className.getAnnotation(classOf[Component]).name().nonEmpty then className.getAnnotation(classOf[Component]).name()
    else className.getSimpleName.toLowerCase

  def getBeanNameFromInjectedField(field: Field): String =
    if field.getAnnotation(classOf[Inject]).name().nonEmpty then field.getAnnotation(classOf[Inject]).name()
    else field.getType.getSimpleName.toLowerCase

  def getBeanNameFromInjectedParameter(parameter: Parameter): String =
    if parameter.getAnnotation(classOf[Inject]).name().nonEmpty then parameter.getAnnotation(classOf[Inject]).name()
    else parameter.getType.getSimpleName.toLowerCase

  def getBeanNameFromBeanMethod(method: Method): String =
    if method.getAnnotation(classOf[Bean]).name().nonEmpty then method.getAnnotation(classOf[Bean]).name()
    else method.getName

  def hasOnlyDefaultConstructor(className: Class[?]): Boolean =
    hasOnlyOneConstructor(className) && isEmptyConstructor(className.getDeclaredConstructors.head)

  def hasOnlyOneConstructor(className: Class[?]): Boolean = className.getDeclaredConstructors.length == 1

  def isEmptyConstructor(constructor: Constructor[?]): Boolean = constructor.getParameterCount == 0

  private def getAllClassesForPackageNamesSeparatedByComma(packageNamesSeparatedByComma: String): List[Class[?]] =
    if packageNamesSeparatedByComma.nonEmpty then
      val allPackages = ListBuffer[String]()
      for packageName <- packageNamesSeparatedByComma.split(",") do allPackages.addOne(packageName)
      ClassInfoHelper.getAllClasses(allPackages.toList)
    else List()

  private def getAllClasses(packageNames: List[String]): List[Class[?]] =
    val classList = ListBuffer[Class[?]]()
    if packageNames.nonEmpty then
      classList.addAll(
        packageNames
          .filter(_.nonEmpty)
          .flatMap(getAllClasses)
      )
    classList.toList

  def getAllClasses(packageName: String): List[Class[?]] =
    val classList          = ListBuffer[Class[?]]()
    val updatedPackageName = updatePackageName(packageName)
    val classLoader        = ClassLoader.getSystemClassLoader
    val packageUrl         = classLoader.getResource(updatedPackageName)
//    logger.debug(s"updatedPackageName is $updatedPackageName, packageName is $packageName, packageUrl is $packageUrl")
    if packageUrl != null then
      val packageDir = new File(packageUrl.getFile)
      for file <- packageDir.listFiles() do
        if file.isDirectory then classList.addAll(getAllClasses(s"${packageName}.${file.getName}"))
        if file.isFile && file.getName.endsWith(".class") then
          try
            val className = s"${packageName}.${file.getName.replace(".class", "")}"
//            logger.debug(s"className is ${className}")
            classList.addOne(classLoader.loadClass(className))
          catch case e: ClassNotFoundException => e.printStackTrace()
    classList.toList

  private def updatePackageName(packageName: String) = s"$packageName".replace('.', '/')
end ClassInfoHelper
