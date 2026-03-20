-- Módulo football-api
CREATE TABLE IF NOT EXISTS matches (
    id INTEGER PRIMARY KEY,
    local_team TEXT NOT NULL,
    visitor_team TEXT NOT NULL,
    score_local INTEGER,
    score_visitor INTEGER,
    matchday INTEGER,
    match_status TEXT,
    match_date TEXT,
    competition TEXT,
    captured_at TEXT NOT NULL
);

-- Módulo travel-scrapper
CREATE TABLE IF NOT EXISTS flights (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    flight_number TEXT NOT NULL,
    destination TEXT NOT NULL,
    departure_time TEXT,
    flight_status TEXT,
    airline TEXT,
    captured_at TEXT NOT NULL
);