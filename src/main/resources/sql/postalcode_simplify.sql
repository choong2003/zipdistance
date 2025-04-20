INSERT INTO postcodelatlng (id,postcode,latitude,longitude) VALUES (1, 'AB10 1XG', '57.144165','-2.114848');
INSERT INTO postcodelatlng (id,postcode,latitude,longitude) VALUES (2, 'AB10 6RN', '57.137880','-2.121487');
INSERT INTO postcodelatlng (id,postcode,latitude,longitude) VALUES (3, 'AB10 7JB', '57.124274','-2.127190');
INSERT INTO postcodelatlng (id,postcode,latitude,longitude) VALUES (4, 'AB11 5QN', '57.142701','-2.093295');
INSERT INTO postcodelatlng (id,postcode,latitude,longitude) VALUES (5, 'AB11 6UL', '57.137547','-2.112233');
INSERT INTO postcodelatlng (id,postcode,latitude,longitude) VALUES (6, 'AB11 8RQ', '57.135978','-2.072115');
INSERT INTO postcodelatlng (id,postcode,latitude,longitude) VALUES (7, 'AB12 3FJ', '57.098003','-2.077438');
INSERT INTO postcodelatlng (id,postcode,latitude,longitude) VALUES (8, 'AB12 4NA', '57.064273','-2.130018');
INSERT INTO postcodelatlng (id,postcode,latitude,longitude) VALUES (9, 'AB12 5GL', '57.081938','-2.246567');
INSERT INTO postcodelatlng (id,postcode,latitude,longitude) VALUES (10, 'AB12 9SP', '57.148707','-2.097806');

--Reset the sequence to current max
--SELECT setval('postcodelatlng_id_seq', (SELECT MAX(id) FROM postcodelatlng));
