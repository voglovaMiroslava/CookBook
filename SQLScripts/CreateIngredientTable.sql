-- Written for DerbyDB 10.12.1.1
CREATE TABLE INGREDIENT (
    ID BIGINT primary key generated always as identity,
    NAME VARCHAR(255),
    LOWERNAME VARCHAR(255) unique
)