package taller

import scala.annotation.tailrec

/**
 * Taller 1 — cifrados clásicos con recursión.
 *
 * Solo se cifran las 26 letras minúsculas del alfabeto inglés; cualquier otro
 * carácter se copia sin cambio.
 */
class CifradosClasicos {

  type Mensaje = String
  type Clave = String

  // Una frecuencia asocia cada letra con las veces que aparece.
  type Frecuencias = List[(Char, Int)]

  val letras = 26 //abecedario
  val primera = 'a'.toInt //valor ASCII de 'a'

  def esMinuscula(c: Char): Boolean = c >= 'a' && c <= 'z'

  // Punto 1 -------------------------------------------------------------------

  /** César con recursión lineal: una operación pendiente por letra. */
  def cesar(m: Mensaje, k: Int): Mensaje = {
    if (m.isEmpty) ""
    else {
      val c = m.head //toma la primera letra de la frase
      val nuevaLetra = if (esMinuscula(c)) {  //c es minuscula?
        val desplazado = ((c.toInt - primera + k) % letras + letras) % letras + primera
        //c.toInt hace el desplazamiento - primera es el valor ASCII de 'a'
        //c=99 a=97 99-97=2 -> a=0, b=1, c=2... z=25
        //+k desplazamiento
        //%letras para que se repita al llegar a 25  26%26=0 0=a
        //+letras para que no sea negativo
        //+primera para que sea el valor ASCII de la letra
        desplazado.toChar //pasa el valor ASCII a un char
      } else c //si no es minuscula, no se desplaza, se deja igual
      nuevaLetra + cesar(m.tail, k) //"borra" la letra de la frase y vuelve a iniciar
    }
  }

  // Punto 2 -------------------------------------------------------------------

  /**
   * El mismo César como proceso iterativo: espacio constante.
   * Cuando la función esté escrita, anótela con @tailrec: el compilador
   * comprueba que la llamada recursiva sea lo último que hace.
   */
    @tailrec // recursion de cola
  final def cesarCola(m: Mensaje, k: Int, acc: Mensaje = ""): Mensaje = {
      //final def no puede sobreescribirse
    if (m.isEmpty) acc //si ya no hay letras, devuelve el acumulador (frase desplazada)
    else {
      val c = m.head //primera letra
      val nuevaLetra = if (esMinuscula(c)) { //si c es minuscula
        val desplazado = ((c.toInt - primera + k) % letras + letras) % letras + primera
        // mismo proceso que cesar
        desplazado.toChar //vuelve el valor ascii a char
      } else c //si no es minuscula, no se desplaza
      cesarCola(m.tail, k, acc + nuevaLetra)
      //m.tail borra la letra y vuelve a iniciar, k no se modifica,
      //acc es lo que llevamos, nuevaLetra es la letra desplazada
    }
  }

  // Punto 3 -------------------------------------------------------------------

  /**
   * Cuenta las letras minúsculas del mensaje, de mayor a menor frecuencia y,
   * en empate, en orden alfabético. El recorrido es recursivo de cola.
   */
  def frecuencias(m: Mensaje): Frecuencias = {
    @tailrec
    def contar(m: Mensaje, acc: Map[Char, Int]): Map[Char, Int] = {
      if (m.isEmpty) acc //se verifica si el mensaje está vacío
      else {
        val c = m.head //se toma la primer letra del mensaje actual
        val nuevaAcc = if (esMinuscula(c)) {
          acc + (c -> (acc.getOrElse(c, 0) + 1)) //se suma 1 a la frecuencia de la letra
        } else acc
        contar(m.tail, nuevaAcc) //se borra la letra y vuelve a iniciar
      }
    }

    val freqMap = contar(m, Map.empty) //se llama a la función contar para contar las frecuencias
    freqMap.toList.sortBy { case (c, count) => (-count, c) } //se ordenan las frecuencias de mayor a menor
  }

  // Punto 4 -------------------------------------------------------------------

  /**
   * Supone que la letra más frecuente del mensaje cifrado es la 'e' del
   * original y devuelve la distancia entre las dos. Sin letras, cero.
   */
  def desplazamientoProbable(m: Mensaje): Int = {
    val freq = frecuencias(m)  //se llama a la función frecuencias para obtener las frecuencias del mensaje
    if (freq.isEmpty) 0 //si el mensaje está vacío, devuelve 0
    else {
      val letraMasFrecuente = freq.head._1 //se toma la primer letra del mensaje actual

      val desplazamiento = letraMasFrecuente - 'e'// se calcula la distancia entre la letra más frecuente y la letra 'e'

      if (desplazamiento < 0) desplazamiento + letras else desplazamiento
      //si el desplazamiento da negativo, se le suma la cantidad de letras para que vuelva a ser positivo
    }
  }

  def romperCesar(m: Mensaje): Mensaje = {
    val k = desplazamientoProbable(m) //el valor de la letra
    cesarCola(m, -k) //se desplaza el mensaje
  }

  // Punto 5 -------------------------------------------------------------------

  /**
   * Cuántos mensajes de longitud n se forman con a letras sin dos iguales
   * seguidas.
   */
  def combinaciones(n: Int, a: Int): BigInt = { //
    if (n == 0) BigInt(1)
    else if (n == 1) BigInt(a)
    else {
      BigInt(a - 1) * combinaciones(n - 1, a)
    }
  }

  /**
   * Vigenère: cada letra se corre según la letra de la clave que le toca. Lo
   * que no es letra minúscula se copia y no consume clave.
   */
  def vigenere(m: Mensaje, clave: Clave): Mensaje = {
  if (m.isEmpty) ""
    else {
      val c = m.head // inicio del mensaje
      if(clave.isEmpty) m
      else{
        if (esMinuscula(c)) {
        val desplazamiento = clave.head - 'a'
        val letra = ((c - 'a' + desplazamiento) % 26 + 'a').toChar // nueva letra del cifradp

        letra + vigenere(m.tail, clave.tail + clave.head) // la clave va rotando ciclicamente
      } else {
        c + vigenere(m.tail, clave)
      }
      }
    }
  }
}

