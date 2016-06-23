DROP DATABASE IF EXISTS UniversityTracker;
CREATE DATABASE UniversityTracker;
USE UniversityTracker;

CREATE TABLE colleges
(
	college_name       VARCHAR(60)    PRIMARY KEY
);

CREATE TABLE students 
(
	student_id         VARCHAR(30)    PRIMARY KEY,
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
	student_from_id	VARCHAR(30) NOT NULL,
    student_to_id   VARCHAR(30) NOT NULL,	
    note_id 		INT			PRIMARY KEY  	AUTO_INCREMENT, 
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
    group_id			INT			PRIMARY KEY 		AUTO_INCREMENT,
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
    student_id		VARCHAR(30)     NOT NULL,
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
	student_id        VARCHAR(30)     NOT NULL, 
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
	thread_id 		INT 			PRIMARY KEY 		AUTO_INCREMENT,
	group_id 		INT				NOT NULL,
    thread_text		MEDIUMTEXT,
    thread_poster 	VARCHAR(30) 	NOT NULL,
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
    student_id		VARCHAR(30) 	NOT NULL,
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
	SELECT DISTINCT groups.group_name, groups.group_id, groups.college_name
	FROM members JOIN groups
		ON groups.group_id = members.group_id
	WHERE members.student_id = username
	ORDER BY groups.group_id;
END//

/*DROP PROCEDURE IF EXISTS get_other_groups//
CREATE PROCEDURE get_other_groups
(
	username VARCHAR(30)
)
BEGIN
	SELECT DISTINCT groups.group_name, groups.group_id, groups.college_name
	FROM members JOIN groups
		ON groups.group_id = members.group_id
	WHERE NOT members.student_id = username
	ORDER BY groups.group_id;
END//
*/

DROP PROCEDURE IF EXISTS get_sent_notes//
CREATE PROCEDURE get_sent_notes
(
	username VARCHAR(30)
)
BEGIN
	SELECT notes.student_to_id, notes.note_text
	FROM notes
	WHERE notes.student_from_id = username
	ORDER BY notes.note_id;
END//

DROP PROCEDURE IF EXISTS get_received_notes//
CREATE PROCEDURE get_received_notes
(
	username VARCHAR(30)
)
BEGIN
	SELECT notes.student_from_id, notes.note_text
	FROM notes
	WHERE notes.student_to_id = username 
	ORDER BY notes.note_id;
END//

DROP PROCEDURE IF EXISTS get_members//
CREATE PROCEDURE get_members
(
	id VARCHAR(30)
)
BEGIN
	SELECT members.student_id
	FROM members JOIN groups
		ON groups.group_id = members.group_id
	WHERE groups.group_id = id
	ORDER BY members.student_id;
END//

DROP PROCEDURE IF EXISTS get_admins//
CREATE PROCEDURE get_admins
(
	name VARCHAR(30)
)
BEGIN
	SELECT group_admin.student_id
	FROM group_admin JOIN groups
		ON groups.group_id = group_admin.group_id
	WHERE groups.group_name = name
	ORDER BY group_admin.student_id;
END//

DROP PROCEDURE IF EXISTS get_threads//
CREATE PROCEDURE get_threads
(
	name VARCHAR(30)
)
BEGIN
	SELECT thread.thread_id, thread.thread_poster, thread.thread_text
	FROM thread JOIN groups
		ON groups.group_id = thread.group_id
	WHERE groups.group_name = name
	ORDER BY thread.thread_id;
END//

DROP PROCEDURE IF EXISTS get_comments//
CREATE PROCEDURE get_comments
(
	id VARCHAR(30)
)
BEGIN
	SELECT thread_comment.comment_date, thread_comment.student_id, thread_comment.thread_text
	FROM thread_comment
	WHERE thread_comment.thread_id = id
	ORDER BY thread_comment.comment_date;
END//

DROP FUNCTION IF EXISTS verify_user//
CREATE FUNCTION verify_user
(
	user VARCHAR(30),
    pass VARCHAR(60)
)
RETURNS BOOLEAN
BEGIN
	DECLARE verified BOOLEAN;
    SET verified = FALSE;
	IF EXISTS (SELECT students.student_id 
				FROM students 
				WHERE student_id = user 
					AND student_password = pass)
		THEN SET verified = TRUE;   
	END IF;
    RETURN(verified);
END//

DROP FUNCTION IF EXISTS user_is_banned//
CREATE FUNCTION user_is_banned
(
	user VARCHAR(30),
    gname VARCHAR(30)
)
RETURNS BOOLEAN
BEGIN
	DECLARE banned BOOLEAN;
    SET banned = FALSE;
	IF EXISTS (SELECT students.student_id 
				FROM ban_list JOIN groups
					ON ban_list.group_id = groups.group_id
				WHERE ban_list.student_id = user 
					AND groups.group_name = gname)
		THEN SET banned = TRUE;   
	END IF;
    RETURN(banned);
END//