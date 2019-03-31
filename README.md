# logAnalizer
Spring batch application for events log file analysis. 

Use next command line params:
--filePath
--chunkSize
--alertEventInterval

Example:
D:\Projects\logAnalizer\build\libs>java -jar logAnalizer-1.0-SNAPSHOT.jar --filePath=D:/Temp/json.json --chunkSize=100 --alertEventInterval=1

Result in console:

2019-03-31 15:58:04.914  Found alert event < eventId: scsmbstgra | duration: 5  | type: APPLICATION_LOG | host: 12345 >
2019-03-31 15:58:04.915  Found alert event < eventId: scsmbstgrc | duration: 8  | type:  | host:  >
