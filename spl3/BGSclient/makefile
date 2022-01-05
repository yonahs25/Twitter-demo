CFLAGS:=-c -Wall -Weffc++ -g -std=c++11 -Iinclude
LDFLAGS:=-lboost_system -pthread

all: bin/BGSclient

bin/BGSclient: bin/BGSclient.o bin/connectionHandler.o bin/writeTask.o bin/readTask.o
	g++ -o bin/BGSclient bin/BGSclient.o bin/connectionHandler.o bin/writeTask.o bin/readTask.o $(LDFLAGS)

bin/BGSclient.o: src/BGSclient.cpp
	g++ $(CFLAGS) -o bin/BGSclient.o src/BGSclient.cpp

bin/connectionHandler.o: src/connectionHandler.cpp
	g++ $(CFLAGS) -o bin/connectionHandler.o src/connectionHandler.cpp

bin/writeTask.o: src/writeTask.cpp
	g++ $(CFLAGS) -o bin/writeTask.o src/writeTask.cpp

bin/readTask.o: src/readTask.cpp
	g++ $(CFLAGS) -o bin/readTask.o src/readTask.cpp

.PHONY: clean
clean:
	rm -f bin/*
