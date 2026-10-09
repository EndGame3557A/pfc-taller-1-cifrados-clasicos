package taller

import org.scalatest.funsuite.AnyFunSuite
import org.junit.runner.RunWith
import org.scalatestplus.junit.JUnitRunner

/**
 * Cada ejemplo del enunciado es una prueba. Si el enunciado promete un valor,
 * aquí se comprueba que la solución lo produce.
 */
@RunWith(classOf[JUnitRunner])
class CifradosClasicosTest extends AnyFunSuite {

  val c = new CifradosClasicos()
  import c._

  // Punto 1: ejemplos del enunciado -------------------------------------------

  test("cesar: casa con 3 da fdvd") { assert(cesar("casa", 3) == "fdvd") }
  test("cesar: fdvd con -3 vuelve a casa") { assert(cesar("fdvd", -3) == "casa") }
  test("cesar: hola mundo con 1") { assert(cesar("hola mundo", 1) == "ipmb nvoep") }
  test("cesar: zzz con 1 da aaa") { assert(cesar("zzz", 1) == "aaa") }
  test("cesar: 29 es lo mismo que 3") { assert(cesar("abc", 29) == "def") }
  test("cesar: el mensaje vacío sale vacío") { assert(cesar("", 5) == "") }

  test("cesar: la puntuación y los dígitos pasan sin cambio") {
    assert(cesar("ab, 12!", 1) == "bc, 12!")
  }

  test("cesar: las mayúsculas no se cifran") {
    assert(cesar("Casa", 3) == "Cdvd")
  }

  test("cesar: cifrar y descifrar es la identidad") {
    assert(cesar(cesar("un mensaje cualquiera", 11), -11) == "un mensaje cualquiera")
  }
  
  // test agregados
  test("cesar: perro con 2 da rgttq") { assert(cesar("perro", 2) == "rgttq") }

  test("cesar: xyz con 3 da la vuelta y queda abc") { assert(cesar("xyz", 3) == "abc") }

  test("cesar: abc con -1 retrocede y queda zab") { assert(cesar("abc", -1) == "zab") }

  test("cesar: gato con 26 queda igual porque es una vuelta completa") {
    assert(cesar("gato", 26) == "gato")
  }

  test("cesar: mayúsculas y signos no cambian, solo las minúsculas") {
    assert(cesar("Hola, Mundo!", 2) == "Hqnc, Mwpfq!")
  }

  // Punto 2 -------------------------------------------------------------------

  test("cesarCola: casa con 3 da fdvd") { assert(cesarCola("casa", 3) == "fdvd") }
  test("cesarCola: hola mundo con 1") { assert(cesarCola("hola mundo", 1) == "ipmb nvoep") }
  test("cesarCola: con 0 el mensaje no cambia") { assert(cesarCola("abc", 0) == "abc") }

  test("cesarCola: da lo mismo que la versión lineal") {
    val casos = List(("casa", 3), ("hola mundo", 1), ("zzz", 1), ("abc", 29),
                     ("", 5), ("ab, 12!", -4))
    assert(casos.forall { case (m, k) => cesarCola(m, k) == cesar(m, k) })
  }

  test("cesarCola: aguanta un mensaje largo sin desbordar la pila") {
    val largo = "abcdefghij" * 20000
    assert(cesarCola(largo, 1).length == largo.length)
  }

  // test agregados
  test("cesarCola: zorro con 1 da apssp") { assert(cesarCola("zorro", 1) == "apssp") }

  test("cesarCola: xyz con 3 da la vuelta y queda abc") { assert(cesarCola("xyz", 3) == "abc") }

  test("cesarCola: abc con -1 retrocede y queda zab") { assert(cesarCola("abc", -1) == "zab") }

  test("cesarCola: el acumulador inicial se conserva al comienzo del resultado") {
    assert(cesarCola("bc", 1, "x") == "xcd")
  }

  test("cesarCola: mayúsculas y signos no cambian, solo las minúsculas") {
    assert(cesarCola("Hola, Mundo!", 2) == "Hqnc, Mwpfq!")
  }

  // Punto 3 -------------------------------------------------------------------

  test("frecuencias: casa") {
    assert(frecuencias("casa") == List(('a', 2), ('c', 1), ('s', 1)))
  }

  test("frecuencias: aabbbc") {
    assert(frecuencias("aabbbc") == List(('b', 3), ('a', 2), ('c', 1)))
  }

  test("frecuencias: hola mundo") {
    assert(frecuencias("hola mundo") ==
      List(('o', 2), ('a', 1), ('d', 1), ('h', 1), ('l', 1), ('m', 1),
           ('n', 1), ('u', 1)))
  }

  test("frecuencias: el mensaje vacío no tiene letras") {
    assert(frecuencias("") == List())
  }

  test("frecuencias: un mensaje sin letras no tiene frecuencias") {
    assert(frecuencias("123 !?") == List())
  }

  test("frecuencias: en empate manda el orden alfabético") {
    assert(frecuencias("ba") == List(('a', 1), ('b', 1)))
  }

  //test agregados
  test("frecuencias: banana") {
    assert(frecuencias("banana") == List(('a', 3), ('n', 2), ('b', 1)))
  }

  test("frecuencias: mississippi, i y s empatan y gana el orden alfabético") {
    assert(frecuencias("mississippi") == List(('i', 4), ('s', 4), ('p', 2), ('m', 1)))
  }

  test("frecuencias: las mayúsculas no se cuentan") {
    assert(frecuencias("AaBb") == List(('a', 1), ('b', 1)))
  }

  test("frecuencias: los espacios no se cuentan y se ordena de mayor a menor") {
    assert(frecuencias("zzz aa y") == List(('z', 3), ('a', 2), ('y', 1)))
  }

  test("frecuencias: la suma de las frecuencias es la cantidad de minúsculas") {
    val m = "Hola, Mundo 2024!"
    assert(frecuencias(m).map(_._2).sum == m.count(esMinuscula))
  }


  // Punto 4 -------------------------------------------------------------------

  test("desplazamientoProbable: h está 3 después de e") {
    assert(desplazamientoProbable("h") == 3)
  }

  test("desplazamientoProbable: hhhaa, con h como la más frecuente") {
    assert(desplazamientoProbable("hhhaa") == 3)
  }

  test("desplazamientoProbable: sin letras da 0") {
    assert(desplazamientoProbable("123") == 0)
  }

  test("desplazamientoProbable: en empate manda la primera alfabéticamente") {
    // 'a' y 'h' aparecen tres veces; gana 'a', que está 22 después de 'e'.
    assert(desplazamientoProbable("hhhaaa") == 22)
  }

  test("romperCesar: recupera un mensaje con suficientes letras e") {
    val original = "el mensaje secreto"
    assert(romperCesar(cesar(original, 7)) == original)
  }

  test("romperCesar: el método falla cuando la e no es la más frecuente") {
    // En este mensaje la letra más frecuente es la 'a', no la 'e'.
    val original = "cada casa amarilla"
    assert(romperCesar(cesar(original, 7)) != original)
  }
  //test agregados

  test("desplazamientoProbable: z está 21 después de e") {
    assert(desplazamientoProbable("zzz") == 21)
  }

  test("desplazamientoProbable: si la más frecuente es e, no hay desplazamiento") {
    assert(desplazamientoProbable("eee") == 0)
  }

  test("desplazamientoProbable: c está antes de e, el resultado se corrige a 24") {
    assert(desplazamientoProbable("dcc") == 24)
  }

  test("romperCesar: hhh se descifra como eee") {
    assert(romperCesar("hhh") == "eee")
  }

  test("romperCesar: recupera un mensaje cifrado con k = 9") {
    val original = "este es el mensaje"
    assert(romperCesar(cesar(original, 9)) == original)
  }

  // Punto 5 -------------------------------------------------------------------

  test("combinaciones: con longitud 0 hay un mensaje, el vacío") {
    assert(combinaciones(0, 26) == BigInt(1))
  }

  test("combinaciones: con longitud 1 hay tantos como letras") {
    assert(combinaciones(1, 26) == BigInt(26))
  }

  test("combinaciones: 3 letras sobre 26 dan 16250") {
    assert(combinaciones(3, 26) == BigInt(16250))
  }

  test("combinaciones: 2 letras sobre un alfabeto de 2 dan 2") {
    assert(combinaciones(2, 2) == BigInt(2))
  }

  test("combinaciones: crece según la recurrencia") {
    assert(combinaciones(5, 4) == BigInt(3) * combinaciones(4, 4))
  }

  test("vigenere: ataque con la clave sol") {
    assert(vigenere("ataque", "sol") == "shliip")
  }

  test("vigenere: hola mundo con la clave ab") {
    assert(vigenere("hola mundo", "ab") == "hplb mvneo")
  }

  test("vigenere: con la clave vacía el mensaje no cambia") {
    assert(vigenere("casa", "") == "casa")
  }

  test("vigenere: el espacio no consume letra de la clave") {
    // Sin el espacio la clave iría corrida y la m se cifraría con b.
    assert(vigenere("hola mundo", "ab").charAt(5) == 'm')
  }

  test("vigenere: con una clave de una sola letra es un César") {
    assert(vigenere("hola mundo", "d") == cesar("hola mundo", 3))
  }
  
  //test agregados
  test("combinaciones: 4 letras sobre 10 dan 7290") {
    assert(combinaciones(4, 10) == BigInt(7290))
  }
  test("combinaciones: con una sola letra no se puede formar un mensaje de longitud 3") {
    assert(combinaciones(3, 1) == BigInt(0))
  }
  test("combinaciones: con longitud 30 y 26 letras el resultado necesita BigInt") {
    assert(combinaciones(30, 26) == BigInt(25).pow(29) * 26)
  }
  test("vigenere: xyz con la clave ccc da la vuelta y queda zab") {
    assert(vigenere("xyz", "ccc") == "zab")
  }
  test("vigenere: las mayúsculas y los espacios se copian y no consumen clave") {
    assert(vigenere("A b c", "ab") == "A b d")
}
