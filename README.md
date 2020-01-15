# Lösung für den InformatiCup 2020

Dies ist ein lokaler Webservice, welcher die vom 

zeilentool des InformatiCups erstellten Spielzustände bearbeiten kann und einen möglichst guten Zug ausführt. Mit enthalten ist eine grafische Oberfläche, welche zusätzliches manuelles Spielen ermöglicht, sowie ein Skript zum Testen der von uns erstellten Lösung.

Genauere Informationen zur Benutzung der grafischen Oberfläche, sowie des Testskripts finden sich in der [Dokumentation](./documentation/documentation.pdf) (Kapitel 4.2 und 4.3).
  
Außerdem lässt sich das Projekt bei Amazon Web Services deployen und es existiert eine deployte Version. Mehr Informationen, um das Projekt selber zu deployen finden sich in der [Dokumentation](./documentation/documentation.pdf) (Kapitel 3.1).
Um den deployten Webservice mit dem Kommandozeilentool aufzurufen, fügt man die URL "https://udi8pt9vo9.execute-api.us-east-1.amazonaws.com/default/" hinzu. Ein Aufruf unter Linux sieht z.B. so aus
```sh
./ic20_linux -u https://udi8pt9vo9.execute-api.us-east-1.amazonaws.com/default/
```

## Systemvoraussetzungen

Damit der Webservice installiert und ausgeführt werden kann benötigt man die Oracle Java Version 8 und Maven.
Für das Testskript wird zusätzlich Python 3.6 benötigt.

## Installation

Um das Projekt mit maven zu bauen folgt der Systemaufruf

```sh
$ mvn package
```
War dies erfolgreich, so sollte der Webservice als ausführbare Jar-Datei in einem neu angelegten Ordner, mit dem Namen "target", liegen. 
Zum Starten ruft man die Jar-Datei wie folgt auf.

```sh
$ java -jar target/ic_webservice-20.jar
```  
War das Starten erfolgreich, so sollte sich eine grafische Oberfläche öffnen.

## Benutzung

Nach dem Starten der grafischen Oberfläche muss man noch das Kommandozeilentool des InformatiCup ausführen. Dabei ist es empfehlenswert die Antwortzeit auf "unbegrenzt" zu setzten, da man selber
leicht den Standardwert von zehn Sekunden überschreitet. Dies sieht unter Linux folgendermaßen aus.
```sh
$ ./ic20_linux -t 0
```
In der grafischen Oberfläche gibt ein blauer Ausgabetext Informationen über den aktuellen Zustand an. Nach dem Starten des Kommandozeilentools sollte der Text ausgeben, dass ein Spiel gefunden wurde.

Nun kann man durch den 'Auto turn' Knopf einen Zug von der Heuristik ausführen lassen. Optional lässt sich im Textfeld 'amount' eine gewisse Anzahl von automatischen Zügen angeben.
Ebenso lassen sich manuell Aktionen spielen. Der Ausgabetext gibt an, falls Informationen zum Ausführen einer Aktion fehlen, oder ob diese ausgeführt wurde.
Städte und Pathogene lassen sich per "ChoiceBox" auswählen, während die Anzahl der Runden einer Aktion im dazugehörigen Textfeld angegeben werden.

Will man nur die grafische Oberfläche beenden, geht dies über das Kreuz des Fensters. Will man dazu noch den Server schließen, muss der 'Quit' Knopf benutzt werden.

## Testen

Da man durch manuelles Spielen nur sehr langsam einen Eindruck von der Effizienz der Heuristik bekommt, gibt es das Testskript.
Dieses bietet verschiedene Möglichkeiten mehrere Spiele parallel spielen zu lassen.
Zum Ausführen muss das Kommandozeilentool des InformatiCup im selben Ordner, wie das Testskript liegen.
Durch den Aufruf
```
$ python3 Test.py
```
werden die ersten 100 Seeds gespielt, wovon jeweils vier parallel ausgeführt werden. Nach diesem Aufruf sollte sich das GUI schließen. Relevante Informationen zum aktuellen Fortschritt werden auf der Konsole ausgegeben.
Erneut finden sich weitere und genauere Informationen in der [Dokumentation](./documentation/documentation.pdf) in Kapitel 4.3 "Benutzung des Testskripts" .


## Beenden des Webservices als Hintergrundprozess

Spielt man mehrere Spiele zeitgleich, so wird die GUI geschlossen, da diese nicht weiter benötigt wird. Will man nun den Prozess des Webservices beenden, so kann man dies per beliebigen Request, der nicht vom Typ POST ist, machen. 

Diesen kann man z.B. durch das Aufrufen der URL "localhost:50123" in seinem Browser erzeugen.


## Lizenz

Diese Projekt unterliegt der [MIT Lizenz](./LICENSE).

