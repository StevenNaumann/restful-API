# Library API

This project is to create a http4s API server along with MariaDB for storage utilizing some docker to make things easier to spin up. Currently the containers spin up and connect, I still need to test out and implement more routes.

Assuming you have SBT, docker-cli and docker desktop install, here are the steps to build and run this application.

cd into api-server and run
> sbt assembly

cd back to restful-API (assuming docker desktop is installed)
> docker-compose up