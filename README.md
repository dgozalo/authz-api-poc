# API Authz POC Project

Quarkus REST API secured with custom ContainerRequestFilters that request authorization
decisions to OPA.

It also includes a way to query data from ORY Keto in order to create a bundle for OPA to
decide internally to avoid roundtrips.

## <span style="color:red">:: POC CODE - CRINGE POSSIBLE ::</span>

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

## Authentication

This application uses basic auth configured in `src/main/resources/application.properties`

It comes with a pre-configured user: `demouser` with password `123123`.

New users can be added with the following format:

```
quarkus.security.users.embedded.users.<add_user_here>=123123
quarkus.security.users.embedded.roles.<add_user_here>=user
```

## Packaging and running the application

The application can be packaged using Docker:
```shell script
docker build -f src/main/docker/Dockerfile.multistage -t quarkus/auth_poc_api_native
```

This will create a Docker image with a native executable of the application.

The application is now runnable using `docker run -p 8081:8081 -e KETO_URL=keto:4466 -e OPA_URL=opa:8181 docker.io/quarkus/auth_poc_api_native`.