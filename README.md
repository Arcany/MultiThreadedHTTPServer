# MultiThreadedHTTPServer

## Description
This app sums up numbers given to it in a post request.

- WAR file is in the webapp/target/ folder
- Run JettyServer in main/java/ to start the server.
- It accepts a post request with a raw body, example: `curl -d 6 http://localhost:1337`
- To get the sum use end keyword and also add ID, example: `curl -d "end X" http://localhost:1337`
