CREATE TABLE users (
   telegramID  INT PRIMARY KEY
  ,firstName   VARCHAR(30) NULL
  ,secondName  VARCHAR(30) NULL
  ,login       VARCHAR(30) NULL
);

CREATE TABLE transact (
   transactID  SERIAL PRIMARY KEY
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

CREATE INDEX findTransact ON transact (fromID, toID);

CREATE INDEX findTransactWithData ON transact (fromID, toID,data);

