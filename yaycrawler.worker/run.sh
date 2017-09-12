#!/bin/bash
# export SPRING_PROFILES_ACTIVE=uat
# nohup mvn -Puat -Dspring.profiles.active=uat -DskipTests spring-boot:run -Ddebug > ../logs/yaycrawler-worker.log 2>&1 &
  nohup mvn -Pprod -Dspring.profiles.active=prod -DskipTests spring-boot:run -Ddebug > ../logs/yaycrawler-worker.log 2>&1 &