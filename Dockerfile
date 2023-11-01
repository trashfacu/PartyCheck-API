FROM mysql:latest

# Set the root password for MySQL
ENV MYSQL_ROOT_PASSWORD=root

# Create the database
ENV MYSQL_DATABASE=party_check_db

# Copy the SQL schema into the container
COPY assets/create_db.sql /docker-entrypoint-initdb.d/

# Expose the default MySQL port
EXPOSE 3306
