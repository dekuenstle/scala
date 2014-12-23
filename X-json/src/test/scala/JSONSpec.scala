import org.scalatest._

import JSON._

class JSONSpec extends FlatSpec {
  
  // Task X.3: test the JSON parser and evaluator

  // Simple values

  "JSON evaluator" should "handle numbers" in {
    assert(eval("5") == 5)
    assert(eval("-10") == -10)
    assert(eval("12.34") == 12.34)
    assert(eval("12.34e1") == 123.4)
    assert(eval("12.34e+1") == 123.4)
    assert(eval("12.34e-1") == 1.234)
  }

  it should "handle strings" in {
    assert(eval("\"hi\"") == "hi")

    // Triple quotation marks enclose string literals, too.
    // Characters are never escaped between triple quotes.
    assert(eval(""""hi"""") == "hi")
    assert(eval(""""\n"""") == "\n")
    assert(eval(""""\""""") == "\"")
  }

  // JSON objects

  // Scala has multi-line string literals. Everything between
  // triple quotation marks, including line breaks, are included
  // in the string.
  //
  // This example comes from the JSON examples page:
  //
  //   http://json.org/example
  //
  // The other examples should be useful for testing as well.
  val menu0: String = """
{"menu": {
  "id": "file",
  "value": "File",
  "popup": {
    "menuitem": [
      {"value": "New", "onclick": "CreateNewDoc()"},
      {"value": "Open", "onclick": "OpenDoc()"},
      {"value": "Close", "onclick": "CloseDoc()"}
    ]
  }
}}
"""

  // The String method `stripMargin` makes it possible to indent
  // multi-line strings.
  val menu: String =
    """|
       |{"menu": {
       |  "id": "file",
       |  "value": "File",
       |  "popup": {
       |    "menuitem": [
       |      {"value": "New", "onclick": "CreateNewDoc()"},
       |      {"value": "Open", "onclick": "OpenDoc()"},
       |      {"value": "Close", "onclick": "CloseDoc()"}
       |    ]
       |  }
       |}}
       |""".stripMargin

  "String.stripMargin" should "make string literals indentable" in {
    assert(menu0 == menu)
  }

  // For small JSON expressions like `menu`, it is feasible to test
  // by comparing the output of evaluation.

  val menuObjInScala = Map(
    "menu" -> Map(
      "id" -> "file",
      "value" -> "File",
      "popup" -> Map(
        "menuitem" -> List(
          42,
          42,
          Map("value" -> "Close", "onclick" -> "CloseDoc()")
        )
      )
    )
  )

  "`menu`" should "be parsed and evaluated correctly" in {
    assert(eval(menu) == menuObjInScala)
  }

  // Some JSON expressions are big. Translating them to Scala by hand
  // is error-prone. Instead of comparing the entire output, we can
  // check parts of it instead.

  "Subexpressions of `menu`" should "evaluate to the correct results" in {
    val menuObj = eval(menu)

    // menuObj.menu.id should be "file"
    assert(extract(menuObj, "menu", "id") == "file")

    // menuObj.menu.value should be "File"
    assert(extract(menuObj, "menu", "value") == "File")

    assert(extract(menuObj, "menu", "popup", "menuitem", "2", "onclick") == "CloseDoc()")
  }

  // Helper to extract nested JSON fields.
  //
  // DO MODIFY this method if your `eval` method does not follow
  // specification exactly.
  //
  //   extract(Map("hello" -> "world"), "hello") == "world"
  //
  //   extract(Map("manager" -> Map("name" -> "joe")), "manager", "name") == "joe"
  //
  //   extract(List("lorem", "ipsum"), "0") == "lorem"
  //
  def extract(jsonObj: Any, fields: String*): Any =
    if (fields.isEmpty)
      jsonObj
    else
      jsonObj match {
        case map: Map[_, _] =>
          val castedMap = map.asInstanceOf[Map[String, Any]]
          extract(castedMap(fields.head), fields.tail: _*)

        case list: List[_] =>
          extract(list(fields.head.toInt), fields.tail: _*)

        case _ =>
          sys error s"accessing field `${fields.head}` of non-JSON-object $jsonObj"
      }


  // your tests here
}
