-- Add outside_geom to disease_extent
-- Copyright (c) 2015 University of Oxford

ALTER TABLE disease_extent ADD COLUMN outside_geom geometry(MULTIPOLYGON, 4326);

UPDATE disease_extent
SET outside_geom = calculated_inverse_geoms.outer_geom
FROM disease_extent AS e
INNER JOIN
(
	SELECT de.disease_group_id as disease_group_id,
	(
		SELECT ST_COLLECT(shapes.geom)
		FROM 
		(
			SELECT (ST_DUMP(COALESCE(t.geom, g.geom))).geom 
			FROM admin_unit_disease_extent_class AS ec 
			LEFT OUTER JOIN admin_unit_global AS g ON ec.global_gaul_code=g.gaul_code 
			LEFT OUTER JOIN admin_unit_tropical AS t ON ec.tropical_gaul_code=t.gaul_code 
			WHERE ec.disease_extent_class IN ('ABSENCE', 'POSSIBLE_ABSENCE', 'UNKNOWN') 
			AND ec.disease_group_id=de.disease_group_id
		) AS shapes
	) AS outer_geom
	FROM disease_extent AS de
	WHERE de.geom IS NOT NULL
) AS calculated_inverse_geoms
ON e.disease_group_id = calculated_inverse_geoms.disease_group_id;
