(meeting mit Betreuer)

=== Ereignisse ==

1.) Bericht, was in der letzten Woche gemacht wurde,
	zusammengefasst von BM, detailliert von jd. Mitglied einzeln

2.) Fragen von MA wie es uns in der Gruppe geht, wie wir mit unseren
	Aufgaben zeitlich hingekommen sind.


== Ergebnisse ==

KK: Ja, es wird von Uni ein Cesium-Server zur Integration via WebView
	zur Verfügung gestellt.

MA: Empfehlung: Cesium-Dummy einbinden, zB irgendeine online-demo holen

KK: Abschnitt 2.2 unklar, anpassen (TODO)
	Sind die Daten teil des ergebnisses der Vorhersageberechung? Was sind die benötigten Daten? Formulierungen anpassen.

KK: Diverse Rechtschreibfehler im Dokument, korrigieren (DONE)

KK: Verhalten Outdoor-Modus, Datentransfer präzisieren (was passiert wann,
	in welchem Zustand, wann habe ich welche Möglichkeiten?) (TODO)

KK: "IDs" definieren (DONE)
	(bezieht sich auf Movebank - VogelID, StudyID)

KK: Abschnitt 4.5 konkreter. ZB anstatt "Falls ID ungültig"
 	eher schreiben "Prüfe auf Rückgabewert soundso, tue diesunddas"
 	IDs gültig =/=> Daten werden geladen (kann sonst noch was nicht stimmen)
	Welche Probleme können bei Kommunikation mit API sonst noch auftreten?
	Spezifizieren wie die Gültigkeit der IDs überprüft wird
	(TODO)

KK: Gibt es besondere Abläufe/Vorgänge bei App-Installation od. Erstausführung?
	(TODO)

KK: Formulierung "formal falsch" präzisieren (überall). 
	Es geht eher um einen hohen Anteil von fehlenden oder unbrauchbaren
	Datensätzen

Allg.: Falsche \ref bei SRS/Zeitplan/Woche3


=== Ideen ===

KK: Dynamisch feststellen, welche Map / welche Zoomstufen am Besten 
	heruntergeladen werden

KK: Ohne Netzwerkverbindung aber noch Tracking-Daten lokal vorhanden - 
	Anfrage nach Vorhersage trotzdem möglich?


=== Entscheidungen ===

Beschluss: Suchen uns einen festen wöchentlichen Termin für Meetings

Beschluss: Keine PDFs ins repo pushen, bei Abgaben vor Deadline PDF
	manuell erzeugen und in separatem Ordner einchecken
	(Begründung: Beim Meeting war zunächst eine alte PDF im gleichen
	 Verzeichnis wie eine aktuelle tex-Datei)


== Weitere Planung ===

Simon: Punkt 1 von Zeitplan Woche 2
Sebastian: Punkt 3 von Zeitplan Woche 2
Timon: Einlesen Zeichnen auf Karte, Integration von Methoden zum Zeichnen
Benny: Map-Einbindung weiterführen, Korrekturen am SDD (uU mit Hilfe von Anderen)
Oliver: Einlesen Cesium, Abklären Integration, Kommunikation mit Cesium