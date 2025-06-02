CREATE DATABASE healthcare_project;
USE healthcare_project;

CREATE TABLE class (
    class_id INT AUTO_INCREMENT PRIMARY KEY,
    teacher_name VARCHAR(100),
    class_name VARCHAR(20),
    quantity INT
);


CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    password VARCHAR(255) NULL,
    phone VARCHAR(20),
    address VARCHAR(200),
    role VARCHAR(50) DEFAULT 'parent',
    CHECK (role IN ('parent', 'admin', 'nurse'))
);

CREATE TABLE student (
    student_id INT AUTO_INCREMENT PRIMARY KEY,
    student_name VARCHAR(100) NOT NULL,
    dob DATE,
    gender VARCHAR(10),
    relationship VARCHAR(50),
    class_id INT,
    parent_id INT,
    FOREIGN KEY (class_id) REFERENCES class(class_id) ON DELETE CASCADE,
    FOREIGN KEY (parent_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE medical_request (
    request_id INT AUTO_INCREMENT PRIMARY KEY,
    request_name VARCHAR(100),
    date DATE,
    note VARCHAR(255),
    status VARCHAR(50),
    commit BOOLEAN,
    student_id INT,
    parent_id INT,
    class_id INT,
    CHECK (status IN ('pending', 'approved', 'rejected')),
    FOREIGN KEY (student_id) REFERENCES student(student_id) ON DELETE CASCADE,
    FOREIGN KEY (parent_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (class_id) REFERENCES class(class_id) ON DELETE CASCADE
);

CREATE TABLE medical_request_detail (
    detail_id INT AUTO_INCREMENT PRIMARY KEY,
    medicine_name VARCHAR(100),
    quantity INT,
    instruction VARCHAR(255),
    time DATETIME,
    request_id INT,
    FOREIGN KEY (request_id) REFERENCES medical_request(request_id) ON DELETE CASCADE
);

CREATE TABLE medical_event (
    event_id INT AUTO_INCREMENT PRIMARY KEY,
    type_event VARCHAR(100),
    date DATE,
    description VARCHAR(255),
    student_id INT,
    nurse_id INT,
    FOREIGN KEY (student_id) REFERENCES student(student_id) ON DELETE CASCADE,
    FOREIGN KEY (nurse_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE medical_supply (
    supply_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    quantity INT,
    unit VARCHAR(50),
    expiry_date DATE,
    note VARCHAR(255)
);

CREATE TABLE medical_event_supply (
    event_id INT,
    supply_id INT,
    quantity INT,
    note VARCHAR(255),
    PRIMARY KEY (event_id, supply_id),
    FOREIGN KEY (event_id) REFERENCES medical_event(event_id) ON DELETE CASCADE,
    FOREIGN KEY (supply_id) REFERENCES medical_supply(supply_id) ON DELETE CASCADE
);

CREATE TABLE health_check_program (
    health_check_id INT AUTO_INCREMENT PRIMARY KEY,
    health_check_name VARCHAR(100),
    description VARCHAR(255),
    start_date DATE,
    end_date DATE,
    status VARCHAR(50),
    note VARCHAR(255),
    nurse_id INT,
    CHECK (status IN ('ongoing', 'completed', 'not started')),
    FOREIGN KEY (nurse_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE health_check_form (
    health_check_form_id INT AUTO_INCREMENT PRIMARY KEY,
    health_check_id INT,
    student_id INT,
    parent_id INT,
    form_date DATE,
    notes VARCHAR(255),
    commit BOOLEAN,
    FOREIGN KEY (health_check_id) REFERENCES health_check_program(health_check_id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES student(student_id) ON DELETE CASCADE,
    FOREIGN KEY (parent_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE health_check_result (
    result_id INT AUTO_INCREMENT PRIMARY KEY,
    diagnosis VARCHAR(255),
    level VARCHAR(50),
    note VARCHAR(255),
    nurse_id INT,
    health_check_form_id INT,
    CHECK (level IN ('good', 'fair', 'average', 'poor')),
    FOREIGN KEY (nurse_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (health_check_form_id) REFERENCES health_check_form(health_check_form_id) ON DELETE CASCADE
);

CREATE TABLE medical_record (
    record_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT,
    allergies VARCHAR(255),
    chronic_disease VARCHAR(255),
    treatment_history VARCHAR(255),
    vision VARCHAR(50),
    hearing VARCHAR(50),
    weight FLOAT,
    high FLOAT,
    last_update DATE,
    note VARCHAR(255),
    FOREIGN KEY (student_id) REFERENCES student(student_id) ON DELETE CASCADE
);

CREATE TABLE vaccine_program (
    vaccine_id INT AUTO_INCREMENT PRIMARY KEY,
    vaccine_name VARCHAR(100),
    description VARCHAR(255),
    vaccine_date DATE,
    note VARCHAR(255),
    nurse_id INT,
    FOREIGN KEY (nurse_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE vaccine_form (
    vaccine_form_id INT AUTO_INCREMENT PRIMARY KEY,
    vaccine_id INT,
    student_id INT,
    parent_id INT,
    form_date DATE,
    note VARCHAR(255),
    commit BOOLEAN,
    FOREIGN KEY (vaccine_id) REFERENCES vaccine_program(vaccine_id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES student(student_id) ON DELETE CASCADE,
    FOREIGN KEY (parent_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE vaccine_result (
    result_id INT AUTO_INCREMENT PRIMARY KEY,
    vaccine_form_id INT,
    result_note VARCHAR(255),
    reaction VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    nurse_id INT,
    FOREIGN KEY (vaccine_form_id) REFERENCES vaccine_form(vaccine_form_id) ON DELETE CASCADE,
    FOREIGN KEY (nurse_id) REFERENCES users(user_id) ON DELETE CASCADE
);



CREATE TABLE vaccine_history (
    vaccine_history_id INT AUTO_INCREMENT PRIMARY KEY,
    vaccine_name VARCHAR(100),
    note VARCHAR(255),
    record_id INT,
    FOREIGN KEY (record_id) REFERENCES medical_record(record_id) ON DELETE CASCADE
);

CREATE TABLE survey (
    survey_id INT AUTO_INCREMENT PRIMARY KEY,
    satisfaction VARCHAR(20),
    reaction VARCHAR(255),
    comment VARCHAR(255),
    created_at DATETIME,
    parent_id INT,
    vaccine_form_id INT,
    health_check_form_id INT,
	event_id INT,
	status VARCHAR(20),
    CHECK (satisfaction IN ('satisfied', 'unsatisfied')),
    FOREIGN KEY (parent_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (vaccine_form_id) REFERENCES vaccine_form(vaccine_form_id) ON DELETE CASCADE,
    FOREIGN KEY (health_check_form_id) REFERENCES health_check_form(health_check_form_id) ON DELETE CASCADE
	FOREIGN KEY (event_id) REFERENCES medical_event(event_id) ON DELETE CASCADE
);

CREATE TABLE blacklisted_tokens (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    token VARCHAR(255) NOT NULL UNIQUE,
    expiry_date DATETIME NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_blacklisted_user
        FOREIGN KEY (user_id)
        REFERENCES users(user_id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

CREATE TABLE refresh_token (
    id INT AUTO_INCREMENT PRIMARY KEY,
    refresh_token VARCHAR(255) NOT NULL UNIQUE,
    expiry_date DATETIME NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    user_id INT UNIQUE,
    CONSTRAINT fk_refresh_token_user
        FOREIGN KEY (user_id)
        REFERENCES users(user_id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
);

INSERT INTO class (teacher_name, class_name, quantity) VALUES
('Nguyen Van A', '1A', 30),
('Tran Thi B', '2B', 28),
('Le Van C', '3C', 35);

INSERT INTO users (full_name, email, password, phone, address, role) VALUES
('Pham Thi Hoa', 'hoa@example.com', 'hashed_password1', '0123456789', '123 ABC Street', 'parent'),
('Nguyen Van Binh', 'binh@example.com', 'hashed_password2', '0987654321', '456 DEF Street', 'parent'),
('Le Thi Lan', 'lan@example.com', 'hashed_password3', '0345678912', '789 GHI Street', 'parent');

INSERT INTO student (student_name, dob, gender, relationship, class_id, parent_id) VALUES
('Nguyen Thi Mai', '2015-09-01', 'Female', 'Mother', 1, 1),
('Tran Van Khoa', '2014-08-20', 'Male', 'Father', 2, 2),
('Le Thi Thu', '2013-05-15', 'Female', 'Mother', 3, 3);


