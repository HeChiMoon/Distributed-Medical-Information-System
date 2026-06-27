<script setup>
import {
  Activity,
  CalendarDays,
  ClipboardList,
  FileText,
  LayoutDashboard,
  Pill,
  ReceiptText,
  ShieldCheck,
  Stethoscope,
  UsersRound
} from 'lucide-vue-next'

const modules = [
  { name: '患者管理', icon: UsersRound, count: '1,286', status: '档案完整率 94%', tone: 'teal' },
  { name: '预约排班', icon: CalendarDays, count: '342', status: '今日待就诊', tone: 'blue' },
  { name: '电子病历', icon: FileText, count: '8,764', status: '病历归档', tone: 'green' },
  { name: '医嘱记录', icon: ClipboardList, count: '619', status: '执行中', tone: 'amber' },
  { name: '药品库存', icon: Pill, count: '2,148', status: '32 项低库存', tone: 'rose' },
  { name: '电子账单', icon: ReceiptText, count: '¥86.4k', status: '本日流水', tone: 'indigo' }
]

const services = [
  ['gateway-service', '9527', 'API 网关', '运行中'],
  ['auth-service', '9001', '登录认证', '运行中'],
  ['patient-service', '9002', '患者服务', '待接入数据库'],
  ['appointment-service', '9003', '预约排班', '已预留 Feign'],
  ['pharmacy-service', '9005', '药房库存', '已预留 Redis']
]
</script>

<template>
  <div class="shell">
    <aside class="sidebar">
      <div class="brand">
        <div class="brand-mark">
          <Stethoscope :size="22" />
        </div>
        <div>
          <strong>DMIS</strong>
          <span>医疗信息系统</span>
        </div>
      </div>

      <nav class="nav-list" aria-label="主导航">
        <button class="nav-item active" type="button">
          <LayoutDashboard :size="18" />
          工作台
        </button>
        <button class="nav-item" type="button">
          <UsersRound :size="18" />
          患者
        </button>
        <button class="nav-item" type="button">
          <CalendarDays :size="18" />
          预约
        </button>
        <button class="nav-item" type="button">
          <Pill :size="18" />
          药房
        </button>
        <button class="nav-item" type="button">
          <ShieldCheck :size="18" />
          权限
        </button>
      </nav>
    </aside>

    <main class="workspace">
      <header class="topbar">
        <div>
          <p class="eyebrow">Distributed Medical Information System</p>
          <h1>分布式医疗信息化工作台</h1>
        </div>
        <div class="operator">
          <span>演示账号</span>
          <strong>admin / doctor</strong>
        </div>
      </header>

      <section class="summary-grid" aria-label="业务模块概览">
        <article v-for="item in modules" :key="item.name" class="metric-card" :data-tone="item.tone">
          <div class="metric-icon">
            <component :is="item.icon" :size="22" />
          </div>
          <div>
            <span>{{ item.name }}</span>
            <strong>{{ item.count }}</strong>
            <small>{{ item.status }}</small>
          </div>
        </article>
      </section>

      <section class="content-grid">
        <div class="panel">
          <div class="panel-head">
            <div>
              <p class="eyebrow">Service Topology</p>
              <h2>微服务运行视图</h2>
            </div>
            <Activity :size="20" />
          </div>
          <div class="service-list">
            <div v-for="service in services" :key="service[0]" class="service-row">
              <strong>{{ service[0] }}</strong>
              <span>{{ service[2] }}</span>
              <code>{{ service[1] }}</code>
              <em>{{ service[3] }}</em>
            </div>
          </div>
        </div>

        <div class="panel flow-panel">
          <div class="panel-head">
            <div>
              <p class="eyebrow">Demo Flow</p>
              <h2>本轮演示链路</h2>
            </div>
          </div>
          <ol class="flow-list">
            <li>登录认证获取 JWT Token</li>
            <li>通过 API Gateway 访问业务接口</li>
            <li>预约服务使用 Feign 调用患者服务</li>
            <li>患者与药品热点数据接入 Redis 缓存</li>
            <li>Nacos 展示服务注册、下线与配置刷新</li>
          </ol>
        </div>
      </section>
    </main>
  </div>
</template>
