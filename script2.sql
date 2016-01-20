# ---------------------------------------
# Host     : localhost
# Port     : 3306
# Database : nag

USE `nag`;

#
# Structure for the `dog` table : 
#

CREATE TABLE `dog` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` tinytext,
  `age` int(11) DEFAULT NULL,
  `breed` tinytext ,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=252 DEFAULT CHARSET=utf8;


