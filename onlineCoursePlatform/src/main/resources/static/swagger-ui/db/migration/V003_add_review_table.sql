ALTER TABLE review ADD COLUMN user_id BIGINT;
ALTER TABLE review ADD CONSTRAINT FKprox8elgnr8u5wrq1983degk FOREIGN KEY (course_id) REFERENCES course (id);
ALTER TABLE review ADD CONSTRAINT FKiyf57dy48lyiftdrf7y87rnxi FOREIGN KEY (user_id) REFERENCES user (id);
