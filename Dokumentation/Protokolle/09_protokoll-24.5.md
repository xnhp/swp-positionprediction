Visualisierung
==============

Es sollen verschiede Arten von Visualisierungsmöglichkeiten für verschiedene Arten von Daten (je nach Ausgabe des verwendeten Vorhersagealgorithmus) bereitgestellt werden.

1. Eine Trajektorie
-------------------

Die einfachste Art von Daten besteht aus genau einer Trajektorie. Diese enthät Koordinaten der vorhergesagten Aufenthaltsorte des Vogels für einen oder mehrere Zeitschritte. Außerdem kann die Trajektorie mit einem Wert für die Ungenauigkeit der Vorhersage an jedem Zeitsschritt versehen sein. Sind diese Werte vorhanden, so werden die Daten als "Schlauch" visualisiert, andernfalls als einfache Polylinie.

**Diese Vorhersageart soll als Grundfunktionalität von uns selbst implementiert werden durch einfache Methoden (z.B. Interpolation, Regression). Für die übrigen Vorhersagearten wird eine Java-Schnittstelle implementiert, da davon ausgegangen werden kann, dass der Nutzer seine Algorithmen in einer dazu kompatiblen Art bereitstellt.**

2. Mehrere Trajektorien
-----------------------

Der Algorithmus liefert eine Menge von Trajektorien mit einer Wahrscheinlichkeit. Disjunkte Cluster dieser Trajektorien werden für verschiedene Wahrscheinlichkeitsniveaus als Polygon visualisiert. Die Zeitschritte, die dafür berücksichtigt werden können vom Nutzer spezifiziert werden. Die obere Grenze ist gegeben durch die artspezifische Geschwindigleit des Vogels.

3. Punktewolken
---------------

Der Algorithmus liefert eine Menge von Punkten für jeden Zeitschritt. Diese Punkte selbst oder ihre konvexe Hülle werden als Polygon visualisiert.



TODO
====
Dokumentation:
o In SRS allgemeine Beschreibung von Outdoor/Normal-Modus hinzufügen.
  (Kann man zB einfach bei "1.4 Definitionen" dazuschreiben).
o In SRS konrete Anforderungen formulieren die dem Treffen vom 24.5. [1] entsprechen

Woche 3:
o Dummy-Visualisierung auf 2D-Karte
o Ergebnis der Vorhersage wird visualisiert
o Visualisierung von Daten aus der Vergangenheit



[1] https://git.uni-konstanz.de/kn/swp2018/group12/blob/master/Dokumentation/Protokolle/09_protokoll-24.5.md
