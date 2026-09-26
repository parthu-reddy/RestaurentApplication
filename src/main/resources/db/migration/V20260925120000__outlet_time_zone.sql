-- Every outlet names the IANA time zone its opening hours are written in.
--
-- Why: "is this outlet open?" compared outlet_timings (a wall clock) with the current time in a
-- hardcoded Asia/Kolkata, in two native queries and two Java loops. An outlet anywhere else would show
-- the wrong hours. The zone is now data: each outlet's hours are evaluated in its own zone.
-- RandomDocuments/TimezoneCorrectness_2026-09-25, defect D3.
--
-- Backfill: every outlet that exists today is in Bengaluru (dev data), so Asia/Kolkata. There is no
-- column default after this migration: a new outlet must state its zone when it is onboarded.

ALTER TABLE outlets ADD COLUMN time_zone VARCHAR(64);
UPDATE outlets SET time_zone = 'Asia/Kolkata';
ALTER TABLE outlets ALTER COLUMN time_zone SET NOT NULL;
