DO $$
DECLARE
  owner CONSTANT bigint := 2304252607762632704;
  slot smallint;
  new_id bigint := 900000000000000001;
  dex_ids int[] := ARRAY[6, 9, 151];
  dex int;
BEGIN
  FOR dex IN SELECT unnest(dex_ids) LOOP
    SELECT s INTO slot
    FROM generate_series(0,5) AS s
    WHERE NOT EXISTS (
      SELECT 1 FROM pokemon p
      WHERE p.owner_id = owner
        AND p.container = 'PARTY'
        AND p.container_slot = s
    )
    LIMIT 1;

    IF slot IS NULL THEN
      RAISE EXCEPTION 'No free party slot';
    END IF;

    INSERT INTO pokemon (
      id, owner_id, container, container_slot, dex_id, seed, ot, nickname,
      pokemon_level, hp, xp, caught_at
    ) VALUES (
      new_id, owner, 'PARTY', slot, dex, 0, 'fasfsasfa', '', 40, 5, 0, now()
    );

    new_id := new_id + 1;
  END LOOP;
END $$;
