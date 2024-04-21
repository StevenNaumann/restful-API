FROM mariadb:lts as builder

# remove last line to avoid the initialization process from launching the database, run the initdb scripts, closing it, then launch the daemon
#RUN ["sed", "-i", "s/exec \"$@\"/echo \"not running $@\"/", "/usr/local/bin/docker-entrypoint.sh"]

# needed for intialization
ENV MYSQL_ROOT_PASSWORD=root

ADD ./init_db.sql /docker-entrypoint-initdb.d




