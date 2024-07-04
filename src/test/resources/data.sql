-- This file allows us to load static data into the test database before tests are run.

-- Passwords are in the format: Password<UserLetter>123. Unless specified otherwise.
-- Encrypted using https://www.javainuse.com/onlineBcrypt
INSERT INTO local_user (email, first_name, last_name, password, username, email_verified)
    VALUES ('UserA@junit.com', 'UserA-FirstName', 'UserA-LastName', '$2a$10$RojbcjxTtRryKI3LHQaZ6umA5Arj9gu/top3lzvGFM62LeTpTbGv6', 'UserA', true)
    , ('UserB@junit.com', 'UserB-FirstName', 'UserB-LastName', '$2a$10$eFe9D2hIMx/GoRDd5HrUHOHbVDmy76Y1ghym022h6xI//.7OzUDze', 'UserB', false);