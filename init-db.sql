-- Create database if not exists
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = 'BlindBoxDB')
BEGIN
    CREATE DATABASE BlindBoxDB;
    PRINT 'Database BlindBoxDB created successfully';
END
ELSE
BEGIN
    PRINT 'Database BlindBoxDB already exists';
END
GO
