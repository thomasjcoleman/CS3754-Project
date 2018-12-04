-- May be subject to change

DROP TABLE IF EXISTS UserInterests, UserFile, UserPhoto, UserTrip, User;

CREATE TABLE User (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  username VARCHAR(32) NOT NULL,
  password VARCHAR(256) NOT NULL,
  first_name VARCHAR(32) NOT NULL,
  middle_name VARCHAR(32),
  last_name VARCHAR(32) NOT NULL,
  address1 VARCHAR(128) NOT NULL,
  address2 VARCHAR(128),
  city VARCHAR(64) NOT NULL,
  state VARCHAR(2) NOT NULL,
  zipcode VARCHAR(10) NOT NULL,
  security_question_number INT NOT NULL,
  security_answer VARCHAR(128) NOT NULL,
  email VARCHAR(128) NOT NULL,
  phone_number VARCHAR(128) NOT NULL,
  phone_carrier VARCHAR(128) NOT NULL,
  google_id INT,
  PRIMARY KEY (id)
);

CREATE TABLE UserTrip (
  id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT NOT NULL,
  trail_id INT UNSIGNED,
  trip_name VARCHAR(256) NOT NULL,
  trip_date DATE NOT NULL,
  user_id INT UNSIGNED,
  FOREIGN KEY (user_id) REFERENCES User(id) ON DELETE CASCADE
);

CREATE TABLE UserPhoto (
  id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT NOT NULL,
  extension ENUM('jpeg', 'jpg', 'png', 'gif') NOT NULL,
  user_id INT UNSIGNED,
  FOREIGN KEY (user_id) REFERENCES User(id) ON DELETE CASCADE
);

CREATE TABLE UserFile (
  id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT NOT NULL,
  filename VARCHAR(256) NOT NULL,
  trip_id INT UNSIGNED,
  FOREIGN KEY (trip_id) REFERENCES UserTrip(id) ON DELETE CASCADE,
  user_id INT UNSIGNED,
  FOREIGN KEY (user_id) REFERENCES User(id) ON DELETE CASCADE
);

CREATE TABLE UserInterests (
  id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT NOT NULL,
  trail_id INT UNSIGNED NOT NULL,
  interested BOOLEAN,
  completed BOOLEAN,
  user_id INT UNSIGNED,
  FOREIGN KEY (user_id) REFERENCES User(id) ON DELETE CASCADE
)