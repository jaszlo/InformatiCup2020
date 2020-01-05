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

## Spielen über die grafische Oberfläche

Nach dem starten der grafischen Oberfläche muss noch das Kommandozeilentool des InformatiCup auszuführen. Dabei ist es empfehlenswert die Antwortzeit auf "unbegrenz" zu setzten, da man selber
leicht die zehn Sekunden überschreitet. Dies sieht unter Linux folgendermaßen aus.
```sh
$ ./ic20_linux -t 0
```
In der grafischen Oberfläche gibt ein blauer Ausgabetext gibt Informationen über den aktuellen Zustand an. Nach dem Starten des Kommandozeilentool, so sollte der Text ausgeben, dass ein Spiel gefunden wurde.

Nun kann man durch den 'Auto turn' Knopf einen Zug von der Heuristik ausführen lassen. Optional lässt sich im Textfeld 'amount' eine gewisse Anzahl von automatischen Zügen angeben.
Ebenso lassen sich manuell Aktionen spielen. Der Ausgabetext gibt an, falls Informationen zum Ausführen einer Aktion fehlen, oder ob diese ausgeführt wurde.
Städte und Pathogene lassen sich per "ChoiceBox" auswählen, während die Anzahl der Runden einer Aktion im dazugehörigen Textfeld angegeben werden.

Will man nur die grafische Oberfläche beenden geht dies über das Kreuz des Fensters. Will man dazu noch den Server schließen muss der 'Quit' Knopf benutzt werden.

## Testen

Da man durch manuelles Spielen nur sehr langsam Eindruck von der Effizienz der Heuristik bekommt gibt es das Testskript.
Zum effizienteren Testen der Heurisitk sollte man jedoch das Testskript benutzen. Dieses bietet verschiedene Möglichkeiten mehrere Spiele parallel spielen zu lassen.
Durch den Aufruf
```
$ python3 Test.py
```
werden die ersten 100 Seeds gespielt, wovon jeweils vier parallel ausgeführt werden. Nach diesem Aufruf sollte sich die GUI schließen. Relevante Informationen zum aktuellen Fortschritt werden auf der Konsole ausgegeben.
Erneut finden sich weitere und genauere Informationen in der Dokumentation in Kapitel 4.3 "Benutzung des Testskripts" .


## Beenden des Webservices als Hintergrundprozess

Spielt man mehrere Spiele zeitgleich, so wird die GUI geschlossen, da diese nicht weiter benötigt wird. Will man nun den Prozess des Webservices beenden, so kann man dies per beliebigen Request, der nicht vom Typ POST ist, machen. 

Diesen kann man z.B. durch das Aufrufen der URL "localhost:50123" in seinem Browser erzeugen.


## Lizenz

Diese Projekt unterliegt der [MIT Lizenz](./LICENSE).

