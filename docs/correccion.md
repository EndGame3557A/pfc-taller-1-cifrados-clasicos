# Informe de Corrección Matemática

**Curso:** Programación Funcional y Concurrente  
**Universidad del Valle** — Semestre 2026-2

---

## 1. Definición Formal del Modelo Matemático

Sea $\Sigma = \{'a', 'b', \dots, 'z'\}$ el alfabeto de 26 letras minúsculas. Definimos la función posición $P: \Sigma \to \mathbb{Z}_{26}$ como $P(c) = \text{ASCII}(c) - 97$, donde $97$ corresponde al valor ASCII constante de `'a'` (`primera`), y su inversa $P^{-1}: \mathbb{Z}_{26} \to \Sigma$.

Para cualquier carácter $c$:
$$f_{\text{shift}}(c, k) = \begin{cases}  P^{-1}\Big(\big((P(c) + k) \bmod 26 + 26\big) \bmod 26\Big) & \text{si } c \in \Sigma \\ c & \text{si } c \notin \Sigma  \end{cases}$$

Esta especificación formal se corresponde exactamente con la expresión Scala de la función `cesar`:
```scala
val desplazado = ((c.toInt - primera + k) % letras + letras) % letras + primera
```

## 2. Demostración de Corrección por Inducción Estructural (cesar)
### Propiedad $\mathcal{P}(m)$:
Para todo mensaje $m \in \text{String}$ y desplazamiento $k \in \mathbb{Z}$, la función cesar(m, k) genera la transformación $f_{\text{shift}}$ elemento a elemento preservando la estructura y longitud del texto.
### Caso Base:
Sea $m =$ "" (cadena vacía, longitud 0).
Según la implementación:
```scala
if (m.isEmpty) ""
```
Devuelve "", el cual coincide con el mapeo de una secuencia vacía. Por lo tanto, $\mathcal{P}("")$ se cumple trivialmente.

### Paso Inductivo:
Asumimos como Hipótesis Inductiva (H.I.) que para un mensaje arbitrario $t \in \text{String}$, cesar(t, k) satisface la propiedad $\mathcal{P}(t)$.
Consideremos $m = h :: t$ (donde $h = m.\text{head}$ y $t = m.\text{tail}$):

```scala
val nuevaLetra = if (esMinuscula(c)) desplazado.toChar else c
nuevaLetra + cesar(m.tail, k)
```
1. Por definición algorítmica, nuevaLetra calcula $f_{\text{shift}}(h, k)$ para el primer carácter.
2. Por **H.I.**, cesar(m.tail, k) retorna la secuencia corregida para la cola $t$.
3. La concatenación de nuevaLetra con el resultado de cesar(t, k) produce la cadena $f_{\text{shift}}(h, k) :: \text{cesar}(t, k)$, cumpliendo la propiedad $\mathcal{P}(h :: t)$.

Por principio de inducción estructural sobre cadenas, cesar(m, k) es funcionalmente correcta $\forall m$.
## 3. Demostración de Equivalencia entre $cesar$ y $cesarCola$
### Teorema (Invariante del Acumulador):
Para toda cadena $m$, entero $k$ y acumulador $acc$:
$$\text{cesarCola}(m, k, acc) = acc + \text{cesar}(m, k)$$
### Prueba por Inducción sobre la longitud de $m$:
- Base ($m = $""):
  - Lado Izquierdo: $cesarCola($""$, k, acc)$ evalúa a $acc$ (por caso base $if (m.isEmpty) acc$).
  - Lado Derecho: $acc + cesar("", k) = acc + "" = acc$. 
  - Ambos lados coinciden. 
  - Paso Inductivo($m = h :: t$):
  $$\text{cesarCola}(h :: t, k, acc) = \text{cesarCola}(t, k, acc + f_{\text{shift}}(h, k))$$

  Aplicando la *H.I.* sobre la cola $t$ con el nuevo acumulador $(acc + f_{\text{shift}}(h, k))$:$$= (acc + f_{\text{shift}}(h, k)) + \text{cesar}(t, k)$$
  Por la propiedad asociativa de la concatenación de cadenas:$$= acc + (f_{\text{shift}}(h, k) + \text{cesar}(t, k)) = acc + \text{cesar}(h :: t, k)$$


Por ende, evaluando con el acumulador inicial por defecto acc = "":$$\text{cesarCola}(m, k, "") = "" + \text{cesar}(m, k) = \text{cesar}(m, k)$$
Queda demostrado que $cesar$ y $cesarCola$ son totalmente equivalentes para cualquier entrada.

## 4. Análisis Formal del Conteo de Combinaciones $C(n, a)$
Se requiere contar los mensajes de longitud $n$ formados sobre un alfabeto de $a$ símbolos sin dos caracteres contiguos idénticos.
### Definición Recurrente:

```Scala
def combinaciones(n: Int, a: Int): BigInt = {
  if (n == 0) BigInt(1)
  else if (n == 1) BigInt(a)
  else {
    BigInt(a - 1) * combinaciones(n - 1, a)
  }
}
```
### Demostración de la Forma Cerrada:
Demostramos que para $n \ge 1$, la función implementada satisface la relación $C(n, a) = a \cdot (a - 1)^{n - 1}$.
- *Base ($n = 1$):*$$C(1, a) = a \cdot (a - 1)^{1 - 1} = a \cdot (a - 1)^0 = a \cdot 1 = a$$ Coincide con el caso base $else if (n == 1) BigInt(a)$.
- *Paso Inductivo ($n = k + 1$):* Por código, $combinaciones(k + 1, a)$ ejecuta $BigInt(a - 1) * combinaciones(k, a)$. Sustituyendo la *H.I.* $C(k, a) = a \cdot (a - 1)^{k - 1}$:$$C(k + 1, a) = (a - 1) \cdot \left[ a \cdot (a - 1)^{k - 1} \right] = a \cdot (a - 1)^k$$


La implementación recursiva de la función computa de manera exacta la fórmula matemática esperada.

## 5. Análisis de Fallo en $romperCesar$
El método $romperCesar$ se apoya en $desplazamientoProbable$, el cual toma la letra con mayor frecuencia en el texto cifrado y calcula su distancia respecto a la letra $'e'$:

```Scala
val desplazamiento = letraMasFrecuente - 'e'
```

### 5.1 Condiciones bajo las cuales el método falla
1. *Ausencia o baja frecuencia de la letra 'e':* Si el texto plano original no contiene la letra $'e'$ o si otra letra (como $'a'$ o $'o'$) aparece un mayor número de veces.
2. *Textos o mensajes muy cortos:* En mensajes de poca longitud no se cumple la distribución estadística habitual del idioma.
3. *Criterio de desempaque en empates de frecuencia:* En caso de empate en la máxima frecuencia, la función $frecuencias$ ordena los pares mediante $sortBy { case (c, count) => (-count, c) }$. Esto selecciona automáticamente la letra de menor orden alfabético, la cual puede no corresponder a la rotación real de la $'e'$.


### 5.2 Contraejemplo Concreto de Fallo
Consideremos el siguiente mensaje original en texto plano:
$$m = \text{"papa"}$$
1. *Frecuencias originales:*$'a' -> 2$, $'p' -> 2$. (Nótese que la letra $'e'$ no aparece en la cadena).
2. *Cifrado con desplazamiento $k = 5$:*$$\text{cesarCola}("papa", 5) = "ufuf"$$
3. *Conteo de frecuencias en $"ufuf"$:* 
Tanto $'f'$ como $'u'$ aparecen $2$ veces.
Al aplicar $frecuencias("ufuf")$, el ordenamiento por $(-count, c)$ sitúa primero a la $'f'$ porque $'f' < 'u'$:$$\text{freq} = \text{List}(('f', 2), ('u', 2))$$
Por ende, $freq.head._1$ retorna $'f'$. 
4. *Cálculo de desplazamiento estimado:* $$\text{desplazamiento} = 'f' - 'e' = 102 - 101 = 1$$
$desplazamientoProbable("ufuf")$ retorna $1$ (en lugar del valor real $k = 5$).
5. *Descifrado fallido:*$$\text{romperCesar}("ufuf") = \text{cesarCola}("ufuf", -1) = "tete"$$
6. *Conclusión:*$$\text{romperCesar}(\text{cesar}("papa", 5)) = "tete" \neq "papa"$$


Esto prueba que el algoritmo falla cuando la hipótesis estadística de prevalencia de la letra $'e'$ no se cumple en el texto plano original.

## 6. Definición Formal y Análisis del Cifrado Vigenere

El cifrado Vigenere extiende el cifrado por sustitución asociando a cada carácter del mensaje $m$ un carácter de la clave $K$ de manera cíclica.

### Definición Recurrente Formal:
Sea $m = c :: m'$ el mensaje y $K = k :: K'$ la clave de caracteres (donde $P(k) = \text{ASCII}(k) - 97$). La función $f_{\text{vigenere}}(m, K)$ se define formalmente como:

$$f_{\text{vigenere}}(c :: m', k :: K') = \begin{cases}  f_{\text{shift}}(c, P(k)) :: f_{\text{vigenere}}(m', K' :: k) & \text{si } c \in \Sigma \\  c :: f_{\text{vigenere}}(m', k :: K') & \text{si } c \notin \Sigma  \end{cases}$$

Con los casos base:
- $f_{\text{vigenere}}($""$, K) = $""
- $f_{\text{vigenere}}(m, $""$) = m$

### Correspondencia con la Implementación en Scala:
En la implementación recursiva de `vigenere`:
- Si el carácter $c$ es una letra minúscula (`esMinuscula(c)` es `true`), se calcula el desplazamiento $P(\text{clave.head}) = \text{clave.head} - \text{'a'}$ y se aplica la rotación de la clave pasando el primer carácter al final: `vigenere(m.tail, clave.tail + clave.head)`.
- Si el carácter $c$ **no** es una letra minúscula, se conserva en la salida ($c + \text{vigenere}(m.tail, \text{clave})$) **sin consumir ni rotar** la clave, cumpliendo con la regla del taller.