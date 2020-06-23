--liquibase formatted sql
--changeset andrew:2020-05-31
CREATE TABLE "Match" (
    "Pk"	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,
    "SessionId" TEXT NOT NULL,
	"GameFk"	INTEGER NOT NULL,
	"PlayerFk"	INTEGER NOT NULL,
	"Team"	INTEGER NOT NULL,
	"Rank"	INTEGER NOT NULL,
	"PlayDate"	INTEGER NOT NULL,
	"Mean"	REAL NOT NULL,
	"StandardDeviation"	REAL NOT NULL,
	FOREIGN KEY("GameFk") REFERENCES "Game"("Pk"),
	FOREIGN KEY("PlayerFk") REFERENCES "Player"("Pk")
);