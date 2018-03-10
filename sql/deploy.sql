CREATE TABLE users (
   telegramID  INT PRIMARY KEY
  ,firstName   VARCHAR(30) NOT NULL
  ,secondName  VARCHAR(30) NULL
  ,login       VARCHAR(30) NOT NULL UNIQUE
);

CREATE TABLE transact (
   transactID  BIGSERIAL PRIMARY KEY
  ,fromID      INT REFERENCES users(telegramID)
  ,toID        INT REFERENCES users(telegramID)
  ,amount      INT NOT NULL CHECK (amount > 0)
  ,description VARCHAR(30)
  ,isLegal     BOOLEAN NOT NULL DEFAULT '0'
  ,data        TIMESTAMP NOT NULL
);

CREATE TABLE total (
   fromID      INT REFERENCES users(telegramID)
  ,toID        INT REFERENCES users(telegramID)
  ,total       INT NOT NULL
  ,UNIQUE (fromID,toID)
);

CREATE TABLE Groups(
   groupID     SERIAL PRIMARY KEY
  ,groupName   VARCHAR(30) NOT NULL UNIQUE
  ,adminID     INT REFERENCES users(telegramID)
);

CREATE TABLE UserInGroups(
   userID      INT REFERENCES users(telegramID)
  ,groupID     INT REFERENCES Groups(groupID)
  ,UNIQUE (userID,groupID)
);

CREATE INDEX findTransact ON transact (fromID, toID);

CREATE INDEX findTransactWithData ON transact (fromID, toID,data);


