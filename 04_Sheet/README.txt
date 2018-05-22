

Das Programm kann für die verschiedenen Aufgaben in 3 Modi ausgeführt werden. Dieser wird mit dem ersten übergebenen Arugument bestimmt. ( folds, cv, weka )

Um die Folds zu generieren :
java -jar Tasks.jar <Modus> <Source arff file> <Class index> <Output dir> <num folds>
Beispiel : 
java -jar Tasks.jar folds C:\Users\ken\uni\08_UNI_SS_18\ML\data\weather.nominal.arff 4 C:\Users\ken\uni\08_UNI_SS_18\ML\04_Sheet\out 10

Um die corss validation auszuführen:
java -jar Tasks.jar <Modus> <Source arff file> <classAttribute> <max tree depth> <num folds>
Beispiel:
java -jar Tasks.jar cv C:\Users\ken\uni\08_UNI_SS_18\ML\data\car.arff 6 6 10


Um den weka part auszuführen:
java -jar Tasks.jar <Modus> <Directory of folds> <relation name (file name)> <Source arff file> <Num folds> <Class index>
Beispiel:
java -jar Tasks.jar weka C:\Users\ken\uni\08_UNI_SS_18\ML\04_Sheet\out\car car C:\Users\ken\uni\08_UNI_SS_18\ML\data\car.arff 10 6

