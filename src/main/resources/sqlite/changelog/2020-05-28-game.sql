--liquibase formatted sql
--changeset andrew:2020-05-28
CREATE TABLE "Game" (
	"Pk"	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,
	"Name"	TEXT NOT NULL UNIQUE,
	"DrawProbability"	REAL NOT NULL
);