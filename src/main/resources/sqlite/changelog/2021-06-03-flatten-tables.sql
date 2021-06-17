--liquibase formatted sql
--changeset andrew:2021-06-03
CREATE TABLE "Play" (
	"Pk"	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,
	"GameName"	TEXT NOT NULL,
    "SessionId" TEXT NOT NULL,
	"TeamRank"	INTEGER NOT NULL,
	"PlayDate"	INTEGER NOT NULL,
	"PlayerName"	TEXT NOT NULL,
	"PlayerTeam"	INTEGER NOT NULL,
	"PlayerMean"	REAL NOT NULL,
	"PlayerStdDev"	REAL NOT NULL
);

INSERT INTO Play ("GameName","SessionId","TeamRank","PlayDate","PlayerName","PlayerTeam","PlayerMean","PlayerStdDev")
SELECT g.name, m.SessionId, m.Rank, m.PlayDate, p.name, m.Team, m.Mean, m.StandardDeviation FROM Game g
INNER JOIN Match m ON g.Pk = m.GameFk
INNER JOIN Player p ON p.Pk = m.PlayerFk;

DROP TABLE Match;
DROP TABLE Game;
DROP TABLE Player;