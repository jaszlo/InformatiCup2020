# Lösung für den InformatiCup 2020

Dies ist ein lokaler Webservice, welcher die vom Kommandotool erstellen Spielzustände bearbeiten kann und einen möglichst guten Zug ausführt. Mit enthalten ist eine grafische Oberfläche, welche zusätzlichemanuelles Spielen ermöglicht, sowie ein Skript zum Testen der von uns erstellen Lösung.

Genauere Informationen zur Benutzung der grafischen Oberfläche, sowie des Testskripts finden sich in der Dokumentation (Kapitel 4.2 und 4.3).

## Systemvoraussetzungen

Damit der Webservice installiert und ausgeführt werden kann benötigt es die Oracle Java Version 8 und Maven.
Für das Testskript wird zusätzlich Python 3.6 benötigt.

## Installation

Um das Projekt zu bauen und auszuführen benötigt es nur einen Systemaufruf

```sh
$ mvn package
```
War dies erfolgreich, so sollte der Webservice gestartet werden und die grafische Oberfläche erscheint.

## Benutzung

Ein blauer Ausgabetext gibt dabei Informationen über den aktuellen Zustand an. Startet man an dieser Stelle den \gls{GI Client}, so sollte der Text ausgeben, dass ein Spiel gefunden wurde.

Nun kann man durch den 'Auto turn' Knopf einen Zug von der Heuristik ausführen lassen. Optional lässt sich im Textfeld 'amount' eine gewisse Anzahl von automatischen Zügen angeben.

Zum effizienteren Testen der Heurisitk sollte man jedoch das Testskript benutzen. Dieses bietet verschiedene Möglichkeiten mehrere Spiele parallel spielen zu lassen.
Durch den einfachen Aufruf

```
$ python3 Test.py
```

werden die ersten 100 Seeds gespielt, wovon jeweils vier parallel ausgeführt werden. Nach diesem Aufruf sollte sich die GUI schließen. Relevante Informationen zum aktuellen Fortschritt werden auf der Konsole ausgegeben.

Erneut finden sich weitere und genauere Informationen in der Dokumentation in Kapitel 4.3 "Benutzung des Testskripts" 

## Beenden des Webservices als Hintergrundprozess

Spielt man mehrere Spiele zeitgleich, so wird die GUI geschlossen, da diese nicht weiter benötigt wird. Will man nun den Prozess des Webservices beenden, so kann man dies per POST-Request machen. 

Diesen kann man z.B. durch das Aufrufen der URL "localhost:50123" in seinem Browser erzeugen.



