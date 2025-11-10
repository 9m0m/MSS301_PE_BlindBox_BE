#!/bin/bash

# Start SQL Server in the background
/opt/mssql/bin/sqlservr &

# Wait for SQL Server to start
echo "Waiting for SQL Server to start..."
sleep 30s

# Create database
echo "Creating BlindBoxDB database..."
/opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P YourStrong@Passw0rd -C -Q "IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = 'BlindBoxDB') CREATE DATABASE BlindBoxDB"

echo "Database setup completed!"

# Keep the container running
wait
