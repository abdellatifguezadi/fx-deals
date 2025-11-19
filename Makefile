# Phony targets
.PHONY:  clean compile test package build install run run-jar docker-up docker-down docker-logs docker-build db-shell test-coverage test-api dev-setup dev-restart all deploy

# Build targets
clean:
	mvn clean

compile:
	mvn compile

test:
	mvn test

package:
	mvn clean package -DskipTests

build:
	mvn clean compile test package

install:
	mvn clean install

# Run targets
run:
	mvn spring-boot:run

run-jar: package
	java -jar target/fx-deals-importer-0.0.1-SNAPSHOT.jar

# Docker targets
docker-up:
	docker-compose up -d

docker-down:
	docker-compose down

docker-logs:
	docker-compose logs -f app

docker-build: package
	docker-compose build

# Database targets
db-shell:
	docker-compose exec mysql mysql -u root -p deals

# Test targets
test-coverage:
	mvn clean test jacoco:report

test-api:
	./test-api.sh

# Development targets
dev-setup: clean install docker-up

dev-restart: docker-down package docker-up

# All-in-one targets
all: clean test package

deploy: clean test package docker-build docker-up