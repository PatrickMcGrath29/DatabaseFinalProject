DROP DATABASE IF EXISTS UniversityTracker;
CREATE DATABASE UniversityTracker;
USE UniversityTracker;

CREATE TABLE colleges
(
	college_name       VARCHAR(60)    PRIMARY KEY
);

CREATE TABLE students 
(
	student_id         VARCHAR(30)    PRIMARY KEY	UNIQUE,
    student_password   VARCHAR(60)	  NOT NULL, 
	first_name 		   VARCHAR(30)    NOT NULL, 
	last_name 		   VARCHAR(30)    NOT NULL,
	college_name       VARCHAR(60)    NOT NULL,
	CONSTRAINT college_name_fk
		FOREIGN KEY (college_name)
		REFERENCES colleges (college_name)
	ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE TABLE notes
(
	student_from_id	VARCHAR(30) NOT NULL		UNIQUE,
    student_to_id   VARCHAR(30) NOT NULL		UNIQUE,	
    note_id 		INT			PRIMARY KEY 	UNIQUE,
    note_text		MEDIUMTEXT,
    CONSTRAINT student_from_fk
		FOREIGN KEY (student_from_id)
		REFERENCES students (student_id)
		ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT student_to_fk
		FOREIGN KEY (student_to_id)
		REFERENCES students (student_id)
		ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE table groups
(
	group_name			VARCHAR(50) NOT NULL,
    group_id			INT			PRIMARY KEY,
    college_name		VARCHAR(60),
    purpose_statement	TEXT,
    CONSTRAINT college_name_groups_fk
		FOREIGN KEY (college_name)
		REFERENCES colleges (college_name)
		ON DELETE CASCADE ON UPDATE SET NULL 
);

CREATE TABLE members
(
	group_id 		INT				NOT NULL,
    student_id		VARCHAR(30)     NOT NULL	UNIQUE,
    PRIMARY KEY (group_id, student_id),
    CONSTRAINT group_id_reference_fk
		FOREIGN KEY (group_id)
        REFERENCES groups (group_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT student_id_reference_fk
		FOREIGN KEY (student_id)
        REFERENCES students (student_id)
        ON DELETE CASCADE ON UPDATE CASCADE 
);
    
CREATE TABLE group_admin
(
	student_id        VARCHAR(30)     NOT NULL 		UNIQUE, 
    group_id 		  INT 			  NOT NULL,
    PRIMARY KEY (group_id, student_id),
	CONSTRAINT student_id_fk
		FOREIGN KEY (student_id)
		REFERENCES students (student_id)
	ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT group_id_fk
		FOREIGN KEY (group_id)
		REFERENCES groups (group_id)
	ON DELETE CASCADE ON UPDATE CASCADE
);    

CREATE TABLE ban_list
(
	student_id        VARCHAR(30)     NOT NULL, 
    group_id 		  INT 			  NOT NULL,
    PRIMARY KEY (student_id, group_id), 
    CONSTRAINT group_ban_fk
		FOREIGN KEY (group_id)
        REFERENCES groups (group_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT student_ban_fk
		FOREIGN KEY (student_id)
        REFERENCES students (student_id)
        ON DELETE CASCADE ON UPDATE CASCADE 
);

CREATE TABLE thread
(
	thread_id 		INT 			PRIMARY KEY,
	group_id 		INT				NOT NULL,
    thread_text		MEDIUMTEXT,
    thread_poster 	VARCHAR(30) 	NOT NULL		UNIQUE,
    CONSTRAINT group_thread_fk
		FOREIGN KEY (group_id)
        REFERENCES groups (group_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT poster_fk
		FOREIGN KEY (thread_poster)
        REFERENCES students (student_id)
        ON DELETE CASCADE ON UPDATE CASCADE 
);

CREATE TABLE thread_comment
(
	thread_id 		INT				NOT NULL, 
    thread_text 	MEDIUMTEXT, 
    comment_date 	TIMESTAMP 		NOT NULL, 
    student_id		VARCHAR(30) 	NOT NULL	UNIQUE,
	PRIMARY KEY (thread_id, student_id, comment_date),
    CONSTRAINT comment_poster_fk
		FOREIGN KEY (student_id)
        REFERENCES students (student_id)
        ON DELETE CASCADE ON UPDATE CASCADE , 
	CONSTRAINT thread_fk
		FOREIGN KEY (thread_id)
        REFERENCES thread (thread_id)
        ON DELETE CASCADE ON UPDATE CASCADE 
);
    
INSERT INTO UniversityTracker.colleges VALUES 
("College of Arts, Media and Design"), 
("D'Amore-McKim School of Business"), 
("College of Computer and Information Science"), 
("College of Engineering"), 
("Bouvé College of Health Sciences"), 
("School of Law"), 
("College of Professional Studies"), 
("College of Science"), 
("College of Social Sciences and Humanities"), 
("None");

DELIMITER //

DROP PROCEDURE IF EXISTS get_groups//
CREATE PROCEDURE get_groups
(
	username VARCHAR(30)
)
BEGIN
	SELECT groups.group_name
	FROM members JOIN groups
		ON groups.group_id = members.group_id
	WHERE members.student_id = name
	ORDER BY groups.group_name;
END//

DROP PROCEDURE IF EXISTS get_members//
CREATE PROCEDURE get_members
(
	name VARCHAR(30)
)
BEGIN
	SELECT members.student_id
	FROM members JOIN groups
		ON groups.group_id = members.group_id
	WHERE groups.group_name = name
	ORDER BY members.student_id;
END//

DROP FUNCTION IF EXISTS verify_user//
CREATE FUNCTION verify_user
(
	user VARCHAR(30),
    pass VARCHAR(30)
)
RETURNS BOOLEAN
BEGIN
	DECLARE verified BOOLEAN;
	IF EXISTS (SELECT students.student_id 
				FROM students 
				WHERE student_id = user 
					AND student_password = pass)
		THEN SET verified = TRUE;
	ELSE 
        SET verified = FALSE;
	END IF;
    RETURN(verified);
END//