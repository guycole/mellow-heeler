#
# Title: file_helper.py
# Description: file helper class
# Development Environment: Ubuntu 22.04.5 LTS/python 3.10.12
# Author: G.S. Cole (guycole at gmail dot com)
#
from asyncio.log import logger
import datetime
import json

class FileHelper:
    file_name = ""
    json_preamble = {}
    raw_buffer = []

    def load_log(self) -> dict[str, any]:
        if self.json_preamble["version"] == 1:
            epoch = self.json_preamble["zTime"]
            utc_dt = datetime.datetime.fromtimestamp(epoch, tz=datetime.timezone.utc)

            return {
                "file_name": self.file_name,
                "file_time": utc_dt,
                "file_type": f"{self.json_preamble['project']}_{self.json_preamble['version']}",
                "obs_quantity": len(self.json_preamble["wifi"]),
                "platform": self.json_preamble["platform"],
            }
        else:
            logger.error(f"invalid version:{self.json_preamble['version']} for file:{self.file_name}")
            return {}

    def file_reader(self, file_name: str) -> bool:
        self.file_name = file_name
        self.json_preamble = {}

        try:            
            with open(file_name, "r", encoding="utf-8") as in_file:
                self.raw_buffer = in_file.readlines()
                if len(self.raw_buffer) < 3:
                    self.raw_buffer = []
                    return False
        except Exception as error:
            return False

        return True

    def converter(self, file_name: str) -> bool:
        if self.file_reader(file_name) is False:
            return False

        try:
            self.json_preamble = json.loads(self.raw_buffer[0])
        except Exception as error:
            return False

        return True

# ;;; Local Variables: ***
# ;;; mode:python ***
# ;;; End: ***
