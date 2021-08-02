SET GLOBAL concurrent_insert=2;
CREATE DATABASE IF NOT EXISTS Chatroom;
USE Chatroom;
# 只有插入和读取操作,重复的username采取覆盖处理
CREATE TABLE IF NOT EXISTS `User`(
    `username` VARCHAR(200) PRIMARY KEY NOT NULL,
    `firstName` VARCHAR(200) NOT NULL,
    `lastName` VARCHAR(200) NOT NULL,
    `email` VARCHAR(200) NOT NULL,
    `password` VARCHAR(200) NOT NULL,
    `phone` VARCHAR(200) NOT NULL
) ENGINE=Innodb DEFAULT CHARSET=utf8mb4;
# 只有插入和读取操作,允许重名room创建
CREATE TABLE IF NOT EXISTS `Room`(
    `roomId` INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
    `name` VARCHAR(200) NOT NULL
) ENGINE=Innodb DEFAULT CHARSET=utf8mb4;
# message只会插入和读取,没有删除和更新的操作
CREATE TABLE IF NOT EXISTS `Message`(
    `roomId` INT NOT NULL,
    `messageId` VARCHAR(200) NOT NULL,
    `text` VARCHAR(200) NOT NULL,
    `timestamp` VARCHAR(200) NOT NULL,
    INDEX `room_message_index` (`roomId`,`timestamp`)
) ENGINE=Myisam DEFAULT CHARSET=utf8mb4;
# 可能有频繁的删除插入读取操作
CREATE TABLE IF NOT EXISTS `RoomUser`(
    `roomId` INT NOT NULL,
    `username` VARCHAR(200) NOT NULL,
    PRIMARY KEY (`roomId`,`username`)
) ENGINE=Innodb DEFAULT CHARSET=utf8mb4;

# CREATE TABLE IF NOT EXISTS `UserRoom`(
#      `username` VARCHAR(200) PRIMARY KEY NOT NULL,
#      `roomId` INT NOT NULL
# ) ENGINE=Myisam DEFAULT CHARSET=utf8mb4;
