-- change PASSWORD

create database if not exists tracker;
grant all privileges on tracker.* to tracker@localhost identified by "PASSWORD";
flush privileges;

use tracker;

drop table if exists users;
create table users (
  id varchar(36) not null,
  pin varchar(6) not null,
  creation_date datetime not null,
  update_date datetime not null,
  ip varchar(15),
  latitude float(10, 6),
  longitude float(10, 6),
  bot bool,
  primary key(id)
) default charset=utf8;

drop table if exists check_points;
create table check_points (
  id int not null auto_increment,
  name varchar(50) not null,
  latitude float(10, 6) not null,
  longitude float(10, 6) not null,
  radius int not null,
  primary key(id)
) default charset=utf8;
