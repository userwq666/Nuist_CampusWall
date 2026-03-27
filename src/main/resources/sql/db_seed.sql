INSERT INTO `user` (`username`, `password`, `nickname`, `education_email`, `role`, `status`)
SELECT 'admin', '$2a$10$X6jAkn5sPwNg5br0khmGs.DvqqZB1aDQL8citFvvxGVzhnbnzw8Hu', 'admin', 'admin@nuist.edu.cn', 1, 1
WHERE NOT EXISTS (SELECT 1 FROM `user` WHERE `username` = 'admin');

