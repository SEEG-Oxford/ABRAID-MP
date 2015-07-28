-- Fix some incorrect mappings between country and admin unit (extent)
-- Copyright (c) 2015 University of Oxford

INSERT INTO admin_unit_country (admin_unit_gaul_code, country_gaul_code) VALUES (926, 53); -- Tianjing Shi, China
INSERT INTO admin_unit_country (admin_unit_gaul_code, country_gaul_code) VALUES (930, 53); -- Zhejiang Sheng, China
DELETE FROM admin_unit_country WHERE admin_unit_gaul_code=2509; -- Kaliningradskaya Oblast, Russian Federation

-- Mark custom russia shape as russia & custom canada shape as canada
INSERT INTO admin_unit_country (admin_unit_gaul_code, country_gaul_code) VALUES (46, 46); 
INSERT INTO admin_unit_country (admin_unit_gaul_code, country_gaul_code) VALUES (204001, 204); 