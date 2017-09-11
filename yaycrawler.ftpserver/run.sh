#!/usr/bin/env bash
# export SPRING_PROFILES_ACTIVE=uat
# nohup mvn -Puat -DskipTests spring-boot:run > ../smart-schedule.log 2>&1 &
nohup mvn -Dspring.profiles.active=prod -DskipTests spring-boot:run -Ddebug > ../logs/yaycrawler-ftpserver.log 2>&1 &