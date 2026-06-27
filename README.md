# Distributed Medical Information System

DMIS 是一套前后端结合的分布式医疗信息化系统演示项目。后端基于 Spring Boot 3、Spring Cloud、Spring Cloud Alibaba、Nacos、Redis、MySQL、OpenFeign 和 Spring Cloud Gateway；前端基于 Vue 3 + Vite，实现登录认证、患者管理、预约排班、电子病历、医嘱、药房库存、电子账单、检索中心和联调验证等真实业务交互。

## 1. 交付范围

- 后端微服务：`gateway-service`、`auth-service`、`patient-service`、`appointment-service`、`medical-record-service`、`pharmacy-service`、`billing-service`、`admin-service`。
- 前端工作台：独立登录页、业务工作台、患者档案中心、预约排班、病历医嘱、药房库存、电子账单、检索中心、联调验证面板。
- 基础设施：MySQL、Redis、Nacos 注册中心、Nacos 配置中心。
- 接口文档：各服务 Swagger UI + `docs/api-progress.md` 汇总接口清单。
- 演示数据：`infra/mysql/init/08-demo-data.sql` 内置患者、预约、病历、医嘱、药品、账单、日志等默认数据。
- 验收材料：PRD、架构说明、里程碑文档、演示运行手册、验收截图、视频录制讲稿。

## 2. 环境要求

- JDK 17+
- Maven 3.9+
- Node.js 18+
- Docker Desktop
- PowerShell

如果 Docker 启动 MySQL 时提示拉取 `mysql:8.4` 失败，但本地已有 `mysql:8.4.0` 镜像，可以执行：

```powershell
docker tag mysql:8.4.0 mysql:8.4
```

## 3. 日常一键启动

在项目根目录执行：

```powershell
.\scripts\start-dev.ps1
```

脚本会自动完成：

- 启动 MySQL、Redis、Nacos。
- 导入 Nacos 演示配置。
- 执行 `mvn -DskipTests package` 打包后端。
- 后台启动 8 个 Spring Boot 服务。
- 启动 Vue 3 前端。
- 将运行日志输出到 `logs/dev/`。

启动完成后访问：

```text
前端工作台: http://localhost:5173
API 网关:   http://localhost:9527
Nacos:      http://localhost:8848/nacos
```

登录账号：

```text
admin / admin123
```

常用参数：

```powershell
# 只启动后端和前端，不重启 Docker 基础设施
.\scripts\start-dev.ps1 -SkipInfrastructure

# 已经打包过时跳过 Maven package
.\scripts\start-dev.ps1 -SkipBuild

# 只启动基础设施
.\scripts\start-dev.ps1 -SkipBackend -SkipFrontend
```

## 4. 日常一键关闭

```powershell
.\scripts\stop-dev.ps1
```

脚本会停止：

- 前端 Vite 进程。
- DMIS 后端 Java 服务。
- Docker 基础设施容器。

如果你的本机 MySQL 也占用了 `3306`，并且确认它只用于本项目，可以执行：

```powershell
.\scripts\stop-dev.ps1 -StopLocalMySql
```

如果只想关闭前后端、保留 Docker 中间件：

```powershell
.\scripts\stop-dev.ps1 -KeepInfrastructure
```

## 5. 手动启动方式

基础设施：

```powershell
docker compose up -d mysql redis nacos
.\scripts\import-nacos-config.ps1
```

后端打包：

```powershell
mvn -DskipTests package
```

分别启动后端服务：

```powershell
java -jar backend\auth-service\target\auth-service-0.1.0-SNAPSHOT.jar
java -jar backend\patient-service\target\patient-service-0.1.0-SNAPSHOT.jar
java -jar backend\appointment-service\target\appointment-service-0.1.0-SNAPSHOT.jar
java -jar backend\medical-record-service\target\medical-record-service-0.1.0-SNAPSHOT.jar
java -jar backend\pharmacy-service\target\pharmacy-service-0.1.0-SNAPSHOT.jar
java -jar backend\billing-service\target\billing-service-0.1.0-SNAPSHOT.jar
java -jar backend\admin-service\target\admin-service-0.1.0-SNAPSHOT.jar
java -jar backend\gateway-service\target\gateway-service-0.1.0-SNAPSHOT.jar
```

前端：

```powershell
cd frontend
npm install
npm run dev
```

## 6. 端口说明

| 组件 | 端口 | 说明 |
| --- | ---: | --- |
| Vue 前端 | 5173 | 业务工作台 |
| API Gateway | 9527 | 统一 API 入口 |
| auth-service | 9001 | 登录认证、JWT |
| patient-service | 9002 | 患者档案、Redis 缓存 |
| appointment-service | 9003 | 科室、医生、排班、预约 |
| medical-record-service | 9004 | 电子病历、医嘱 |
| pharmacy-service | 9005 | 药品、库存、发药 |
| billing-service | 9006 | 账单、支付 |
| admin-service | 9007 | 字典、操作日志 |
| MySQL | 3306 | 分库数据存储 |
| Redis | 6379 | 患者详情缓存 |
| Nacos | 8848 / 9848 | 注册中心、配置中心 |

## 7. 默认演示数据

首次创建 MySQL Docker 卷时，`infra/mysql/init/*.sql` 会自动建库、建表并导入默认数据。默认数据包括：

- 患者：王晓晨、李建国。
- 科室/医生：全科门诊/张敏，心内科/陈立。
- 预约：已完成和待确认各一条。
- 病历/医嘱：普通门诊病历、高血压复诊病历、药品医嘱和检查医嘱。
- 药品库存：布洛芬、氨氯地平、维生素C。
- 账单：已支付和待支付各一条。
- 操作日志：患者、预约、账单演示日志。

如果数据库卷已经存在，Docker 不会重复执行初始化脚本。此时可手动导入：

```powershell
cmd /c "docker exec -i dmis-mysql mysql --user=dmis --password=dmis123456 --default-character-set=utf8mb4 < infra\mysql\init\08-demo-data.sql"
```

也可以重置 Docker MySQL 数据卷后重新初始化：

```powershell
docker compose down -v
docker compose up -d mysql redis nacos
```

## 8. 验证命令

完整测试：

```powershell
mvn test
cd frontend
npm run build
```

项目自带验收脚本：

```powershell
.\scripts\verify-milestone4.ps1
```

网关联调冒烟脚本：

```powershell
.\scripts\demo-api.ps1
```

## 9. 接口文档

服务启动后打开 Swagger UI：

```text
http://localhost:9001/swagger-ui.html
http://localhost:9002/swagger-ui.html
http://localhost:9003/swagger-ui.html
http://localhost:9004/swagger-ui.html
http://localhost:9005/swagger-ui.html
http://localhost:9006/swagger-ui.html
http://localhost:9007/swagger-ui.html
```

接口汇总文档：

```text
docs/api-progress.md
```

## 10. 主要演示路径

建议录制 demo 视频时按以下顺序：

1. 展示 README，说明如何启动系统。
2. 打开 Nacos，展示服务注册列表。
3. 打开前端登录页，使用 `admin / admin123` 登录。
4. 展示患者管理：搜索、查看默认患者、编辑表单、Redis 缓存按钮。
5. 展示预约排班、病历医嘱、药房库存、电子账单。
6. 展示检索中心，按患者 ID 聚合查询全链路数据。
7. 展示联调验证面板，说明网关、JWT、Redis、Feign、Nacos 配置中心。
8. 打开 Swagger 或 `docs/api-progress.md`，说明接口数量和文档。
9. 展示 `mvn test`、`npm run build`、Git 提交记录。

完整视频讲稿见：

```text
docs/demo-video-script.md
```

## 11. 重要文档

- PRD：`docs/frontend-interaction-prd.md`
- 架构说明：`docs/architecture.md`
- 数据库设计：`docs/database-design.md`
- 接口清单：`docs/api-progress.md`
- 演示手册：`docs/demo-runbook.md`
- 视频脚本：`docs/demo-video-script.md`
- 最终验收：`docs/final-acceptance.md`
- 交付总结：`docs/delivery-summary.md`

## 12. GitHub

远程仓库：

```text
https://github.com/HeChiMoon/Distributed-Medical-Information-System
```
