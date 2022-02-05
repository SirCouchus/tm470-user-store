This repository is the SCIM-compliant User Storage service developed as part of my TM470 OU final year module.
-

This project consists of three maven modules:
- `api`
- `service`

api
-
The `api` module is responsible for defining and generating the OpenAPI specifications for the service's API and models.

service
-
The `service` module is responsible for directing REST requests via its REST controller and forwarding to the relevant business logic classes.