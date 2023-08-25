JAVAC=/usr/bin/javac
JAVA=/usr/bin/java
.SUFFIXES: .java .class
SRCDIR=src/clubSimulation
BINDIR=bin

$(BINDIR)/%.class:$(SRCDIR)/%.java
	$(JAVAC) -d $(BINDIR)/ -cp $(BINDIR) $<

CLASSES= ClubGrid.class / Clubgoer.class / ClubView.class / CounterDisplay.class  / GridBlock.class / PeopleCounter.class / PeopleLocation.class / ClubSimulation.class / 


CLASS_FILES=$(CLASSES:%.class=$(BINDIR)/%.class)

default: $(CLASS_FILES)

clean:
	rm $(BINDIR)/clubSimulation/*.class

run: $(CLASS_FILES)
	$(JAVA) -cp bin clubSimulation.ClubSimulation 150 25 25 50
# CLA = total people to enter | no gridx cells | no grid y cells | max people