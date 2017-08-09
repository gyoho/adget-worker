package com.gyoho.exercise

trait MyOption[+A] {
  def isDefined: Boolean
  def map[B](f: (A) => B): MyOption[B]
  def flatMap[B](f: (A) => MyOption[B]): MyOption[B]
//  def flatMap[B]( f : A => Option[B] ) : Option[B] = if ( isDefined ) f(this.get) else None
  def foreach[B]( f : (A) => Unit): Unit
  def get(): A
}

object MyOption {
  def apply[A](value: A): MyOption[A] = value match {
    case null => MyNone
    case _ => MySome(value)
  }
}

// why not Option[+A]
final case class MySome[+A](value: A) extends MyOption[A] {
  override def isDefined: Boolean = true
  override def get(): A = value
  override def map[B](f: (A) => B): MyOption[B] = MySome(f(value))
  override def flatMap[B](f: (A) => MyOption[B]): MyOption[B] = f(value)
  override def foreach[B](f: (A) => Unit): Unit = f(value)
}

// Nothing is subclass of all types (i.e bottom of the hierarchy)
case object MyNone extends MyOption[Nothing] {
  override def isDefined: Boolean = false
  override def get(): Nothing = throw new NoSuchElementException()
  override def map[B](f: (Nothing) => B): MyOption[B] = MyNone
  override def flatMap[B](f: (Nothing) => MyOption[B]): MyOption[B] = MyNone
  override def foreach[B](f: (Nothing) => Unit): Unit = {}
}