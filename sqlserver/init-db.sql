-- Create BlindBoxDB database for MSAccount service
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

-- Create BlindBoxDB_BlindBox database for MSBlindBox service
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = 'BlindBoxDB_BlindBox')
BEGIN
    CREATE DATABASE BlindBoxDB_BlindBox;
    PRINT 'Database BlindBoxDB_BlindBox created successfully';
END
ELSE
BEGIN
    PRINT 'Database BlindBoxDB_BlindBox already exists';
END
GO

-- Create BlindBoxDB_Brand database for MSBrand service
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = 'BlindBoxDB_Brand')
BEGIN
    CREATE DATABASE BlindBoxDB_Brand;
    PRINT 'Database BlindBoxDB_Brand created successfully';
END
ELSE
BEGIN
    PRINT 'Database BlindBoxDB_Brand already exists';
END
GO

PRINT 'All databases setup completed!';
GO
