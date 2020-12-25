# Bunte Panzer!

Bunte Panzer ist ein Java/LWJGL clone des Online Spiels [AZTanks](https://www.agame.com/game/az). Ein kurzes (45s) Video vom Gameplay kann [HIER](https://www.youtube.com/watch?v=yb6AUVgGRg0) angeschaut werden. Im **jars**-Ordner stehen JAR-Dateien zur verfügung, um das Spiel selbst mal auszuprobieren, jedoch konnte ich die MacOS Version noch nicht testen.

Wie schon erwähnt ist Bunte Panzer ein Clone vom dem Online Browser Spiel "AZ Tanks", welches ich in der Schule mit Klassenkameraden immer gespielt habe. 


Man steuert bunte Panzer aus der Vogelperspektive in einer Arena und versucht seine Mitspieler abzuschießen. Zusätzlich kann man noch stärkere Waffen aufsammeln.


Das Spielprinziep ist ziemlich simpel und deswegen perfekt für mein erstes Spiel, was ich fertig gestellt habe. Zusätzlich ist das ein "X Player 1 Computer" Spiel, das heißt ein großteil des Spielspaßes kommt davon, mit seinen Mitspielern Wettzueifern und zu lachen. 


## Building and Running

Die Hauptklasse ist **steamTanks/mainGame/BuntePanzer.java**.



### [LWJGL] Failed to load a library:

```
[LWJGL] Failed to load a library. Possible solutions:
	a) Add the directory that contains the shared library to -Djava.library.path or -Dorg.lwjgl.librarypath.
	b) Add the JAR that contains the shared library to the classpath.
[LWJGL] Enable debug mode with -Dorg.lwjgl.util.Debug=true for better diagnostics.
[LWJGL] Enable the SharedLibraryLoader debug mode with -Dorg.lwjgl.util.DebugLoader=true for better diagnostics.
Exception in thread "main" java.lang.UnsatisfiedLinkError: Failed to locate library: liblwjgl.so
	at org.lwjgl.system.Library.loadSystem(Library.java:162)
	at org.lwjgl.system.Library.loadSystem(Library.java:62)
	at org.lwjgl.system.Library.<clinit>(Library.java:50)
	at org.lwjgl.system.MemoryUtil.<clinit>(MemoryUtil.java:97)
	at org.lwjgl.system.Pointer$Default.<clinit>(Pointer.java:67)
	at org.lwjgl.system.Callback.<clinit>(Callback.java:41)
	at graphics.context.Display.create(Display.java:62)
	at graphics.context.Display.<init>(Display.java:56)
	at steamTanks.mainGame.BuntePanzer.<init>(BuntePanzer.java:48)
	at steamTanks.mainGame.BuntePanzer.main(BuntePanzer.java:42)

Process finished with exit code 1

```

Sollte ein Fehler dieser Art auftreten, dann könnte die Lösung dafür sein, bei den Maven-Profiles das Profile mit den Natives für sein System auszuwählen und Maven zu aktualisieren.

## Features:

### LWJGL (Lightweight Java Game Library)
[LWJGL](https://www.lwjgl.org/) ist ein Sammelbegriff für eine Reihe Biblotheken, die die Spieleentwicklung vereinfachen sollen. Man kann sie über Maven, Gradle, Ivy oder als Custom-Zip ins Project einbinden. Die Biblotheken stellen über das JNI-Interface Methoden zur verfügung, um:
- Fenster mit GLFW zu erstellen und Eingaben zu lesen
- Hardwarebeschleunigtes Rendern mit OpenGL zu nutzen
- Töne mit OpenAL abzuspielen
- Bilder mit STB zu laden und
- GUI mit [Nuklear](https://github.com/Immediate-Mode-UI/Nuklear) zu realisieren.

Außerdem ist mit [JOML](https://github.com/JOML-CI/JOML) (Java OpenGL Math Library) eine Mathe Biblothek enthalten, mit der man Vector, Matrix und Quarternionen verrechnen kann. Was jedoch fehlt ist eine Physik-Biblothek, weswegen ich Kollisionen selbst implementieren musste.

### Kollisionsabfrage:
Die Kollisionsabfrage wird mit dem [SAT](https://gamedevelopment.tutsplus.com/tutorials/collision-detection-using-the-separating-axis-theorem--gamedev-169) (Separating Axes Theorem) -Algorithmus realisiert. Kurz zusammengefasst kann man sagen, dass zwei konvexe Polygone nicht kollidieren, wenn man eine Linie zwischen ihnen zeichnen kann.


Um festzustellen, ob das möglich ist, projeziert man alle Eckpunkt der beiden Testkörper auf alle Normalenachsen und überprüft, ob sich diese Projektionen überlappen. Wenn das bei allen Achsen der Fall ist, dann überschneiden sich die beiden Polygone.


Man kann sie trennen, indem man sich die Achse mit der kleinste Überlappung sucht und dann entlang der Normalen die Körper trennt.

![Bild: Verschiedene Fälle](images/SAT.jpg)


### OpenGL und OpenAL
Der eigentliche Grund warum ich überhaupt LWJGL benutze ist OpenAL, weil es mit dieser API kinderleicht ist die Soundkarte  zu nutzen um Töne abzuspielen. Auf die Idee bin ich gekommen, weil es ziemlich schwierig ist unter Java Artefaktfrei und in Echtzeit Soundeffekte ertönen zu lassen. 


Bis zu diesem Zeitpunkt hatte ich alle Grafiken, Animationen und Partikelleffekte mit Swing und Java2D gerendert, aber da ich nun die LWJGL-Biblothek einbinden musste, konnte ich auch alle Features nutzen: Unter anderem auch hardwarebeschleunigtes Rendern mit OpenGL, was ein kompletten Rewrite der Renderpipline mit sich zog, da ich jetzt Shader, FBOs, und VAOs nutzen musste, weil sie Teil von Modern OpenGL sind. Für dieses sehr simple Spiel ist das zwar abseluter overkill, aber ich hab OpenGL besser kennengelernt.


Der Aufwand lohnt sich aber: mein Spiel läuft jetzt mit 250+ FPS und ich kann einfach Töne abspielen.

### Procedurale Spielkarten Generation
Bei jedem Neustart wird die Spielfläche neu generiert, die KI-Panzer und die Spieler in dem Labyrinth plaziert und mit fortlaufender Zeit werden Waffen-Upgrades gespawnt.


Um das Labyrinth zu generieren benutze ich den "[Randomized depth-first search](https://en.wikipedia.org/wiki/Maze_generation_algorithm)"  ( auch "recursive backtracker" ) Algorithmus. Er basiert auf einen Schachfeld und darauf Wände zwischen den einzelnen Zellen zu entfernen.


Hier ist eine Animation, um zu [visualisieren](https://upload.wikimedia.org/wikipedia/commons/7/7d/Depth-First_Search_Animation.ogv), wie er funktioniert.
Er funktioniert wie folgt (von Wikipedia kopiert):
-   Given a current cell as a parameter,
-   Mark the current cell as visited
-   While the current cell has any unvisited neighbour cells
    1.  Choose one of the unvisited neighbours
    2.  Remove the wall between the current cell and the chosen cell
    3.  Invoke the routine recursively for a chosen cel
- if you reached a dead end jump to a remaining unvisited cell
- if there are no more unvisited cells you are done

Der Algorithmus hat die praktische Eigenschaft, dass er ein perfektes Labyrinth erzeugt, was heißt, dass man von jedem Punkt der Karte zu jedem anderen Punkt gelangen kann.


Um die Karte zusätzlich interessanter zu gestalten, benutze ich einen [Randomwalker](https://en.wikipedia.org/wiki/Random_walk).
