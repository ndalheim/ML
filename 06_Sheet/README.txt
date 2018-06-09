
Die Abgabe enthält 2 JAR Datein. 
MetaParamOptimizer.jar Task 1
MLR.jar Task 3

Zum Ausführen von MLR
java -jar [...]/MLR.jar [...]/task3.csv

Zum Ausführen von von MetaParamOptimizer:

!! 1 Argument bestimmt den Modus !!

Task 1a)
Es können am Ende beliebig viele Files angegeben werden, mit denen dann ein Plot gebaut wird. Es sollten jedoch maximal 3 sein. Es wurden hier nur diese zwei Datensätze hinzugefügt, da der letzte eine sehr lange Laufzeit hat (14h).
Die Zahl nach dem Datensatz ist der index des class attributes.
java -jar [...]/MetaParamOptimizer.jar J48Weka [...]/car.arff 6 [...]/diabetes.arff 8

Task 1c)

java -jar [...]/MetaParamOptimizer.jar optimalJ48 <path to arff file> <class index> <range>

Beispiel:
java -jar [...]/MetaParamOptimizer.jar optimalJ48 C:\Users\ken\uni\08_UNI_SS_18\ML\data\car.arff 6 0.05:0.05:0.5
