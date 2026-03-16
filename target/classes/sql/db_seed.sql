INSERT INTO `user` (`username`, `password`, `nickname`, `education_email`, `role`, `status`)
SELECT 'admin', '123456', 'admin', 'admin@nuist.edu.cn', 1, 1
WHERE NOT EXISTS (SELECT 1 FROM `user` WHERE `username` = 'admin');
