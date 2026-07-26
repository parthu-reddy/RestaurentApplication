CREATE INDEX IF NOT EXISTS idx_outlets_location_gist ON outlets USING GIST (location);
