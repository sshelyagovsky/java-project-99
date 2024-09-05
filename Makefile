setup:
	./gradlew wrapper --gradle-version 8.7
	./gradlew build

clean:
	./gradlew clean

build:
	./gradlew clean test build

lint:
	./gradlew checkstyleMain checkstyleTest

report:
	./gradlew jacocoTestReport

run-dist:
	./build/install/app/bin/app