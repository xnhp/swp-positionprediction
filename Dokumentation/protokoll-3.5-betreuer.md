KK ^= Karsten Klein

MA ^= Michael Aichem

MB ^= Moritz Bock



# noch offen

* Designentscheidungen, Fragen, Absprachen, Annahmen; deren Auswirkungen in einem Dokument festhalten sodass nachvollziehbar. Außerdem weiterführende Ideen.
* Protokollanten für das nächste Meeting festlegen

## SRS


* (KK) "Sehr gutes technisches Wissen" nicht voraussetzbar, Formulierung runterschrauben; stattdessehen eher: keine Vereinfachungen nätig, können UI verstehen -> konkreter werden; nur voraussetzen, was auch wirklich von uns gebraucht/gefordert wird.

* Szenario: Update-Funktion in Erzählung  integrieren, nicht davon ausgehen, dass Vogel tatsächlich angetroffen wird. (KK)

* (R5) Umformulieren, statt "gegeben Vogel" eher "gegeben StudyID & VogelID" (KK)
* (R5) Umformulieren, "jd Punkt in Umgebung" ist Festlegung auf Heatmap, billiges Vorhersagemodell könnte auch nur einen einzelnen Punkt zurückgeben -> Formulierung abschwächen (KK)

* (MA) nur maximal kleine Mengen von Vergangenheits-Daten anzeigen

* (MA) *WIR müssen uns überlegen*: Was ist vernünftige Einschränkung aus *unserer* Sicht? Harte Grenze muss von Betreuung abgeklärt werden, weiche Grenze kann von uns festgelegt werden und soll ins SRS geschrieben werden.

* (MB) Lieber gmaps oder openstreetmap als Mapbox verwenden (cf osmdroid)

* (MA) In SRS nirgends auf Mapbox festlegen, allgemein formulieren ("Karten-Provider" oder so)

* (KK) (R25) explizit reinschreiben, was wir uns davon eigentlich wünschen, Darstellung/response-time *verhältnismäßig* zu Rechenaufwand; MA "modulo Rechenzeit, ..." --- Performance der Hardware und Komplexität der Algorithmen sollte bei den Wartezeiten erwähnt werden

* (R19) expliziter formulieren: IDs liefern Zugriff auf Daten

* (O1) ändern: Virtual-Reality-"Umgebung" (MA)

* (O2) ändern; Beispiele hinzufügen: Wetter, Temperatur, ..., (O8, O7) (MA)

* (KK) Es wird Cesium-Server von Uni bereitgestellt; wir übergeben dann Daten, "Einbindung" uns überlassen; entweder WebView oder externes öffnen in Browser

* (MS1) Wort "Mapbox" ersetzen

* (BM) Bei Definitionen unterscheiden zwischen Cesium und (2D-)Kartenprovider

* Punkt mit Cesium nicht in (MS1)

* Zeitplan mit Wochentakt aufstellen (soll Testen beinhalten)

* Testen zu MS3 hinzufügen



## Erledigt

* Szenario: "X" einen echten Namen geben. (MA)
* (R8) Satzbau
* Def. von "Forscher" ändern (MA)
* (KK) Nutzercharakterisierung, Szenario: Nicht beschränken auf Mitgliedschaft beim MPI. Eher: "Forscher, die Verhalten von Vögeln untersuchten".
* (MA) Hinzufügen zu Abschnitt "Definitonen": Movebank, Cesium [BM: Movebank habe ich nun nicht erwähnt, dass das ja generell im SRS nicht mehr erwähnt werden wird.]
* (MA) Hinzufügen zu "Referenzen": Links zu API-Dokumentionen
* Idee in extra Dokument aufnehmen: Tracking von suchender Person (KK)
* In Protokoll von letztem Meeting schauen ob da noch Ideen stehen die man noch zentral sammeln kann (BM)
* (KK) "formal falsch" ändern "falsch formatiert"
* R20: korrekt formulieren. formal falsche Daten werden ja nicht für Berechnung verwendet.
* R24 formulierung eher: Alle Daten werden aufbereitet, auch wenn formal falsche darunter sind (KK)
