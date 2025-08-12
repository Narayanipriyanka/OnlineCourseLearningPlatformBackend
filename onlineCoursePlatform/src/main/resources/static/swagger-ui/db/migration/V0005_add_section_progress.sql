CREATE TABLE section_progress (
  id BIGINT NOT NULL AUTO_INCREMENT,
  is_completed BIT,
  progress_percentage FLOAT(53),
  video_duration_watched FLOAT(53),
  course_progress_id BIGINT,
  section_id BIGINT,
  PRIMARY KEY (id)
) ENGINE=InnoDB;

ALTER TABLE section ADD COLUMN video_duration FLOAT(53);

ALTER TABLE section_progress
ADD CONSTRAINT FK_section_progress_course_progress
FOREIGN KEY (course_progress_id)
REFERENCES course_progress (id);

ALTER TABLE section_progress
ADD CONSTRAINT FK_section_progress_section
FOREIGN KEY (section_id)
REFERENCES section (id);
