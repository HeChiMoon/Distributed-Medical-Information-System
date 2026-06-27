<script setup>
import {
  Activity,
  BadgeCheck,
  Boxes,
  Braces,
  Cable,
  ClipboardCheck,
  DatabaseZap,
  FileText,
  GitBranch,
  KeyRound,
  Network,
  Pill,
  RadioTower,
  ReceiptText,
  RefreshCw,
  Route,
  ShieldCheck,
  Stethoscope,
  TestTube2,
  UsersRound
} from 'lucide-vue-next'

const businessCards = [
  { title: '患者管理', value: '8 APIs', detail: '档案 CRUD + Redis 缓存', icon: UsersRound, tone: 'mint' },
  { title: '预约排班', value: '15 APIs', detail: '科室、医生、排班、预约', icon: ClipboardCheck, tone: 'sky' },
  { title: '电子病历', value: '10 APIs', detail: '病历 + 医嘱闭环', icon: FileText, tone: 'leaf' },
  { title: '药房库存', value: '11 APIs', detail: '入库、流水、发药扣减', icon: Pill, tone: 'coral' },
  { title: '电子账单', value: '8 APIs', detail: '账单、明细、支付状态', icon: ReceiptText, tone: 'gold' },
  { title: '系统管理', value: '14 APIs', detail: '字典 + 操作日志', icon: Boxes, tone: 'ink' }
]

const serviceRows = [
  ['gateway-service', '9527', 'API 网关 / JWT 鉴权', '统一入口'],
  ['auth-service', '9001', '登录认证 / Token 签发', '公开登录'],
  ['patient-service', '9002', '患者档案 / Redis 缓存', '缓存演示'],
  ['appointment-service', '9003', '预约排班 / Feign 患者校验', '远程调用'],
  ['medical-record-service', '9004', '电子病历 / 医嘱', '远程校验'],
  ['pharmacy-service', '9005', '药品库存 / 发药', '医嘱联动'],
  ['billing-service', '9006', '账单支付 / 状态流转', '患者联动'],
  ['admin-service', '9007', '字典配置 / 操作日志', '后台基础']
]

const demoFlow = [
  { icon: KeyRound, title: '登录认证', text: 'POST /api/auth/login 获取 JWT，业务接口统一走 Bearer Token。' },
  { icon: Route, title: '网关访问', text: '通过 9527 访问患者、预约、病历、药房、账单和管理服务。' },
  { icon: RadioTower, title: '注册发现', text: 'Nacos 服务列表展示 8 个服务上线，下线服务后网关路由自动失效。' },
  { icon: Cable, title: '远程调用', text: '预约查患者，病历验预约，药房验医嘱，账单验患者。' },
  { icon: DatabaseZap, title: 'Redis 缓存', text: '患者详情采用 cache-aside：Redis 命中直接返回，更新/删除后驱逐。' },
  { icon: RefreshCw, title: '配置中心', text: '修改 Nacos 中 dmis.demo.config-message，访问 /api/config/*/demo/config 观察变化。' }
]

const checks = [
  ['后端全量测试', 'mvn test', '通过'],
  ['前端生产构建', 'npm run build', '通过'],
  ['接口总量', 'docs/api-progress.md', '76 个'],
  ['提交历史', 'git log --oneline', '已记录']
]
</script>

<template>
  <div class="page-shell">
    <aside class="side-rail">
      <div class="brand">
        <div class="brand-mark">
          <Stethoscope :size="24" />
        </div>
        <div>
          <strong>DMIS</strong>
          <span>Distributed Medical</span>
        </div>
      </div>

      <nav class="nav-stack" aria-label="演示导航">
        <a class="nav-pill active" href="#overview">
          <Activity :size="18" />
          总览
        </a>
        <a class="nav-pill" href="#flow">
          <Network :size="18" />
          链路
        </a>
        <a class="nav-pill" href="#services">
          <Braces :size="18" />
          服务
        </a>
        <a class="nav-pill" href="#quality">
          <TestTube2 :size="18" />
          验证
        </a>
      </nav>
    </aside>

    <main class="workspace">
      <section id="overview" class="hero-panel">
        <div class="hero-copy">
          <p class="eyebrow">Milestone 3 Demo Console</p>
          <h1>分布式医疗信息化系统演示驾驶舱</h1>
          <p>
            以 API 网关为统一入口，串联登录认证、Nacos 注册与配置中心、Redis 缓存、Feign
            远程调用、核心业务服务和单元测试结果。
          </p>
          <div class="hero-actions">
            <span><ShieldCheck :size="16" /> JWT Gateway</span>
            <span><RadioTower :size="16" /> Nacos Discovery</span>
            <span><DatabaseZap :size="16" /> Redis Cache</span>
          </div>
        </div>
        <div class="hero-score">
          <span>Documented APIs</span>
          <strong>76</strong>
          <small>覆盖新增、删除、修改、查询与演示接口</small>
        </div>
      </section>

      <section class="business-grid" aria-label="核心业务模块">
        <article v-for="card in businessCards" :key="card.title" class="business-card" :data-tone="card.tone">
          <div class="card-icon">
            <component :is="card.icon" :size="22" />
          </div>
          <span>{{ card.title }}</span>
          <strong>{{ card.value }}</strong>
          <p>{{ card.detail }}</p>
        </article>
      </section>

      <section id="flow" class="split-grid">
        <article class="panel flow-map">
          <div class="panel-head">
            <div>
              <p class="eyebrow">Integration Flow</p>
              <h2>上台演示链路</h2>
            </div>
            <GitBranch :size="22" />
          </div>
          <div class="flow-list">
            <div v-for="item in demoFlow" :key="item.title" class="flow-step">
              <div class="step-icon">
                <component :is="item.icon" :size="18" />
              </div>
              <div>
                <strong>{{ item.title }}</strong>
                <span>{{ item.text }}</span>
              </div>
            </div>
          </div>
        </article>

        <article id="quality" class="panel quality-panel">
          <div class="panel-head">
            <div>
              <p class="eyebrow">Quality Gates</p>
              <h2>验收检查</h2>
            </div>
            <BadgeCheck :size="22" />
          </div>
          <div class="check-list">
            <div v-for="check in checks" :key="check[0]" class="check-row">
              <span>{{ check[0] }}</span>
              <code>{{ check[1] }}</code>
              <strong>{{ check[2] }}</strong>
            </div>
          </div>
        </article>
      </section>

      <section id="services" class="panel service-panel">
        <div class="panel-head">
          <div>
            <p class="eyebrow">Service Registry</p>
            <h2>Nacos 服务注册视图</h2>
          </div>
          <RadioTower :size="22" />
        </div>
        <div class="service-table">
          <div v-for="service in serviceRows" :key="service[0]" class="service-row">
            <strong>{{ service[0] }}</strong>
            <code>{{ service[1] }}</code>
            <span>{{ service[2] }}</span>
            <em>{{ service[3] }}</em>
          </div>
        </div>
      </section>
    </main>
  </div>
</template>
