DROP DATABASE IF EXISTS university;
CREATE DATABASE university;
USE university;

CREATE TABLE colleges
(
	college_name       VARCHAR(30)    PRIMARY KEY
);

CREATE TABLE students 
(
	student_id         INT            PRIMARY KEY,
	first_name 		   VARCHAR(30)    NOT NULL, 
	last_name 		   VARCHAR(30)    NOT NULL,
	college_name       VARCHAR(30)    NOT NULL,
	CONSTRAINT college_name_fk
		FOREIGN KEY (college_name)
		REFERENCES colleges (college_name)
	ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE TABLE notes
(
	student_from_id	INT 		NOT NULL,
    student_to_id   INT   		NOT NULL,
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
    college_name		VARCHAR(30),
    purpose_statement	TEXT,
    CONSTRAINT college_name_groups_fk
		FOREIGN KEY (college_name)
		REFERENCES colleges (college_name)
		ON DELETE CASCADE ON UPDATE SET NULL 
);

CREATE TABLE members
(
	group_id 		INT				NOT NULL,
    student_id		INT     		NOT NULL,
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
	student_id        INT             NOT NULL, 
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

CREATE TABLE thread
(
	thread_id 		INT 			PRIMARY KEY,
	group_id 		INT				NOT NULL,
    thread_text		MEDIUMTEXT,
    thread_poster 	INT 			NOT NULL,
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
    student_id		INT 			NOT NULL,
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
    

    
    
    
    
	