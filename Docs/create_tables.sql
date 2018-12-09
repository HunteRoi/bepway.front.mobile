CREATE SCHEMA [bepway];

CREATE TABLE [bepway].[User] (
	[login] [nvarchar] NOT NULL,
	[password] [nvarchar] NOT NULL,
	[email] [nvarchar] NOT NULL,
	[birthdate] [date] NOT NULL,
	[isAdmin] [bit] NOT NULL,
	[isEnabled] [bit] NOT NULL,
	[todoList] [nvarchar],
	[creator] [nvarchar],
	CONSTRAINT userKey PRIMARY KEY ( [login] ),
	CONSTRAINT creatorKey FOREIGN KEY ( [creator] ) REFERENCES [bepway].[User] ( [login] )
);

CREATE TABLE [bepway].[GeoCoordinates]
(
	[latitude] [numeric] NOT NULL,
	[longitude] [numeric] NOT NULL,
	[order] [numeric] NOT NULL,
	CONSTRAINT geoCoordinatesKey PRIMARY KEY ([latitude],[longitude])
);

CREATE TABLE [bepway].[Zoning]
(
	[id] [numeric] NOT NULL IDENTITY(0,1),
	[name] [nvarchar] NOT NULL,
	CONSTRAINT zoningKey PRIMARY KEY ([id])
);

CREATE TABLE [bepway].[Road]
(
	[id] [numeric] NOT NULL IDENTITY(0,1),
	[isPracticable] [bit] NOT NULL,
	[zoning_id] [numeric] NOT NULL,
	CONSTRAINT roadKey PRIMARY KEY ([id]),
	CONSTRAINT roadZoning FOREIGN KEY ([zoning_id]) REFERENCES [bepway].[Zoning]([id])
);

CREATE TABLE [bepway].[RoadGeoreference]
(
	[latitude] [numeric] NOT NULL,
	[longitude][numeric] NOT NULL,
	[road_id] [numeric] NOT NULL,
	CONSTRAINT roadGeoreferenceKey PRIMARY KEY ([latitude],[longitude],[road_id]),
	CONSTRAINT roadRoadGeoreference FOREIGN KEY ([road_id]) REFERENCES [bepway].[Road] ([id])
);

CREATE TABLE [bepway].[Address]
(
	[id] [numeric] NOT NULL IDENTITY(0,1),
	[number] [numeric] NOT NULL,
	[postalBox] [nvarchar] NOT NULL,
	[zipCode] [numeric] NOT NULL,
	[city] [nvarchar] NOT NULL,
	[address] [nvarchar] NOT NULL,
	[floorNumber] [numeric],
	[road_id] [numeric] NOT NULL,
	CONSTRAINT addressKey PRIMARY KEY ([id]),
	CONSTRAINT addressRoad FOREIGN KEY ([road_id]) REFERENCES [bepway].[Road] ([id])
);

CREATE TABLE [bepway].[Company]
(
	[id] [numeric] NOT NULL IDENTITY(0,1),
	[name] [nvarchar] NOT NULL,
	[imageURL] [nvarchar],
	[sector] [nvarchar] NOT NULL,
	[urlSite] [nvarchar],
	[status] [nvarchar] NOT NULL,
	[coordinates] [DBGeography] NOT NULL,
	CONSTRAINT companyKey PRIMARY KEY ([id])
);

CREATE TABLE [bepway].[History]
(
	[id] [numeric] NOT NULL IDENTITY(0,1),
	[address_id] [numeric] NOT NULL,
	[startDate] [date] NOT NULL,
	[endDate] [date],
	[company_id] [numeric] NOT NULL,
	CONSTRAINT historyKey PRIMARY KEY ([id]),
	CONSTRAINT historyAddress FOREIGN KEY ([address_id]) REFERENCES [bepway].[Address] ([id]),
	CONSTRAINT historyCompany FOREIGN KEY ([company_id]) REFERENCES [bepway].[Company] ([id])
);

CREATE TABLE [bepway].[Language]
(
	[id] [numeric] NOT NULL IDENTITY(0,1),
	[name] [nvarchar] NOT NULL,
	CONSTRAINT languageKey PRIMARY KEY ([id])
);

CREATE TABLE [bepway].[AddressTranslation]
(
	[id] [numeric] NOT NULL IDENTITY(0,1),
	[address_id] [numeric] NOT NULL,
	[city] [nvarchar] NOT NULL,
	CONSTRAINT addressTranslationKey PRIMARY KEY ([id]),
	CONSTRAINT addressTransleted FOREIGN KEY ([address_id]) REFERENCES [bepway].[Address] ([id])
);

CREATE TABLE [bepway].[CompanyTranslation]
(
	[id] [numeric] NOT NULL IDENTITY(0,1),
	[company_id] [numeric] NOT NULL,
	[activitySector] [nvarchar] NOT NULL,
	CONSTRAINT companyTranslationKey PRIMARY KEY ([id]),
	CONSTRAINT companyTranslated FOREIGN KEY ([company_id]) REFERENCES [bepway].[Company] ([id])
);

CREATE TABLE [bepway].[Creation]
(
	[id] [numeric] NOT NULL IDENTITY(0,1),
	[creationDate] [date] NOT NULL,
	[user_id] [nvarchar] NOT NULL,
	[company_id] [numeric] NOT NULL,
	CONSTRAINT creationKey PRIMARY KEY ([id]),
	CONSTRAINT creationUser FOREIGN KEY ([user_id]) REFERENCES [bepway].[User] ([login]),
	CONSTRAINT creationCompany FOREIGN KEY ([company_id]) REFERENCES [bepway].[Company] ([id])
);

CREATE TABLE [bepway].[Audit]
(
	[id] [numeric] NOT NULL IDENTITY(0,1),
	[editDate] [date] NOT NULL,
	[user_id] [nvarchar] NOT NULL,
	[company_id] [numeric] NOT NULL, 
	CONSTRAINT auditKey PRIMARY KEY ([id]),
	CONSTRAINT auditUser FOREIGN KEY ([user_id]) REFERENCES [bepway].[User] ([login]),
	CONSTRAINT auditCompany FOREIGN KEY ([company_id]) REFERENCES [bepway].[Company] ([id])
);