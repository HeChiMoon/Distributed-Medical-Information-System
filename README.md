# Distributed Medical Information System

基于 Spring Boot 3 + Spring Cloud + Vue 3 的分布式医疗信息化系统。

## Current Scope

- `backend/dmis-common`: 统一响应、分页模型、错误码、业务异常、参数校验异常处理、审计字段
- `backend/gateway-service`: API 网关、路由转发、JWT 鉴权过滤器
- `backend/auth-service`: 登录认证基础接口，返回 JWT
- `backend/patient-service`: 患者服务基础接口，含内部远程调用演示接口
- `backend/appointment-service`: 预约服务基础接口，含 Feign 调用患者服务示例
- `backend/medical-record-service`: 电子病历与医嘱服务入口
- `backend/pharmacy-service`: 药房与库存服务入口
- `backend/billing-service`: 电子账单服务入口
- `backend/admin-service`: 系统管理服务入口
- `frontend`: Vue 3 医疗后台管理界面骨架
- `infra`: MySQL、Redis、Nacos 与数据库初始化脚本

## Local Startup

1. 启动基础设施：

```powershell
docker compose up -d mysql redis nacos
```

2. 启动后端服务：

- `gateway-service`: `9527`
- `auth-service`: `9001`
- `patient-service`: `9002`
- `appointment-service`: `9003`
- `medical-record-service`: `9004`
- `pharmacy-service`: `9005`
- `billing-service`: `9006`
- `admin-service`: `9007`

3. 启动前端：

```powershell
cd frontend
npm install
npm run dev
```

## Verification

```powershell
mvn test
cd frontend
npm run build
```

## Demo APIs

- Login: `POST http://localhost:9527/api/auth/login`
- Gateway protected route: `GET http://localhost:9527/api/patients/modules`
- Feign demo: `GET http://localhost:9527/api/appointments/demo/remote-patient/1`
- Swagger UI example: `http://localhost:9001/swagger-ui.html`

## Remote Repository

GitHub: https://github.com/HeChiMoon/Distributed-Medical-Information-System
