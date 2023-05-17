FROM eclipse-temurin:17

WORKDIR /

RUN apk update && apk add bash

COPY infra/wait-for-it.sh wait-for-it.sh

COPY target/app-standalone.jar app-standalone.jar
EXPOSE 3000

RUN chmod +x wait-for-it.sh

CMD ["sh", "-c", "./wait-for-it.sh --timeout=90 $DB_HOST:5432 -- java -jar app-standalone.jar"]
