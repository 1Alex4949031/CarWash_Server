CREATE TABLE roles (
  id INTEGER PRIMARY KEY
  /* H2 syntax */
  /* use IF statement to choose syntax based on database */
  /* PostgreSQL syntax */
  /* use IF statement to choose syntax based on database */
  ,
  name VARCHAR(256) NOT NULL
);

INSERT INTO roles (name) VALUES ('ROLE_USER');
INSERT INTO roles (name) VALUES ('ROLE_MODERATOR');
INSERT INTO roles (name) VALUES ('ROLE_ADMIN');