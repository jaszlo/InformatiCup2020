All:
	mvn clean
	mvn package
	java -cp target/ic_local_webservice-20.jar app.App	
