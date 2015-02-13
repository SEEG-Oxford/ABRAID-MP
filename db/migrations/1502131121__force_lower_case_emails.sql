-- Updates the database schema to add lower case constraint to the email field of an expert.
-- Copyright (c) 2014 University of Oxford

UPDATE expert SET email=LOWER(email);
ALTER TABLE expert ADD CONSTRAINT ck_expert_email CHECK (email = LOWER(email));