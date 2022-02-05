DROP TABLE IF EXISTS user;

CREATE TABLE user (
  id            BIGINT          PRIMARY KEY,
  userId        VARCHAR(36)     NOT NULL,
  username      VARCHAR(36)     NOT NULL,
  emails        BINARY          NOT NULL,
  user          LONGBLOB        NOT NULL
);