-- public.confluence_vector definition

-- Drop table

-- DROP TABLE public.confluence_vector;

CREATE DATABASE vector_db;
\c vector_db;
CREATE EXTENSION IF NOT EXISTS vector;
CREATE TABLE IF NOT EXISTS public.confluence_vector (
                                          id serial4 NOT NULL,
                                          root_id int8 NOT NULL,
                                          metadata text NULL,
                                          vectors public.vector NULL,
                                          CONSTRAINT confluence_vector_pkey PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS public.confluence_space (
                                                       id serial4 NOT NULL,
                                                       space_id int8 NOT NULL,
                                                       space_key text NOT NULL,
                                                       space_name text NOT NULL,
                                                       CONSTRAINT confluence_space_pkey PRIMARY KEY (id)
);