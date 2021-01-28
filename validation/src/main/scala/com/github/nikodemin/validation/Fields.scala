package com.github.nikodemin.validation

import com.github.nikodemin.validation.Validators.Field

import scala.language.experimental.macros
import scala.reflect.macros.whitebox

abstract class Fields[S] {
  def field[D](property: S => D): Field[S, D] = macro FieldsMacro.impl[S, D]
}

abstract class FieldsMonoid[S, M[_]] {
  def field[D](property: S => D): Field[Option[S], Option[D]] = macro FieldsMacro.monoidImpl[S, D, M]
}

abstract class FieldsOption[S] extends FieldsMonoid[S, Option]

class FieldsMacro(val c: whitebox.Context) {

  import c.universe._

  def impl[S: c.WeakTypeTag, D: c.WeakTypeTag](property: c.Expr[S => D]): c.Expr[Field[S, D]] = {
    val sourceType = weakTypeOf[S]
    val destinationType = weakTypeOf[D]

    val q"($x) => $x2.$name" = property.tree

    val field = sourceType.decls.collect {
      case m: MethodSymbol =>
        val methodName = m.name.decodedName.toString
        if (name.toString().equals(methodName)) {
          Some(q"Field($methodName, Lens[$sourceType, $destinationType]($property)(_ => x => x))")
        } else None
    }.filter(_.isDefined).map(_.get).head

    val result =
      q"""
        import com.github.nikodemin.validation.Validators.Field
        import monocle.Lens

        $field
       """
    c.Expr[Field[S, D]](result)
  }

  def monoidImpl[S: c.WeakTypeTag, D: c.WeakTypeTag, M[_]](property: c.Expr[S => D])(implicit mm: WeakTypeTag[M[_]]): c.Expr[Field[M[S], M[D]]] = {
    val sourceType = weakTypeOf[S]
    val destinationType = weakTypeOf[D]
    val typeConstructorType: c.universe.Ident = Ident(weakTypeOf[M[_]].typeConstructor.typeSymbol)

    val q"($x) => $x2.$name" = property.tree

    val field = sourceType.decls.collect {
      case m: MethodSymbol =>
        val methodName = m.name.decodedName.toString
        if (name.toString().equals(methodName)) {
          Some(q"Field($methodName, Lens[$typeConstructorType[$sourceType],$typeConstructorType[$destinationType]](_.map($property))(_ => x => x))")
        } else None
    }.filter(_.isDefined).map(_.get).head

    val result =
      q"""
        import com.github.nikodemin.validation.Validators.Field
        import monocle.Lens

        $field
       """
    c.Expr[Field[M[S], M[D]]](result)
  }
}