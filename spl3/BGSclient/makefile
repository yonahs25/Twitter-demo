CFLAGS:=-c -Wall -Weffc++ -g -std=c++11 -Iinclude
LDFLAGS:=-lboost_system

all: BGRSclient
	g++ -o bin/BGRSclient bin/connectionHandler.o bin/BGSclient.o $(LDFLAGS)

bin/connectionHandler.o: src/connectionHandler.cpp
	g++ $(CFLAGS) -o bin/connectionHandler.o src/connectionHandler.cpp

bin/BGSclient.o: src/BGSclient.cpp
	g++ $(CFLAGS) -o bin/BGSclient.o src/BGSclient.cpp


.PHONY: clean
clean:
	rm -f bin/*
