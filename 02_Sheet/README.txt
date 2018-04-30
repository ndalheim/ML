Um den Algorithmus zu testen kann das bereitgestellte JAR verwendet werden.
JAR : DecisionTree.jar

Verwende zum Starten: (Wichtig : Es müssen alle Werte angegeben werden !)
java -jar [...]/DecisionTree.jar <Path to arff file> <Index of class attribute> <maximal tree depth> <iterations>

Um die maximale tiefe auf Integer.Max_Value zu setzen kann -1 verwendet werden.

Beisiel:
java -jar .\DecisionTree.jar C:\Users\ken\uni\08_UNI_SS_18\ML\02_Sheet\car.arff 6 -1 10
