-- ==========================================================
-- V017 — Spring Modulith Event Publication table
-- Required by spring.modulith.events.republish-outstanding-events-on-restart
-- ==========================================================

CREATE TABLE IF NOT EXISTS event_publication (
    id                 UUID            NOT NULL PRIMARY KEY,
    listener_id        TEXT            NOT NULL,
    event_type         TEXT            NOT NULL,
    serialized_event   TEXT            NOT NULL,
    publication_date   TIMESTAMPTZ     NOT NULL,
    completion_date    TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS idx_event_publication_by_completion
    ON event_publication (completion_date);

