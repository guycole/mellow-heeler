#
# Title: parser1.py
# Description:
# Development Environment: OS X 12.6.9/Python 3.11.5
# Author: G.S. Cole (guycole at gmail dot com)
#
import json
import os
import sys
import time
import typing
import yaml

from yaml.loader import SafeLoader

#from sqlalchemy import create_engine
#from sqlalchemy.orm import sessionmaker

class HeelerParser:
    def observation_writer(self, file_name:str, project:int, version: int, geo_loc:typing.Dict):
        print("observation_writer")

        obs = {}
        obs['accuracy'] = geo_loc['accuracy']
        obs['altitude'] = geo_loc['altitude']
        obs['fix_time_ms'] = geo_loc['fixTimeMs']
        obs['latitude'] = geo_loc['latitude']
        obs['longitude'] = geo_loc['longitude']
        obs['observation_key'] = file_name
        obs['observed_at'] = time.strftime('%Y-%m-%d %H:%M:%S', time.gmtime(geo_loc['fixTimeMs']/1000))
        obs['project'] = project
        obs['version'] = version

        print(obs)
        
    def sample_cooked_writer(self, file_name, observation:typing.Dict):
        print("sample_cooked_writer")

        # select cooked sample
        # ensure ssid, freq, capability match
        # lat/long distance?
        #
        sample_cooked = None

        if sample_cooked is None:
            print("insert cooked sample")

            sample_cooked = {}
            sample_cooked['bssid'] = observation['bssid']
            sample_cooked['capability'] = observation['capability']
            sample_cooked['frequency'] = observation['frequency']
            sample_cooked['latitude'] = observation['latitude']
            sample_cooked['longitude'] = observation['longitude']
            sample_cooked['observed_first'] = observation['observed_at']
            sample_cooked['observed_last'] = observation['observed_at']
            sample_cooked['population'] = 1
            sample_cooked['ssid'] = observation['ssid']
        else:
            print("update cooked sample")

            sample_cooked['observed_last'] = observation['observed_at']
            sample_cooked['population'] = sample_cooked['population'] + 1

            # average latitude and longitude

    def sample_raw_writer(self, file_name:str, observation:typing.Dict):
        print("sample_raw_writer")

        sample_raw = {}
        sample_raw['bssid'] = observation['bssid']
        sample_raw['capability'] = observation['capability']
        sample_raw['frequency'] = observation['frequency']
        sample_raw['level'] = observation['level']
        sample_raw['ssid'] = observation['ssid']

        print(sample_raw)

    def heeler_v1(self, file_name:str, raw_load:typing.Dict):
        print("heeler parser v1")

    def hound_v1(self, file_name:str, raw_load:typing.Dict):
        print("hound parser v1")

        project = 1
        version = 1

        if 'geoLoc' in raw_load:
            geo_loc = raw_load['geoLoc']
        else:
            print("skipping bad observation missing geoLoc:", file_name)
            return

        observations = None
        if 'wiFi' in raw_load:
            observations = raw_load['wiFi']
            print("observation population:", len(observations), " from:", file_name)
        
        if observations is None:
            print("skipping bad observation missing wiFi:", file_name)
            return

        if len(observations) < 1:
            print("skipping bad observation empty wiFi:", file_name)
            return

        self.observation_writer(file_name, project, version, geo_loc)

        for observation in observations:
            self.sample_raw_writer(file_name, observation)
            self.sample_cooked_writer(file_name, observation)

    def file_loader(self, file_name:str) -> typing.Dict:
        with open(file_name, 'r') as infile:
            try:
                raw_load = json.load(infile)
            except:
                print("parse error")

        project = None
        if 'project' in raw_load:
            project = raw_load['project']  

        version = None
        if 'version' in raw_load:
            version = raw_load['version']

        if project == 'heeler':
            if version == 1:
                self.heeler_v1(file_name, raw_load)
            else:
                print("skipping unknown heeler version:", version)
        elif project == 'hound':
            if version == 1:
                self.hound_v1(file_name, raw_load)
            else:
                print("skipping unknown hound version:", version)
        else:
            print("skipping unknown:", project, ":", version)

print('start loader')

#
# argv[1] = configuration filename
#
if __name__ == '__main__':
    if len(sys.argv) > 1:
        file_name = sys.argv[1]
    else:
        file_name = "config.yaml"

    with open(file_name, 'r') as stream:
        try:
            configuration = yaml.load(stream, Loader=SafeLoader)
        except yaml.YAMLError as exc:
            print(exc)

    import_dir = configuration['importDir']

    heeler_loader = HeelerLoader()

    os.chdir(import_dir)
    targets = os.listdir('.')
    print(len(targets), "files noted")
    for target in targets:
        heeler_loader.file_loader(target)

#    db_data_base = configuration['dbDataBase']
#    db_host_name = configuration['dbHostName']
#    db_pass_word = configuration['dbPassWord']
#    db_user_name = configuration['dbUserName']

print('stop loader')

#;;; Local Variables: ***
#;;; mode:python ***
#;;; End: ***