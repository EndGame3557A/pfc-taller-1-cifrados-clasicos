# Informe de Corrección Matemática

**Curso:** Programación Funcional y Concurrente  
**Universidad del Valle** — Semestre 2026-2

---

## 1. Definición Formal del Modelo Matemático

Sea $\Sigma = \{\texttt{a}, \texttt{b}, \dots, \texttt{z}\}$ el alfabeto de 26 letras minúsculas. Definimos la función posición $P: \Sigma \to \mathbb{Z}_{26}$ como $P(c) = \text{ASCII}(c) - 97$, donde $97$ es el valor ASCII de `'a'`, y su inversa $P^{-1}: \mathbb{Z}_{26} \to \Sigma$.

Notación: $h \cdot t$ es la cadena con primer carácter $h$ y resto $t$; $u \mathbin{+\!\!+} v$ es la concatenación; $\varepsilon$ es la cadena vacía.

Para cualquier carácter $c$:

$$
f_{\text{shift}}(c, k) =
\begin{cases}
P^{-1}\Big(\big((P(c) + k) \bmod 26 + 26\big) \bmod 26\Big) & \text{si } c \in \Sigma \\
c & \text{si } c \notin \Sigma
\end{cases}
$$

El doble módulo es necesario porque el operador `%` de Scala puede devolver valores negativos cuando $k < 0$. Para cualquier entero $x$, $(x \bmod 26 + 26) \bmod 26 \in [0, 25]$ y es congruente con $x$ módulo 26. Por eso la fórmula cubre desplazamientos negativos y mayores que 26, y además $f_{\text{shift}}(c, k) = f_{\text{shift}}(c, k \bmod 26)$.
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
### Prueba por inducción sobre la longitud de $m$

La hipótesis inductiva se plantea **para todo acumulador** $acc$, no para uno fijo. Es lo que permite aplicarla con el acumulador actualizado.

**Base** ($m = \varepsilon$):

- Lado izquierdo: $\text{cesarCola}(\varepsilon, k, acc) = acc$, por el caso base de la función.
- Lado derecho: $acc \mathbin{+\!\!+} \text{cesar}(\varepsilon, k) = acc \mathbin{+\!\!+} \varepsilon = acc$.

Ambos lados coinciden.

**Paso inductivo** ($m = h \cdot t$). Por definición de `cesarCola`:

$$
\text{cesarCola}(h \cdot t, k, acc) = \text{cesarCola}\big(t, k, acc \mathbin{+\!\!+} f_{\text{shift}}(h, k)\big)
$$

Por la H.I. aplicada a $t$ con el acumulador $acc \mathbin{+\!\!+} f_{\text{shift}}(h, k)$:

$$
= \big(acc \mathbin{+\!\!+} f_{\text{shift}}(h, k)\big) \mathbin{+\!\!+} \text{cesar}(t, k)
$$

Por asociatividad de la concatenación y por la definición de `cesar`, que dice $\text{cesar}(h \cdot t, k) = f_{\text{shift}}(h, k) \mathbin{+\!\!+} \text{cesar}(t, k)$:

$$
= acc \mathbin{+\!\!+} \big(f_{\text{shift}}(h, k) \mathbin{+\!\!+} \text{cesar}(t, k)\big) = acc \mathbin{+\!\!+} \text{cesar}(h \cdot t, k)
$$

**Conclusión.** Con el acumulador inicial por defecto $acc = \varepsilon$:

$$
\text{cesarCola}(m, k, \varepsilon) = \varepsilon \mathbin{+\!\!+} \text{cesar}(m, k) = \text{cesar}(m, k)
$$

Por tanto `cesar` y `cesarCola` son equivalentes para toda entrada. $\blacksquare$

### 3.1 Cómo se encadenan los llamados en la ejecución

**`cesar("casa", 3)` (recursión lineal).** Cada llamado deja una concatenación pendiente que solo se resuelve cuando regresa el llamado siguiente:

```text
cesar("casa", 3)
= 'f' ++ cesar("asa", 3)
= 'f' ++ ('d' ++ cesar("sa", 3))
= 'f' ++ ('d' ++ ('v' ++ cesar("a", 3)))
= 'f' ++ ('d' ++ ('v' ++ ('d' ++ cesar("", 3))))      <- caso base: ""
= 'f' ++ ('d' ++ ('v' ++ ('d' ++ "")))
= 'f' ++ ('d' ++ ('v' ++ "d"))
= 'f' ++ ('d' ++ "vd")
= 'f' ++ "dvd"
= "fdvd"
```

Cada letra se calcula con $f_{\text{shift}}$: $P(\texttt{c}) = 2$, $2 + 3 = 5$, y $P^{-1}(5) = \texttt{f}$. Del mismo modo $\texttt{a} \to \texttt{d}$, $\texttt{s}$ ($18$) $\to \texttt{v}$ ($21$), $\texttt{a} \to \texttt{d}$.

**`cesarCola("casa", 3)` (recursión de cola).** El resultado parcial viaja en el acumulador, y el llamado recursivo es la última operación. No queda nada pendiente:

```text
cesarCola("casa", 3, "")
-> cesarCola("asa", 3, "f")
-> cesarCola("sa",  3, "fd")
-> cesarCola("a",   3, "fdv")
-> cesarCola("",    3, "fdvd")     <- caso base: devuelve acc
= "fdvd"
```

La diferencia con la versión lineal es que aquí el resultado ya está completo cuando se llega al caso base. Por eso Scala puede reutilizar el mismo marco de pila (`@tailrec`) y la ejecución usa espacio constante.

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


## 6. Definición Formal y Corrección del Cifrado Vigenere

Sea $K = k_0 k_1 \dots k_{n-1}$ la clave, con $n \ge 1$ y cada $k_i \in \Sigma$, y sea $P(k_i) = \text{ASCII}(k_i) - 97$. Definimos $v(m, K)$ por recursión sobre $m$:

$$
\begin{aligned}
v(\varepsilon, K) &= \varepsilon \\
v(m, \varepsilon) &= m \\
v(c \cdot m', k \cdot K') &=
\begin{cases}
f_{\text{shift}}(c, P(k)) \cdot v\big(m', K' \mathbin{+\!\!+} k\big) & \text{si } c \in \Sigma \\
c \cdot v\big(m', k \cdot K'\big) & \text{si } c \notin \Sigma
\end{cases}
\end{aligned}
$$

En la implementación, $K' \mathbin{+\!\!+} k$ corresponde a `clave.tail + clave.head`, y $P(k)$ a `clave.head - 'a'`.

### Teorema

Sea $R^s(K)$ la clave rotada $s$ posiciones a la izquierda, es decir, $R^s(K) = k_{s \bmod n}\, k_{(s+1) \bmod n} \dots$. Para toda cadena $m$ y todo $s \ge 0$, $v(m, R^s(K))$ es la cadena $m$ en la que la $j$-ésima **letra** de $m$ (contando desde $0$ solo las letras de $\Sigma$) se reemplaza por

$$
f_{\text{shift}}\big(c_j,\; P(k_{(s+j) \bmod n})\big)
$$

y los caracteres que no son letras quedan intactos. En particular, con $s = 0$, la $j$-ésima letra se corre según $k_{j \bmod n}$: la clave se repite cíclicamente y los caracteres que no son letras **no consumen** clave.

### Demostración por inducción sobre la longitud de $m$

La propiedad se plantea para todo $s$.

**Base** ($m = \varepsilon$): $v(\varepsilon, R^s(K)) = \varepsilon$, que no tiene letras que cambiar. Se cumple.

**Paso inductivo** ($m = c \cdot m'$), con la H.I. válida para $m'$ y todo $s$:

- *Caso $c \notin \Sigma$.* Por definición, $v(c \cdot m', R^s(K)) = c \cdot v(m', R^s(K))$. El carácter $c$ se conserva, y las letras de $m'$ tienen los **mismos índices** $j$ que en $m$, porque $c$ no es letra. La H.I. con el mismo $s$ da el desplazamiento $P(k_{(s+j) \bmod n})$ para la $j$-ésima letra. Se cumple.
- *Caso $c \in \Sigma$.* La cabeza de $R^s(K)$ es $k_{s \bmod n}$, así que $c$ (la letra $j = 0$) se reemplaza por $f_{\text{shift}}(c, P(k_{s \bmod n}))$, como exige el teorema. La clave restante es $R^s(K)$ con su primer elemento enviado al final, que es exactamente $R^{s+1}(K)$. La letra $j \ge 1$ de $m$ es la letra $j-1$ de $m'$, y por H.I. con $s+1$ se corre con $P(k_{(s+1+(j-1)) \bmod n}) = P(k_{(s+j) \bmod n})$. Se cumple.

Con $s = 0$ queda demostrada la corrección. Si la clave es vacía, la segunda ecuación de $v$ devuelve $m$ sin cambios, como pide el enunciado. $\blacksquare$

### Traza: `vigenere("hola mundo", "ab")`

| Carácter | Clave actual | Desplazamiento | Salida | Clave siguiente |
|:--------:|:------------:|:--------------:|:------:|:---------------:|
| `h` | `ab` | $0$ | `h` | `ba` |
| `o` | `ba` | $1$ | `p` | `ab` |
| `l` | `ab` | $0$ | `l` | `ba` |
| `a` | `ba` | $1$ | `b` | `ab` |
| ` ` | `ab` | no aplica | ` ` | `ab` (no cambia) |
| `m` | `ab` | $0$ | `m` | `ba` |
| `u` | `ba` | $1$ | `v` | `ab` |
| `n` | `ab` | $0$ | `n` | `ba` |
| `d` | `ba` | $1$ | `e` | `ab` |
| `o` | `ab` | $0$ | `o` | `ba` |

Resultado: `"hplb mvneo"`. El espacio se copia sin consumir letra de la clave, y por eso la `m` de `mundo` se cifra con `a`.





## 7. Corrección de `frecuencias`

Para $c \in \Sigma$ y una cadena $m$, sea $N(c, m)$ el número de veces que $c$ aparece en $m$. La especificación del taller es

$$
\text{frecuencias}(m) = \text{ordenar}_{(-n,\, c)}\big(\{(c, N(c, m)) : c \in \Sigma,\ N(c, m) > 0\}\big)
$$

es decir, solo las letras que aparecen, de mayor a menor frecuencia y, a igual frecuencia, en orden alfabético.

### Correspondencia con la implementación

```scala
@tailrec
def contar(m: Mensaje, acc: Map[Char, Int]): Map[Char, Int] = {
  if (m.isEmpty) acc
  else {
    val c = m.head
    val nuevaAcc = if (esMinuscula(c)) acc + (c -> (acc.getOrElse(c, 0) + 1)) else acc
    contar(m.tail, nuevaAcc)
  }
}
val freqMap = contar(m, Map.empty)
freqMap.toList.sortBy { case (c, count) => (-count, c) }
```

- `esMinuscula(c)` corresponde a $c \in \Sigma$.
- `acc.getOrElse(c, 0)` corresponde a $acc(c)$, con valor $0$ si la letra no está registrada.
- `Map.empty` es el acumulador inicial, con $acc(c) = 0$ para todo $c$.

### Lema (invariante del acumulador)

Para toda cadena $m$ y todo acumulador $acc$, $\text{contar}(m, acc)$ devuelve un mapa $acc^*$ tal que

$$
acc^*(c) = acc(c) + N(c, m) \quad \forall c \in \Sigma
\qquad \text{y} \qquad
\text{dom}(acc^*) = \text{dom}(acc) \cup \{c \in \Sigma : N(c, m) > 0\}
$$

**Demostración** por inducción sobre la longitud de $m$, con $acc$ arbitrario.

- *Base* ($m = \varepsilon$): $\text{contar}(\varepsilon, acc) = acc$ y $N(c, \varepsilon) = 0$ para todo $c$. Ambas igualdades se cumplen.
- *Paso* ($m = h \cdot t$):
  - Si $h \in \Sigma$, se llama a $\text{contar}(t, acc')$ con $acc'(h) = acc(h) + 1$, $acc'(c) = acc(c)$ para $c \neq h$, y $\text{dom}(acc') = \text{dom}(acc) \cup \{h\}$. Por H.I., el resultado $acc^*$ cumple $acc^*(c) = acc'(c) + N(c, t)$. Para $c = h$: $acc(h) + 1 + N(h, t) = acc(h) + N(h, h \cdot t)$. Para $c \neq h$: $acc(c) + N(c, t) = acc(c) + N(c, h \cdot t)$. El dominio es $\text{dom}(acc) \cup \{h\} \cup \{c : N(c, t) > 0\}$, que es el pedido porque $N(h, h \cdot t) > 0$.
  - Si $h \notin \Sigma$, el acumulador no cambia y $N(c, h \cdot t) = N(c, t)$ para todo $c \in \Sigma$. La H.I. da ambas igualdades. $\blacksquare$

Con $acc = \varnothing$, el mapa resultante asocia a cada letra su $N(c, m)$, y **solo** a las letras que aparecen. Por eso las letras con frecuencia cero no salen en el resultado.

### Orden del resultado

Las llaves de un `Map` son distintas, así que `freqMap.toList` tiene pares con letras distintas. El orden lexicográfico $(-n, c)$ sobre $\mathbb{Z} \times \Sigma$ es total, y por tanto la lista ordenada es **única** y coincide con la especificación.

### Traza: `frecuencias("casa")`

```text
contar("casa", {})
-> contar("asa", {c -> 1})
-> contar("sa",  {c -> 1, a -> 1})
-> contar("a",   {c -> 1, a -> 1, s -> 1})
-> contar("",    {c -> 1, a -> 2, s -> 1})      <- caso base: devuelve acc

toList.sortBy(-count, c)  =  List(('a',2), ('c',1), ('s',1))
```

### Terminación y recursión de cola

En cada llamado la cadena pierde un carácter, así que $|m|$ decrece y se alcanza el caso base. La llamada `contar(m.tail, nuevaAcc)` es lo último que hace la función en su rama recursiva, por lo que `@tailrec` es válido y el recorrido usa espacio constante. El ordenamiento final es una sola llamada a la biblioteca, fuera de la recursión.







## 8. Corrección de `desplazamientoProbable` y `romperCesar`

### Correspondencia con la implementación

```scala
val freq = frecuencias(m)
if (freq.isEmpty) 0
else {
  val letraMasFrecuente = freq.head._1
  val desplazamiento = letraMasFrecuente - 'e'
  if (desplazamiento < 0) desplazamiento + letras else desplazamiento
}
```

Sea $F = \text{frecuencias}(m)$ y $c^*$ la letra de la cabeza de $F$ (la más frecuente; en empate, la menor alfabéticamente). Sea $\delta = P(c^*) - P(\texttt{e})$, que es lo que calcula `letraMasFrecuente - 'e'`. Definimos

$$
d(m) =
\begin{cases}
0 & \text{si } F = [\,] \\
\delta & \text{si } F \neq [\,] \text{ y } \delta \ge 0 \\
\delta + 26 & \text{si } F \neq [\,] \text{ y } \delta < 0
\end{cases}
\qquad
\text{romperCesar}(m) = \text{cesarCola}\big(m,\, -d(m)\big)
$$

### Lema 1 (la normalización es un módulo 26)

Si $F \neq [\,]$, entonces $d(m) = \delta \bmod 26 \in [0, 25]$.

*Demostración.* Como $P(c^*), P(\texttt{e}) \in [0, 25]$, se tiene $\delta \in [-25, 25]$. Si $\delta \ge 0$, entonces $\delta \in [0, 25]$ y $d(m) = \delta = \delta \bmod 26$. Si $\delta < 0$, entonces $\delta + 26 \in [1, 25]$ y es congruente con $\delta$ módulo 26, así que $d(m) = \delta \bmod 26$. $\blacksquare$

**Verificación con los casos del enunciado:**

- $d(\texttt{"h"}) = 7 - 4 = 3$.
- $d(\texttt{"hhhaa"}) = 3$, porque $c^* = \texttt{h}$.
- $d(\texttt{"123"}) = 0$, porque $F = [\,]$.
- $d(\texttt{"hhhaaa"})$: hay empate a 3, $F = [(\texttt{a},3), (\texttt{h},3)]$, $c^* = \texttt{a}$ y $\delta = 0 - 4 = -4 < 0$, así que $d = -4 + 26 = 22$.

### Lema 2 (composición de desplazamientos)

Para toda cadena $p$ y enteros $a, b$: $\text{cesar}(\text{cesar}(p, a), b) = \text{cesar}(p, a + b)$. En particular, $\text{cesar}(\text{cesar}(p, k), -k) = \text{cesar}(p, 0) = p$.

*Demostración.* Por inducción estructural basta probar $f_{\text{shift}}(f_{\text{shift}}(c, a), b) = f_{\text{shift}}(c, a + b)$. Si $c \notin \Sigma$, ambos lados dan $c$. Si $c \in \Sigma$ con $P(c) = x$, el lado izquierdo da la posición $\big((x + a) \bmod 26 + b\big) \bmod 26 = (x + a + b) \bmod 26$, que es la del lado derecho. Para $b = -a$ se obtiene $x \bmod 26 = x$. $\blacksquare$

Además, como `cesarCola` y `cesar` son equivalentes (Sección 3), $\text{romperCesar}(m) = \text{cesar}(m, -d(m))$.

### Teorema (condición exacta de éxito)

Sea $p$ un mensaje con al menos una letra y $m = \text{cesar}(p, k)$. Entonces

$$
\text{romperCesar}(m) = p \iff c^* = f_{\text{shift}}(\texttt{e}, k)
$$

es decir, el método acierta si y solo si la letra ganadora del texto cifrado, con el desempate alfabético de `frecuencias`, es la imagen de `e` bajo el corrimiento real.

*Demostración.* Como $p$ tiene letras, $m$ también y $F \neq [\,]$. Por el Lema 1, $d(m) = (P(c^*) - 4) \bmod 26$, así que

$$
d(m) \equiv k \pmod{26} \iff P(c^*) \equiv 4 + k \pmod{26} \iff c^* = f_{\text{shift}}(\texttt{e}, k)
$$

- ($\Leftarrow$) Si $d(m) \equiv k$, entonces $\text{romperCesar}(m) = \text{cesar}(\text{cesar}(p, k), -d(m)) = \text{cesar}(p, k - d(m)) = \text{cesar}(p, 0) = p$, por el Lema 2 y porque $f_{\text{shift}}$ solo depende del desplazamiento módulo 26.
- ($\Rightarrow$) Si $d(m) \not\equiv k$, el Lema 2 da $\text{romperCesar}(m) = \text{cesar}(p, k - d(m))$ con $(k - d(m)) \bmod 26 \neq 0$. Como $p$ tiene al menos una letra, esa letra cambia de posición, y por tanto $\text{romperCesar}(m) \neq p$. $\blacksquare$

Si el mensaje no tiene letras, $d(m) = 0$ y $\text{romperCesar}(m) = \text{cesarCola}(m, 0) = m$, que es correcto trivialmente.

### Relación con el contraejemplo de la Sección 5

El teorema explica el fallo de la Sección 5. El método falla exactamente cuando la letra ganadora del cifrado no es la imagen de `e`. Eso ocurre por dos razones:

1. La `e` no es la más frecuente del texto plano.
2. Hay un empate, y el orden alfabético, que **no se conserva** al correr las letras, elige otra letra.

En el contraejemplo, $p = \texttt{"papa"}$ y $k = 5$, así que $f_{\text{shift}}(\texttt{e}, 5) = \texttt{j}$. Pero $m = \texttt{"ufuf"}$ tiene empate entre `f` y `u`, y gana $c^* = \texttt{f} \neq \texttt{j}$. Por el teorema, el método falla, y de hecho $d = 1 \neq 5$ y $\text{romperCesar}(m) = \texttt{"tete"} \neq \texttt{"papa"}$.