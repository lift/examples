You need to register a connect application with facebook and get your app key and secret at: http://www.facebook.com/developers/

Start with: 
MAVEN_OPTS="-Dcom.facebook.api_key=key -Dcom.facebook.secret=secret" mvn jetty:run