package com.github.nikodemin.cdms.model.exceptions

case class EntityIdNotFound(message: String) extends IllegalStateException(message)
