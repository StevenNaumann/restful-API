DROP DATABASE IF EXISTS library_system_db;
CREATE DATABASE library_system_db;
USE library_system_db;

CREATE TABLE books (
    book_id UUID PRIMARY KEY,
    title CHAR(60) NOT NULL,
    publish_date YEAR NOT NULL,
    genre ENUM('Fiction','Non-Fiction')
);

CREATE TABLE author (
    author_id UUID PRIMARY KEY,
    first_name CHAR(60),
    last_name CHAR (60)
);

CREATE TABLE book_authors (
    book_id UUID,
    author_id UUID
);

SET @BOOK_UUID = CAST(UUID() AS UUID);
SET @AUTHOR_UUID = CAST(UUID() AS UUID);
INSERT INTO books VALUES(@BOOK_UUID, 'The Shining', 1977, 'Fiction');
INSERT INTO author VALUES(@AUTHOR_UUID, 'Stephen', 'King');
INSERT INTO book_authors VALUES(@BOOK_UUID, @AUTHOR_UUID);

SET @BOOK_UUID = CAST(UUID() AS UUID);
SET @AUTHOR_UUID = CAST(UUID() AS UUID);
INSERT INTO books VALUES(@BOOK_UUID, 'Scala for the Impatient', 2012, 'Non-Fiction');
INSERT INTO author VALUES(@AUTHOR_UUID, 'Cay', 'Horstmann');
INSERT INTO book_authors VALUES(@BOOK_UUID, @AUTHOR_UUID);

SET @BOOK_UUID = CAST(UUID() AS UUID);
INSERT INTO books VALUES(@BOOK_UUID, 'Functional Programming in Scala', 2014, 'Non-Fiction');
SET @AUTHOR1_UUID = CAST(UUID() AS UUID);
INSERT INTO author VALUES(@AUTHOR1_UUID, 'Paul', 'Chiusano');
SET @AUTHOR2_UUID = CAST(UUID() AS UUID);
INSERT INTO author VALUES(@AUTHOR2_UUID, 'Runar', 'Bjarnason');
INSERT INTO book_authors VALUES(@BOOK_UUID, @AUTHOR1_UUID);
INSERT INTO book_authors VALUES(@BOOK_UUID, @AUTHOR2_UUID);

CREATE USER 'sample'@'%' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON *.* TO 'sample'@'%';
FLUSH PRIVILEGES;