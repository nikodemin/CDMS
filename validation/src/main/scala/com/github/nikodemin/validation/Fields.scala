package com.github.nikodemin.validation

import scala.language.experimental.macros
import scala.reflect.macros.whitebox

abstract class Fields[T]

object FieldsMacro {
  def genFieldsObj[T]: Fields[T] = macro impl[T]

  def impl[T: c.WeakTypeTag](c: whitebox.Context): c.Expr[Fields[T]] = {
    import c.universe._

    val tpe = weakTypeOf[T]
    val fields = tpe.decls.collect {
      case m: MethodSymbol if m.isCaseAccessor =>
        val name = m.name.decodedName.toString
        q"val ${stringToTermName(name + "Field")}: Field[${tpe.typeSymbol}, ${m.typeSignature}] = Field($name, Lens[${tpe.typeSymbol}, ${m.typeSignature}](_.$m)(_ => x => x))"
    }
    val result =
      q"""
        import com.github.nikodemin.validation.Validators.Field
        import com.github.nikodemin.validation.Fields
        import monocle.Lens

        object FieldsObj extends Fields[${tpe.typeSymbol}] {
          ..$fields
        }

        FieldsObj
       """
    println(showCode(result))
    c.Expr[Fields[T]](result)
  }
}
