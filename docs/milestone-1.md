# Milestone 1 Completion Notes

## Completed

- Maven multi-module backend scaffold.
- Spring Cloud Gateway route scaffold.
- Nacos discovery/config placeholders.
- JWT issuing in `auth-service`.
- JWT validation filter in `gateway-service`.
- OpenFeign demo from `appointment-service` to `patient-service`.
- Redis dependencies and cache key plan.
- Shared API response, error codes, business exception, validation exception handling, and audit fields.
- Vue 3 operational dashboard scaffold.
- Docker Compose for MySQL, Redis, and Nacos.
- MySQL initialization scripts for all service schemas.

## How To Verify

```powershell
mvn test
cd frontend
npm run build
docker compose up -d mysql redis nacos
```

## Milestone 2 Entry Criteria

- Infrastructure can be started locally.
- Backend modules compile together.
- Gateway has a clear authentication boundary.
- SQL schema files are present for business service implementation.
