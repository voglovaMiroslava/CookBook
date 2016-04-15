-- Written for DerbyDB 10.12.1.1
CREATE TABLE INGREDIENTAMOUNT (
    ID BIGINT primary key generated always as identity,
    RECIPEID BIGINT CONSTRAINT ingredientamount_recipeid_ref REFERENCES RECIPE(ID),
    INGREDIENTID BIGINT CONSTRAINT ingredientamount_ingredientid_ref REFERENCES INGREDIENT(ID),
    AMOUNT VARCHAR(255)
)