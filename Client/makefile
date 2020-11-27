CFLAGS:=-c -Wall -Weffc++ -g -std=c++11 -Iinclude
LDFLAGS:=-lboost_system -lpthread

# All Targets
all: BGSclient

# Tool invocations
# Executable "BGSclient" depends on the files BGSclient and run.o.
	g++ -o bin/BGSclient bin/connectionHandler.o bin/ReadFromKeyboard.o bin/ReadFromSocket.o bin/Client.o $(LDFLAGS)

BGSclient: bin/connectionHandler.o bin/ReadFromKeyboard.o bin/ReadFromSocket.o bin/Client.o

# Depends on the source and header files
bin/connectionHandler.o: src/connectionHandler.cpp
	g++ $(CFLAGS) -o bin/connectionHandler.o src/connectionHandler.cpp

bin/ReadFromKeyboard.o: src/ReadFromKeyboard.cpp
	g++ $(CFLAGS) -o bin/ReadFromKeyboard.o src/ReadFromKeyboard.cpp

bin/ReadFromSocket.o: src/ReadFromSocket.cpp
	g++ $(CFLAGS) -o bin/ReadFromSocket.o src/ReadFromSocket.cpp

bin/Client.o: src/Client.cpp
	g++ $(CFLAGS) -o bin/Client.o src/Client.cpp
.PHONY: clean
clean:
	rm -f bin/*
