-- User-job relationships are assigned dynamically via the API in tests
-- This file intentionally contains only the cleanup statement to clear the table before tests
DELETE FROM "user_job";
