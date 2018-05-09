Protokoll Mittwoch, 9. Mai mit MPI Forscher
==============================================

F: Forscher
MA: Michael Aichem


# Todo
- Username an F schicken 
- 

# Workflow 
- Daten -> Movebank. Batterie irgendwann evtl. tot. Wo ist das Tier jetzt? Im Umkreis eines Kilometers hinkommen um mit Gerät den Sender/Vogel aufzuspüren

1. Im Büro Tier beobachten, wie verhält sich das Tier. 
2. Generiere Muster im Feld mit z.B. Karte 


# Zusatz
- Google Maps Route


# Optimierung Workflow:
- Schrittlänge (= Geschwindigkeitsmaß) (korreliert mit Turningangle)
- Turningangle
- Strukturen die sich aus der Karte rauslesen (Wasser ausschließen, wenn Vogel nie im Wasser)
- Vögel ändern ihre Richtung bei hoher Geschwindigkeit eher nicht


# Fragen: 
- Wetter? => Ja z.B. Rückenwind erhöht Geschwindigkeit
- Gibt es Explorationsalgorithmen oder selbst kreativ werden? => In R geschriebene Algorithmen sind vorhanden. Kegel kann berechnet werden mit vorhandenem Algorithmus. Wahrscheinlichkeit wird aus Schrittlänge und Turningangle berechnet. 
- Was kann man vorhersehen, was ist Rauschen? => Erwartung kann angegeben werden, je nach Tier verschieden. User Feedback geben, wie gut der Algorithmus bei einem Vogel funktioniert. 
- Wie geht der Forscher manuell vor? => 90% der Fälle wird das Material gefunden. Confidence miteinbeziehen für den User (Wenn dieser weiß, dass Vogel z.B. gestorben ist bzw. Algorithmus erkennt, dass seltsame Bewegungen vorkommen, dann anzeigen, dass Algorithmus eventuell ungewöhnliche Daten benutzt)
- Ein Vogel oder mehrere Vögel? => Protokoll gibt vor welche Vögel mal wieder überprüft werden sollen. Wenn in anderem Feld evtl. viele Vögel sein werden, informiere Forscher darüber, dass er sein Plan eventuell ändert. 
- Welche Vögel hier in der Gegend? => Storch-Datensatz ist groß (auch live-Feeds verfügbar)
- Vogel mit ID suchen oder welche Voraussetzung hat der Forscher? => Suche auf der Karte nicht erwünschenswert. (Lieber Studien favorisieren und) nach letzter betrachteten Studien sortieren. 
- Werden Daten gepuffert? => Ja, da teuer in manchen Ländern werden nur ein gewisser Anteil der Daten geschickt. Aber mehrere Daten sind eigentlich noch vorhanden. 
- Zusätzliche Attribute? =>
    - Instantanois Speed = Groundspeed. Berechnet durch Doppler-Verschiebung. 
    - Height above ellipsoid = Höhe über Erde (ungefähr)
- Höhe relevant? => Nein
- Verfügung von mobilen Daten im Feld? => Theoretisch hat man kurz bis vor Ort Empfang -> Beide Möglichkeiten.
- Es gibt einen Algo der Schrittlänge und Angle benutzt, dürfen wir den verwenden? => Ja


# Vorschläge Studenten zur API:
- ":" in Passwort macht Probleme
- License Terms
- Dokumentation
- 'isLiveFeed' Attribut in Movebank














