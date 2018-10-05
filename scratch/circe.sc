import io.circe._, io.circe.parser._, io.circe.generic.JsonCodec, io.circe.syntax._, io.circe.generic.extras._

val rawJson: String = """
{
  "foo": "bar",
  "baz": 123,
  "list_of_stuff": [ 4, 5, 6 ]
}
"""

val doc: Json = parse(rawJson).getOrElse(Json.Null)

val cursor: HCursor = doc.hcursor

val foo = cursor.get[String]("foo")


implicit val config: Configuration = Configuration.default.withSnakeCaseKeys

@ConfiguredJsonCodec case class Test(foo: String, baz: Int, listOfStuff: List[Int])

object Test {
  val default = Test("", 0, List.empty[Int])
}

val test = doc.as[Test].getOrElse(Test.default)
test.foo
test.baz
test.listOfStuff


Test("bar", 123, List(4,5,6)).asJson
