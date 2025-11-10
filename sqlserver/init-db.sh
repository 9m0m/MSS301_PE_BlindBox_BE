#!/bin/bash

# Start SQL Server in background
/opt/mssql/bin/sqlservr &

# Wait for SQL Server to be ready
echo "Waiting for SQL Server to start..."
sleep 30s

# Run initialization script
echo "Creating database BlindBoxDB..."
/opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P "$SA_PASSWORD" -C -i /usr/src/app/init-db.sql

echo "Database initialization completed!"

# Keep SQL Server running in foreground
wait
