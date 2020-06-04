--liquibase formatted sql
--changeset andrew:2020-05-31
CREATE TABLE "Player" (
	"Pk"	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,
	"Name"	TEXT NOT NULL UNIQUE
);