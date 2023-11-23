parse and load heeler files

hound_1 = android files, well formatted json
heeler_1 = rPi files, not so well formatted

select observation.geoloc_id, observation.fix_time_ms, observation.wap_id, geoloc.device from observation inner join geoloc on observation.geoloc_id = geoloc.id;

select observation.geoloc_id, observation.fix_time_ms, observation.wap_id, geoloc.device from observation inner join geoloc on observation.geoloc_id = geoloc.id where geoloc.device = 'android1';
