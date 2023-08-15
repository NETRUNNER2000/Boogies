JAVAC=/usr/bin/javac
JAVA=/usr/bin/java
.SUFFIXES: .java .class
SRCDIR=src/clubSimulation
BINDIR=bin

$(BINDIR)/%.class:$(SRCDIR)/%.java
	$(JAVAC) -d $(BINDIR)/ -cp $(BINDIR) $<

CLASSES= PeopleCounter.class / GridBlock.class / PeopleLocation.class / CounterDisplay.class / ClubGrid.class / Clubgoer.class / ClubView.class / ClubSimulation.class / 


CLASS_FILES=$(CLASSES:%.class=$(BINDIR)/%.class)

default: $(CLASS_FILES)

clean:
	rm $(BINDIR)/clubSimulation/*.class

run: $(CLASS_FILES)
	$(JAVA) -cp bin clubSimulation.ClubSimulation 