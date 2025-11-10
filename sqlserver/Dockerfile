FROM mcr.microsoft.com/mssql/server:2022-latest

USER root

# Copy initialization script
COPY init-db.sh /usr/src/app/
COPY init-db.sql /usr/src/app/

# Grant permissions
RUN chmod +x /usr/src/app/init-db.sh

USER mssql

# Set entrypoint
ENTRYPOINT ["/usr/src/app/init-db.sh"]
