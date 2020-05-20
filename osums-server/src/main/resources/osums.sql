CREATE TABLE IF NOT EXISTS songs (
    id INTEGER PRIMARY KEY NOT NULL,
    rank TEXT NOT NULL,
    artist TEXT NOT NULL,
    title TEXT NOT NULL,
    creator TEXT NOT NULL,
    tags TEXT NOT NULL,
    favourites INTEGER NOT NULL,
    plays INTEGER NOT NULL,
    lastUpdate TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS beatmaps (
    id INTEGER PRIMARY KEY NOT NULL,
    songId INTEGER NOT NULL,
    circleSize REAL NOT NULL,
    approachRate REAL NOT NULL,
    hpDrain REAL NOT NULL,
    accuracy REAL NOT NULL,
    starDifficulty REAL NOT NULL,
    success_rate REAL NOT NULL,
    lastUpdate TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    FOREIGN KEY (songId) REFERENCES songs (id)
);

CREATE TABLE IF NOT EXISTS requests (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    songId INTEGER NOT NULL,
    rank TEXT,
    artist TEXT,
    title TEXT,
    creator TEXT,
    tags TEXT,
    favourites INTEGER,
    plays INTEGER,
    beatmapId INTEGER,
    circleSize REAL,
    approachRate REAL,
    hpDrain REAL,
    accuracy REAL,
    starDifficulty REAL,
    success_rate REAL,
    reqTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);
