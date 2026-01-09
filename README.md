# Library Shop

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

## Packaging and running the application

The application can be packaged using:
```shell script
./mvnw package
```
It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:
```shell script
./mvnw package -Dquarkus.package.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

Manually create the database for local testing (with initialization file) ...

```shell script
podman run -d --name pg-library-shop -p 5432:5432 \
    -e POSTGRES_USER=quarkus -e POSTGRES_PASSWORD=quarkus -e POSTGRES_DATABASE=quarkus \
    -v $PWD/src/main/database:/docker-entrypoint-initdb.d:ro,z \
    docker.io/library/postgres:17
```

## Creating a native executable

You can create a native executable using:
```shell script
./mvnw package -Dnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:
```shell script
./mvnw package -Dnative -Dquarkus.native.container-build=true -Dquarkus.native.container-runtime=podman
```

You can then execute your native executable with: `./target/library-shop-1.0.0-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/maven-tooling.


## Creating a Container image with your application

### Create a Container image for the Java Virtual Machine

#### Manually

To create the container image review the file [Containerfile.jvm](./src/main/docker/Containerfile.jvm)

```Containerfile
FROM registry.access.redhat.com/ubi9/openjdk-21-runtime:1.24

ENV LANGUAGE='en_US:en'


# We make four distinct layers so if there are application changes the library-shop layers can be re-used
COPY --chown=185 target/quarkus-app/lib/ /deployments/lib/
COPY --chown=185 target/quarkus-app/*.jar /deployments/
COPY --chown=185 target/quarkus-app/app/ /deployments/app/
COPY --chown=185 target/quarkus-app/quarkus/ /deployments/quarkus/

EXPOSE 8080
USER 185
ENV JAVA_OPTS_APPEND="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"
ENV JAVA_APP_JAR="/deployments/quarkus-run.jar"

ENTRYPOINT [ "/opt/jboss/container/java/run/run-java.sh" ]
```

Execute the command :

```sh
podman build -f ./src/main/docker/Containerfile.jvm -t rha/library-shop:1.0.0 .
```

#### Via Quarkus Building Container Extension

Using jib (quarkus-container-image-jib) extension

```shell script
./mvnw package -Pjib
```

Check the image

```shell script
podman run --rm -it -e DATABASE_HOST=alumno -p 8080:8080 rha/library-shop-jib:1.0.0
```

Using podman (quarkus-container-image-podman) extension

```shell script
./mvnw package -Ppodman
```

Check the image

```shell script
podman run --rm -it -e DATABASE_HOST=alumno -p 8080:8080 rha/library-shop-podman:1.0.0
```

Or, using docker (quarkus-container-image-docker) extension

```shell script
./mvnw package -Pdocker
```

Check the image

```shell script
podman run --rm -it -e DATABASE_HOST=alumno -p 8080:8080 rha/library-shop-docker:1.0.0
```


### Create a Container image for the native executable

```Containerfile
FROM registry.access.redhat.com/ubi9/ubi-micro:9.5-1733126338
WORKDIR /work/
RUN chown 1001 /work \
    && chmod "g+rwX" /work \
    && chown 1001:root /work
COPY --chown=1001:root target/*-runner /work/application

EXPOSE 8080
USER 1001

CMD ["./application", "-Dquarkus.http.host=0.0.0.0"]
```

## Deploying application to OpenShift Platform

### Development using JKube openshift-maven-plugin

Launch an application build

```mvn package oc:build -DskipTests -Djkube.docker.verbose```

Launch the deploy

```mvn package oc:resource oc:apply -DskipTests```

Launch a rollout

```mvn oc:rollout```

## Library Shop Application API

Check the application OpenAPI specification

http://localhost:8080/q/swagger-ui

Create a book

```shell script
curl -i -X POST http://localhost:8080/library -H "Content-type: application/json" -d '{"title":"The Difference Engine","year":1990,"isbn":"0-575-04762-3","price":12.0,"authors":[{"name":"William Gibson"},{"name":"Bruce Sterling"}]}'
```

Get a book by Id

```shell script
curl -s http://localhost:8080/library/1 | jq
```

Update the book price

```shell script
curl -i -X PUT http://localhost:8080/library/1/price\?price\=15.00
```

Paged list all the books

```shell script
curl -s http://localhost:8080/library/ | jq
```

## More Quarkus: Related Guides

- REST resources for Hibernate ORM with Panache ([guide](https://quarkus.io/guides/rest-data-panache)): Generate Jakarta REST resources for your Hibernate Panache entities and repositories
- SmallRye OpenAPI ([guide](https://quarkus.io/guides/openapi-swaggerui)): Document your REST APIs with OpenAPI - comes with Swagger UI
- REST Jackson ([guide](https://quarkus.io/guides/rest#json-serialisation)): Jackson serialization support for Quarkus REST. This extension is not compatible with the quarkus-resteasy extension, or any of the extensions that depend on it
- SmallRye Health ([guide](https://quarkus.io/guides/smallrye-health)): Monitor service health
- JDBC Driver - PostgreSQL ([guide](https://quarkus.io/guides/datasource)): Connect to the PostgreSQL database via JDBC

## Provided Code

### REST Data with Panache

Generating Jakarta REST resources with Panache

[Related guide section...](https://quarkus.io/guides/rest-data-panache)


### REST

Easily start your REST Web Services

[Related guide section...](https://quarkus.io/guides/getting-started-reactive#reactive-jax-rs-resources)

### SmallRye Health

Monitor your application's health using SmallRye Health

[Related guide section...](https://quarkus.io/guides/smallrye-health)