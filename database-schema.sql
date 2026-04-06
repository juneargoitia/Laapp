-- Módulo football-api
CREATE TABLE IF NOT EXISTS matches (
    id INTEGER PRIMARY KEY,
    localTeam TEXT NOT NULL,
    visitorTeam TEXT NOT NULL,
    scoreLocal INTEGER,
    scoreVisitor INTEGER,
    matchday TEXT,
    matchStatus TEXT,
    matchDate TEXT,
    competition TEXT,
    capturedAt TEXT NOT NULL
);

-- Módulo travel-scrapper
CREATE TABLE IF NOT EXISTS flights (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    flightNumber TEXT NOT NULL,
    origin TEXT NOT NULL,
    destination TEXT NOT NULL,
    departureTime TEXT,
    arrivalTime TEXT,
    flightStatus TEXT,
    airline TEXT,
    capturedAt TEXT NOT NULL
);