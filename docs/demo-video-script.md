# DMIS Demo 演示视频脚本

## 1. 视频目标

本视频用于项目交付演示，需要清楚说明：

- 系统使用了什么技术。
- 项目整体架构是什么。
- 系统实现了哪些业务功能。
- 如何启动前端、后端和基础设施。
- 如何通过页面演示登录认证、网关、注册发现、Redis、Feign、配置中心、接口文档和单元测试。

建议视频时长：8 到 12 分钟。

## 2. 录制前准备

在项目根目录执行：

```powershell
.\scripts\start-dev.ps1
```

确认以下地址可访问：

```text
前端: http://localhost:5173
网关: http://localhost:9527
Nacos: http://localhost:8848/nacos
Swagger: http://localhost:9002/swagger-ui.html
```

建议提前打开这些页面：

- `README.md`
- `docs/architecture.md`
- `docs/api-progress.md`
- `docs/frontend-interaction-prd.md`
- 前端工作台登录页
- Nacos 控制台
- Swagger UI

## 3. 视频结构

### 3.1 开场介绍

参考讲解词：

> 大家好，这个项目是 DMIS 分布式医疗信息化系统。系统后端基于 Spring Boot 3 和 Spring Cloud 微服务体系，前端使用 Vue 3。项目面向医院信息化场景，实现了患者管理、预约排班、电子病历、医嘱、药品库存、药房发药、电子账单、检索中心等功能，并集成了 API 网关、JWT 登录认证、Nacos 注册发现、Nacos 配置中心、Redis 缓存、Feign 服务间调用和 Swagger 接口文档。

画面建议：

- 展示 README 标题和项目目录。
- 简单扫过 `backend`、`frontend`、`infra`、`docs`、`scripts`。

### 3.2 技术栈说明

参考讲解词：

> 技术栈上，前端使用 Vue 3 和 Vite；后端使用 Spring Boot 3、Spring Cloud Gateway、OpenFeign、Spring Data JPA；服务治理使用 Nacos；缓存使用 Redis；数据库使用 MySQL，并按照服务拆分多个业务库。每个服务都集成 SpringDoc Swagger UI，用于展示接口文档；后端使用 JUnit 和 Mockito 编写单元测试。

画面建议：

- 打开 `docs/architecture.md` 的技术栈表格。
- 展示 `pom.xml` 中 Spring Boot、Spring Cloud、Spring Cloud Alibaba 版本。

### 3.3 架构说明

参考讲解词：

> 系统采用前后端分离和微服务架构。前端所有请求统一访问 `/api/**`，由 Vite 代理到 9527 端口的 API 网关。网关负责 JWT 鉴权和路由转发。业务服务启动后注册到 Nacos，服务之间通过 Feign 进行远程调用。每个业务服务拥有独立数据库，患者服务还使用 Redis 实现患者详情缓存。

画面建议：

- 展示 `docs/architecture.md` Mermaid 架构图。
- 展示 Nacos 服务列表，说明服务注册。
- 展示端口表。

### 3.4 启动方式说明

参考讲解词：

> 平时开发启动只需要执行 `scripts/start-dev.ps1`，脚本会启动 MySQL、Redis、Nacos，导入 Nacos 配置，打包并启动后端服务，同时启动 Vue 前端。开发结束时执行 `scripts/stop-dev.ps1` 关闭所有服务。

画面建议：

- 展示 README 的“日常一键启动”和“日常一键关闭”。
- 可以展示 `logs/dev` 目录，说明服务日志位置。

### 3.5 登录认证演示

参考讲解词：

> 这里先进入前端系统。系统提供独立登录页，演示账号是 `admin / admin123`。登录成功后，后端 auth-service 会签发 JWT，前端把 Token 保存到本地，后续业务请求都会通过 API 网关并携带 Bearer Token。

操作步骤：

1. 打开 `http://localhost:5173`。
2. 输入 `admin / admin123`。
3. 登录进入工作台。
4. 展示右上角“已登录”和退出按钮。

### 3.6 患者管理演示

参考讲解词：

> 患者管理模块支持患者列表查询、新增、修改、删除和 Redis 缓存演示。这里可以看到系统已经内置了几条默认演示患者。右侧是患者编辑表单，下方是当前患者档案摘要。点击 Redis 缓存按钮，可以看到患者详情缓存使用的 key 和 TTL。

操作步骤：

1. 进入“患者管理”。
2. 展示患者列表默认数据。
3. 点击患者，展示档案摘要。
4. 点击“查看 Redis 缓存策略”。
5. 可点击“填充演示患者”并保存一条新患者。

### 3.7 预约、病历、医嘱演示

参考讲解词：

> 预约排班模块包含科室、医生、排班和预约。创建预约时，预约服务会通过 Feign 调用患者服务校验患者是否存在。病历医嘱模块可以创建电子病历和医嘱，病历会关联患者和医生，也可以关联预约。

操作步骤：

1. 切换“预约排班”，展示科室、医生、排班、预约列表。
2. 切换“病历医嘱”，展示默认病历和医嘱。
3. 说明患者 ID、预约 ID、医生 ID 的关联关系。

### 3.8 药房库存和电子账单演示

参考讲解词：

> 药房库存模块包含药品目录、库存入库、库存查询和发药记录。发药前会远程调用病历服务校验医嘱。电子账单模块支持账单创建、明细记录、支付记录和支付状态变化。

操作步骤：

1. 切换“药房库存”，展示布洛芬、氨氯地平、维生素C等默认药品。
2. 展示库存入库和发药表单。
3. 切换“电子账单”，展示已支付和待支付账单。

### 3.9 检索中心演示

参考讲解词：

> 检索中心用于替代传统系统管理菜单，更贴近演示场景。它可以按患者关键词、药品关键词、患者 ID、日志模块名聚合查询患者、药品、预约、病历、医嘱、账单、发药记录和操作日志，方便演示一名患者的完整业务轨迹。

操作步骤：

1. 进入“检索中心”。
2. 输入患者关键词或药品关键词。
3. 输入患者 ID，例如 `101`。
4. 展示聚合查询结果。

### 3.10 联调验证演示

参考讲解词：

> 联调验证面板用于集中展示分布式能力，包括模块元数据、Redis 缓存、Feign 远程调用和 Nacos 配置中心。这里也可以通过脚本 `scripts/demo-api.ps1` 进行网关冒烟测试。

操作步骤：

1. 进入“联调验证”。
2. 点击运行验证。
3. 展示返回结果。
4. 打开 Nacos 配置中心，说明配置修改后可通过接口读取变化。

### 3.11 接口文档和测试结果

参考讲解词：

> 系统接口文档由 SpringDoc 自动生成，每个服务都可以打开 Swagger UI 查看接口。项目总接口数超过 50 个，汇总文档在 `docs/api-progress.md`。测试方面，后端可以执行 `mvn test`，前端可以执行 `npm run build`，用于验证代码质量和构建结果。

画面建议：

- 打开 `http://localhost:9002/swagger-ui.html`。
- 打开 `docs/api-progress.md`。
- 展示终端运行：

```powershell
mvn test
cd frontend
npm run build
```

### 3.12 Git 提交和远程仓库

参考讲解词：

> 项目也演示了基础 Git 工作流，包括查看状态、提交代码和推送到 GitHub。当前远程仓库地址是 `HeChiMoon/Distributed-Medical-Information-System`。

画面建议：

```powershell
git log --oneline -5
git remote -v
git status --short
```

### 3.13 结尾总结

参考讲解词：

> 总结一下，DMIS 完成了一套基于 Spring Boot 3 和 Vue 3 的分布式医疗信息系统。系统覆盖患者、预约、病历、医嘱、药房、库存、账单等核心业务，并通过网关、JWT、Nacos、Redis、Feign、Swagger 和单元测试体现了分布式系统设计。以上就是本项目的演示。

## 4. 推荐录制清单

- README 启动说明。
- 架构图和技术栈。
- Nacos 服务注册列表。
- 前端登录页和工作台。
- 患者管理 + Redis 缓存演示。
- 预约排班 + 病历医嘱。
- 药房库存 + 电子账单。
- 检索中心聚合查询。
- Swagger 接口文档。
- `mvn test` 和 `npm run build` 结果。
- Git 提交记录和远程仓库。

## 5. 录制注意事项

- 录制前先执行 `.\scripts\start-dev.ps1`，等待所有服务启动完成。
- 如果 Nacos 页面需要登录，默认账号通常是 `nacos / nacos`。
- 如果 MySQL Docker 初始化过旧数据卷，默认演示数据没有出现，可按 README 手动导入 `08-demo-data.sql`。
- 录制完成后执行 `.\scripts\stop-dev.ps1` 关闭设施。
