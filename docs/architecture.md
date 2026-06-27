# 系统模块与微服务架构

## 微服务

| 服务 | 端口 | 职责 |
| --- | ---: | --- |
| gateway-service | 9527 | 统一入口、路由转发、JWT 鉴权 |
| auth-service | 9001 | 登录、JWT、用户权限预留 |
| patient-service | 9002 | 患者档案、患者摘要、Redis 缓存预留 |
| appointment-service | 9003 | 预约排班、远程调用患者服务 |
| medical-record-service | 9004 | 电子病历、医嘱 |
| pharmacy-service | 9005 | 药品、库存、发药 |
| billing-service | 9006 | 账单、支付状态 |
| admin-service | 9007 | 字典、配置、审计日志 |

## 演示链路

- 登录认证: `POST http://localhost:9527/api/auth/login`
- 网关鉴权: 登录后携带 `Authorization: Bearer <token>` 访问业务接口
- 网关访问: `GET http://localhost:9527/api/patients/modules`
- 远程调用: `GET http://localhost:9527/api/appointments/demo/remote-patient/1`
- 注册中心: 观察 Nacos 服务列表中各服务上线和下线
- 配置中心: 将 `infra/nacos/config/*.yml` 中的配置导入 Nacos，修改后观察服务配置变化

## 基础设施

- MySQL: `localhost:3306`
- Redis: `localhost:6379`
- Nacos: `http://localhost:8848/nacos`

启动命令：

```powershell
docker compose up -d mysql redis nacos
```
