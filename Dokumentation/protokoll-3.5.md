KK ^= Karsten Klein

MA ^= Michael Aichem

MB ^= Moritz Bock



# noch offen

* Designentscheidungen, Fragen, Absprachen, Annahmen; deren Auswirkungen in einem Dokument festhalten sodass nachvollziehbar. Außerdem weiterführende Ideen.
* Protokollanten für das nächste Meeting festlegen



## SRS

## Done BM 4.5. (nach Seb)

* (KK) (R25) explizit reinschreiben, was wir uns davon eigentlich wünschen, Darstellung/response-time *verhältnismäßig* zu Rechenaufwand; MA "modulo Rechenzeit, ..." --- Performance der Hardware und Komplexität der Algorithmen sollte bei den Wartezeiten erwähnt werden
(Seb) Hab Modulo noch nie in einem deutschen Satz verwendet :D
(BM) Hoffe, meine Formulierung ist treffend genug.


* "(R5) Umformulieren, "jd Punkt in Umgebung" ist Festlegung auf Heatmap, billiges Vorhersagemodell könnte auch nur einen einzelnen Punkt zurückgeben -> Formulierung abschwächen (KK)"
=> Done, aber passt R5 so? => Welche Grenze wählen wir?
(BM) Passt für mich so.

* (Seb) Frage: Für Vogel in einer Study Vorhersage machen oder kann Vogel in mehreren Studys vorkommen, d.h. pro VogelID mit mehreren StudyIDs?
R5: ... Vogel (und Study??) ... (BM: Bisher wurde immer nur über Vogel innerhalb von Study gesprochen, deswgen würde ich das mal so übernehmen.)


Milestones: 
* (KK) Es wird Cesium-Server von Uni bereitgestellt; wir übergeben dann Daten, "Einbindung" uns überlassen; entweder WebView oder externes öffnen in Browser
(Seb.: muss man eh ganz umändern?!)
(BM: In der Zeitplanung haben wir Cesium-Integration jetzt nicht mehr in der ersten Woche drin (sondern in Woche 4 bei MS2). Da das außerdem auch ganz allgemein noch völlig unklar ist und damit für MS1 unrealistisch habe ich den Punkt Cesium aus MS1 entfernt -- und dafür eine entschärfte Version zu MS2 dazugenommen sodass es auch dem Zeitplan entspricht.)


## Done Sebastian 4.5. 

*Added*:

\paragraph{Offline-Karten} Ist ein 2D-Karten-Provider der Kartendaten liefert, wenn keine Internetverbindung besteht.


*Done*:

* Szenario: Update-Funktion in Erzählung  integrieren, nicht davon ausgehen, dass Vogel tatsächlich angetroffen wird. (KK)
* (KK) "Sehr gutes technisches Wissen" nicht voraussetzbar, Formulierung runterschrauben; stattdessehen eher: keine Vereinfachungen nätig, können UI verstehen -> konkreter werden; nur voraussetzen, was auch wirklich von uns gebraucht/gefordert wird.
* (R5) Umformulieren, statt "gegeben Vogel" eher "gegeben StudyID & VogelID" (KK)
* (MA) nur maximal kleine Mengen von Vergangenheits-Daten anzeigen
* (MB) Lieber gmaps oder openstreetmap als Mapbox verwenden (cf osmdroid)
* (MA) In SRS nirgends auf Mapbox festlegen, allgemein formulieren ("Karten-Provider" oder so)
* U(R19) expliziter formulieren: IDs liefern Zugriff auf Daten
* (O1) ändern: Virtual-Reality-"Umgebung" (MA)
* (O2) ändern; Beispiele hinzufügen: Wetter, Temperatur, ..., (O8, O7) (MA)
* Testen zu MS3 hinzufügen
* (MS1) Wort "Mapbox" ersetzen
* (BM) Bei Definitionen unterscheiden zwischen Cesium und (2D-)Kartenprovider
* Punkt mit Cesium nicht in (MS1)
* Zeitplan mit Wochentakt aufstellen (soll Testen beinhalten)


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