<script setup>
import { computed, h, onMounted, reactive, ref } from 'vue'
import {
  Activity,
  CalendarClock,
  DatabaseZap,
  FileText,
  LayoutDashboard,
  LogOut,
  Network,
  Pill,
  ReceiptText,
  RefreshCw,
  Search,
  ShieldCheck,
  Stethoscope,
  Trash2,
  UsersRound,
  WalletCards
} from 'lucide-vue-next'

const token = ref(localStorage.getItem('dmis_token') || '')
const activeModule = ref('patients')
const loading = ref(false)
const toast = reactive({ type: 'info', message: '' })

const loginForm = reactive({ username: 'admin', password: 'admin123' })

const modules = [
  { key: 'patients', label: '患者管理', icon: UsersRound, hint: '档案 CRUD + Redis 缓存' },
  { key: 'appointments', label: '预约排班', icon: CalendarClock, hint: '科室、医生、排班、预约' },
  { key: 'records', label: '病历医嘱', icon: FileText, hint: '电子病历 + 医嘱记录' },
  { key: 'pharmacy', label: '药房库存', icon: Pill, hint: '药品、库存、发药' },
  { key: 'billing', label: '电子账单', icon: ReceiptText, hint: '账单、明细、支付' },
  { key: 'search', label: '检索中心', icon: Search, hint: '按关键词和患者 ID 聚合查询' },
  { key: 'demo', label: '联调验证', icon: Network, hint: '网关、缓存、Feign、配置' }
]

const DataList = {
  props: {
    title: String,
    items: { type: Array, default: () => [] },
    mainKey: String,
    subKey: String
  },
  setup(props) {
    return () => h('div', { class: 'list-panel' }, [
      h('h3', props.title),
      props.items.length
        ? props.items.map((item) => h('article', { key: item.id, class: 'data-card compact' }, [
            h('strong', `#${item.id} ${item[props.mainKey] || '未命名'}`),
            h('span', item[props.subKey] || '无摘要')
          ]))
        : h('article', { class: 'empty' }, '暂无数据')
    ])
  }
}

const SearchList = {
  props: {
    title: String,
    items: { type: Array, default: () => [] },
    mainKey: String,
    subKey: String
  },
  setup(props) {
    return () => h('article', { class: 'search-result-card' }, [
      h('h3', props.title),
      props.items.length
        ? props.items.map((item) => h('div', { key: item.id, class: 'search-hit' }, [
            h('strong', `#${item.id} ${item[props.mainKey] || '未命名'}`),
            h('span', item[props.subKey] || '无摘要')
          ]))
        : h('div', { class: 'empty small' }, '暂无匹配结果')
    ])
  }
}

const state = reactive({
  patients: [],
  departments: [],
  doctors: [],
  schedules: [],
  appointments: [],
  records: [],
  orders: [],
  drugs: [],
  inventory: [],
  dispenses: [],
  bills: [],
  billItems: [],
  payments: [],
  logs: [],
  demoResults: [],
  search: {
    patients: [],
    drugs: [],
    appointments: [],
    records: [],
    orders: [],
    bills: [],
    dispenses: [],
    logs: []
  }
})

const filters = reactive({
  patientKeyword: '',
  patientId: '',
  drugKeyword: '',
  selectedDrugId: '',
  selectedBillId: '',
  searchKeyword: '',
  searchPatientId: '',
  searchModuleName: ''
})

const patientForm = reactive({
  id: null,
  name: '',
  gender: 'MALE',
  birthday: '1988-06-01',
  idCard: '',
  phone: '',
  address: '',
  bloodType: 'O',
  allergyHistory: 'None',
  medicalHistory: 'Demo baseline record',
  status: 'ACTIVE'
})

const departmentForm = reactive({ deptCode: '', deptName: '', status: 'ENABLED' })
const doctorForm = reactive({ doctorNo: '', doctorName: '', deptId: '', title: '主任医师', phone: '', status: 'ENABLED' })
const scheduleForm = reactive({ doctorId: '', deptId: '', scheduleDate: today(), timeSlot: '09:00-10:00', maxNumber: 20 })
const appointmentForm = reactive({ patientId: '', doctorId: '', deptId: '', scheduleId: '', appointmentDate: today(), timeSlot: '09:00-10:00', source: 'WEB' })

const recordForm = reactive({
  id: null,
  patientId: '',
  appointmentId: '',
  doctorId: '',
  chiefComplaint: '头晕乏力 2 天',
  presentIllness: '无发热，无胸痛，睡眠欠佳。',
  pastHistory: '既往体健。',
  physicalExam: '生命体征平稳，心肺腹查体未见明显异常。',
  diagnosis: '上呼吸道感染待排',
  advice: '注意休息，必要时复诊。',
  recordStatus: 'DRAFT'
})
const orderForm = reactive({ recordId: '', orderType: 'DRUG', orderContent: '布洛芬缓释胶囊', dosage: '0.3g', frequency: 'bid', durationDays: 3 })

const drugForm = reactive({
  id: null,
  drugCode: '',
  drugName: '',
  specification: '0.3g*20粒',
  unit: '盒',
  manufacturer: 'DMIS 制药',
  salePrice: 18.5,
  stockWarningLine: 20,
  status: 'ENABLED'
})
const inventoryForm = reactive({ drugId: '', batchNo: '', quantity: 100, productionDate: today(), expireDate: nextYear(), warehouseLocation: 'A-01' })
const dispenseForm = reactive({ recordId: '', patientId: '', orderId: '', drugId: '', quantity: 1, pharmacistName: '药师A' })

const billForm = reactive({
  patientId: '',
  appointmentId: '',
  recordId: '',
  items: [{ itemType: 'REGISTRATION', itemName: '普通门诊挂号费', unitPrice: 20, quantity: 1 }]
})
const paymentForm = reactive({ billId: '', paymentMethod: 'CASH', paymentAmount: 20 })

const isAuthed = computed(() => Boolean(token.value))
const currentModule = computed(() => modules.find((item) => item.key === activeModule.value) || modules[0])
const selectedPatient = computed(() => state.patients.find((item) => item.id === patientForm.id) || state.patients[0])
const selectedPatientAge = computed(() => {
  if (!selectedPatient.value?.birthday) return '未知'
  const birth = new Date(selectedPatient.value.birthday)
  const now = new Date()
  let age = now.getFullYear() - birth.getFullYear()
  const monthDelta = now.getMonth() - birth.getMonth()
  if (monthDelta < 0 || (monthDelta === 0 && now.getDate() < birth.getDate())) age -= 1
  return age > 0 ? `${age} 岁` : '不足 1 岁'
})
const selectedPatientSummary = computed(() => {
  const id = selectedPatient.value?.id
  if (!id) return { appointments: 0, records: 0, bills: 0 }
  return {
    appointments: state.appointments.filter((item) => item.patientId === id).length,
    records: state.records.filter((item) => item.patientId === id).length,
    bills: state.bills.filter((item) => item.patientId === id).length
  }
})
const summaryStats = computed(() => [
  { label: '患者', value: state.patients.length },
  { label: '预约', value: state.appointments.length },
  { label: '病历', value: state.records.length },
  { label: '药品', value: state.drugs.length },
  { label: '账单', value: state.bills.length },
  { label: '验证项', value: state.demoResults.length }
])

function today() {
  return new Date().toISOString().slice(0, 10)
}

function nextYear() {
  const date = new Date()
  date.setFullYear(date.getFullYear() + 1)
  return date.toISOString().slice(0, 10)
}

function showToast(message, type = 'success') {
  toast.message = message
  toast.type = type
}

function clearEmpty(payload) {
  return Object.fromEntries(
    Object.entries(payload).filter(([, value]) => value !== '' && value !== null && value !== undefined)
  )
}

function pageRecords(data) {
  return data?.records || []
}

function buildQuery(params) {
  const query = new URLSearchParams()
  Object.entries(params || {}).forEach(([key, value]) => {
    if (value !== '' && value !== null && value !== undefined) query.append(key, value)
  })
  const text = query.toString()
  return text ? `?${text}` : ''
}

async function api(path, options = {}) {
  const headers = {
    ...(options.body ? { 'Content-Type': 'application/json' } : {}),
    ...(token.value ? { Authorization: `Bearer ${token.value}` } : {}),
    ...(options.headers || {})
  }
  const response = await fetch(path, { ...options, headers })
  const text = await response.text()
  const result = text ? JSON.parse(text) : null
  if (!response.ok) throw new Error(result?.message || `HTTP ${response.status}`)
  if (result && typeof result.code === 'number' && result.code !== 0) throw new Error(result.message || '业务处理失败')
  return result?.data ?? result
}

async function runAction(action, successMessage) {
  loading.value = true
  try {
    const result = await action()
    if (successMessage) showToast(successMessage)
    return result
  } catch (error) {
    showToast(error.message || '操作失败', 'error')
    throw error
  } finally {
    loading.value = false
  }
}

async function login() {
  await runAction(async () => {
    const data = await api('/api/auth/login', {
      method: 'POST',
      body: JSON.stringify(loginForm)
    })
    token.value = data.accessToken
    localStorage.setItem('dmis_token', data.accessToken)
    await loadBootstrap()
  }, '登录成功，欢迎进入 DMIS 工作台')
}

function logout() {
  token.value = ''
  localStorage.removeItem('dmis_token')
  showToast('已退出登录', 'info')
}

async function loadBootstrap() {
  await Promise.allSettled([
    loadPatients(),
    loadDepartments(),
    loadDoctors(),
    loadSchedules(),
    loadAppointments(),
    loadRecords(),
    loadOrders(),
    loadDrugs(),
    loadBills(),
    loadLogs()
  ])
}

async function refreshActive() {
  const loaders = {
    patients: loadPatients,
    appointments: async () => {
      await loadDepartments()
      await loadDoctors()
      await loadSchedules()
      await loadAppointments()
    },
    records: async () => {
      await loadRecords()
      await loadOrders()
    },
    pharmacy: async () => {
      await loadDrugs()
      await loadDispenses()
    },
    billing: loadBills,
    search: runGlobalSearch,
    demo: runDemoChecks
  }
  await runAction(loaders[activeModule.value] || loadBootstrap, '数据已刷新')
}

async function loadPatients() {
  state.patients = pageRecords(await api(`/api/patients${buildQuery({ keyword: filters.patientKeyword, page: 0, size: 30 })}`))
}

async function savePatient() {
  const isUpdate = Boolean(patientForm.id)
  const payload = clearEmpty({
    name: patientForm.name,
    gender: patientForm.gender,
    birthday: patientForm.birthday,
    idCard: isUpdate ? undefined : patientForm.idCard,
    phone: patientForm.phone,
    address: patientForm.address,
    bloodType: patientForm.bloodType,
    allergyHistory: patientForm.allergyHistory,
    medicalHistory: patientForm.medicalHistory,
    status: isUpdate ? patientForm.status : undefined
  })
  await runAction(async () => {
    await api(isUpdate ? `/api/patients/${patientForm.id}` : '/api/patients', {
      method: isUpdate ? 'PUT' : 'POST',
      body: JSON.stringify(payload)
    })
    resetPatientForm()
    await loadPatients()
  }, isUpdate ? '患者信息已更新' : '患者已创建')
}

function editPatient(row) {
  Object.assign(patientForm, { ...row, birthday: row.birthday || today() })
}

async function deletePatient(id) {
  await runAction(async () => {
    await api(`/api/patients/${id}`, { method: 'DELETE' })
    await loadPatients()
  }, '患者已删除')
}

async function showPatientCache(id) {
  await runAction(async () => {
    const data = await api(`/api/patients/${id}/cache-demo`)
    showToast(`Redis Key: ${data.redisKey}; TTL: ${data.ttlMinutes} 分钟`, 'info')
  })
}

function resetPatientForm() {
  Object.assign(patientForm, {
    id: null,
    name: '',
    gender: 'MALE',
    birthday: '1988-06-01',
    idCard: '',
    phone: '',
    address: '',
    bloodType: 'O',
    allergyHistory: 'None',
    medicalHistory: 'Demo baseline record',
    status: 'ACTIVE'
  })
}

function fillDemoPatient() {
  const suffix = Date.now().toString().slice(-6)
  Object.assign(patientForm, {
    id: null,
    name: `演示患者${suffix.slice(-3)}`,
    gender: 'MALE',
    birthday: '1990-03-15',
    idCard: `DEMO${suffix}`,
    phone: '13800000000',
    address: '门诊综合楼 3 层',
    bloodType: 'O',
    allergyHistory: '无明确药物过敏史',
    medicalHistory: '既往体健，本次用于系统验收演示',
    status: 'ACTIVE'
  })
}

async function loadDepartments() {
  state.departments = pageRecords(await api('/api/appointments/departments?page=0&size=30'))
}

async function loadDoctors() {
  state.doctors = pageRecords(await api('/api/appointments/doctors?page=0&size=30'))
}

async function loadSchedules() {
  state.schedules = pageRecords(await api('/api/schedules?page=0&size=30'))
}

async function loadAppointments() {
  state.appointments = pageRecords(await api(`/api/appointments${buildQuery({ patientId: filters.patientId, page: 0, size: 30 })}`))
}

async function createDepartment() {
  await runAction(async () => {
    await api('/api/appointments/departments', { method: 'POST', body: JSON.stringify(clearEmpty(departmentForm)) })
    Object.assign(departmentForm, { deptCode: '', deptName: '', status: 'ENABLED' })
    await loadDepartments()
  }, '科室已创建')
}

async function createDoctor() {
  await runAction(async () => {
    await api('/api/appointments/doctors', {
      method: 'POST',
      body: JSON.stringify({ ...clearEmpty(doctorForm), deptId: Number(doctorForm.deptId) })
    })
    Object.assign(doctorForm, { doctorNo: '', doctorName: '', deptId: '', title: '主任医师', phone: '', status: 'ENABLED' })
    await loadDoctors()
  }, '医生已创建')
}

async function createSchedule() {
  await runAction(async () => {
    await api('/api/schedules', {
      method: 'POST',
      body: JSON.stringify({
        doctorId: Number(scheduleForm.doctorId),
        deptId: Number(scheduleForm.deptId),
        scheduleDate: scheduleForm.scheduleDate,
        timeSlot: scheduleForm.timeSlot,
        maxNumber: Number(scheduleForm.maxNumber)
      })
    })
    await loadSchedules()
  }, '排班已创建')
}

async function createAppointment() {
  await runAction(async () => {
    await api('/api/appointments', {
      method: 'POST',
      body: JSON.stringify({
        patientId: Number(appointmentForm.patientId),
        doctorId: Number(appointmentForm.doctorId),
        deptId: Number(appointmentForm.deptId),
        scheduleId: Number(appointmentForm.scheduleId),
        appointmentDate: appointmentForm.appointmentDate,
        timeSlot: appointmentForm.timeSlot,
        source: appointmentForm.source
      })
    })
    await Promise.all([loadAppointments(), loadSchedules()])
  }, '预约已创建')
}

async function cancelAppointment(id) {
  await runAction(async () => {
    await api(`/api/appointments/${id}/cancel`, { method: 'PUT' })
    await loadAppointments()
  }, '预约已取消')
}

async function loadRecords() {
  state.records = pageRecords(await api(`/api/records${buildQuery({ patientId: filters.patientId, page: 0, size: 30 })}`))
}

async function loadOrders() {
  state.orders = pageRecords(await api(`/api/orders${buildQuery({ patientId: filters.patientId, page: 0, size: 30 })}`))
}

async function saveRecord() {
  const isUpdate = Boolean(recordForm.id)
  const payload = isUpdate
    ? clearEmpty({
        chiefComplaint: recordForm.chiefComplaint,
        presentIllness: recordForm.presentIllness,
        pastHistory: recordForm.pastHistory,
        physicalExam: recordForm.physicalExam,
        diagnosis: recordForm.diagnosis,
        advice: recordForm.advice,
        recordStatus: recordForm.recordStatus
      })
    : clearEmpty({
        patientId: Number(recordForm.patientId),
        appointmentId: recordForm.appointmentId ? Number(recordForm.appointmentId) : undefined,
        doctorId: Number(recordForm.doctorId),
        chiefComplaint: recordForm.chiefComplaint,
        presentIllness: recordForm.presentIllness,
        pastHistory: recordForm.pastHistory,
        physicalExam: recordForm.physicalExam,
        diagnosis: recordForm.diagnosis,
        advice: recordForm.advice
      })
  await runAction(async () => {
    await api(isUpdate ? `/api/records/${recordForm.id}` : '/api/records', {
      method: isUpdate ? 'PUT' : 'POST',
      body: JSON.stringify(payload)
    })
    resetRecordForm()
    await loadRecords()
  }, isUpdate ? '病历已更新' : '病历已创建')
}

function editRecord(row) {
  Object.assign(recordForm, row)
}

async function deleteRecord(id) {
  await runAction(async () => {
    await api(`/api/records/${id}`, { method: 'DELETE' })
    await loadRecords()
  }, '病历已删除')
}

async function createOrder() {
  await runAction(async () => {
    await api('/api/orders', {
      method: 'POST',
      body: JSON.stringify({
        recordId: Number(orderForm.recordId),
        orderType: orderForm.orderType,
        orderContent: orderForm.orderContent,
        dosage: orderForm.dosage,
        frequency: orderForm.frequency,
        durationDays: Number(orderForm.durationDays)
      })
    })
    await loadOrders()
  }, '医嘱已创建')
}

async function stopOrder(id) {
  await runAction(async () => {
    await api(`/api/orders/${id}/stop`, { method: 'PUT' })
    await loadOrders()
  }, '医嘱已停止')
}

function resetRecordForm() {
  Object.assign(recordForm, {
    id: null,
    patientId: '',
    appointmentId: '',
    doctorId: '',
    chiefComplaint: '头晕乏力 2 天',
    presentIllness: '无发热，无胸痛，睡眠欠佳。',
    pastHistory: '既往体健。',
    physicalExam: '生命体征平稳，心肺腹查体未见明显异常。',
    diagnosis: '上呼吸道感染待排',
    advice: '注意休息，必要时复诊。',
    recordStatus: 'DRAFT'
  })
}

async function loadDrugs() {
  state.drugs = pageRecords(await api(`/api/pharmacy/drugs${buildQuery({ keyword: filters.drugKeyword, page: 0, size: 30 })}`))
}

async function saveDrug() {
  const isUpdate = Boolean(drugForm.id)
  await runAction(async () => {
    await api(isUpdate ? `/api/pharmacy/drugs/${drugForm.id}` : '/api/pharmacy/drugs', {
      method: isUpdate ? 'PUT' : 'POST',
      body: JSON.stringify({
        drugCode: drugForm.drugCode,
        drugName: drugForm.drugName,
        specification: drugForm.specification,
        unit: drugForm.unit,
        manufacturer: drugForm.manufacturer,
        salePrice: Number(drugForm.salePrice),
        stockWarningLine: Number(drugForm.stockWarningLine),
        status: drugForm.status
      })
    })
    resetDrugForm()
    await loadDrugs()
  }, isUpdate ? '药品已更新' : '药品已创建')
}

function editDrug(row) {
  Object.assign(drugForm, row)
}

async function deleteDrug(id) {
  await runAction(async () => {
    await api(`/api/pharmacy/drugs/${id}`, { method: 'DELETE' })
    await loadDrugs()
  }, '药品已删除')
}

async function inboundInventory() {
  await runAction(async () => {
    await api('/api/pharmacy/inventory/inbound', {
      method: 'POST',
      body: JSON.stringify({
        drugId: Number(inventoryForm.drugId),
        batchNo: inventoryForm.batchNo,
        quantity: Number(inventoryForm.quantity),
        productionDate: inventoryForm.productionDate,
        expireDate: inventoryForm.expireDate,
        warehouseLocation: inventoryForm.warehouseLocation
      })
    })
    filters.selectedDrugId = inventoryForm.drugId
    await loadInventory()
  }, '库存入库成功')
}

async function loadInventory() {
  const drugId = filters.selectedDrugId || inventoryForm.drugId
  if (!drugId) return
  state.inventory = await api(`/api/pharmacy/inventory?drugId=${drugId}`)
}

async function loadDispenses() {
  state.dispenses = pageRecords(await api(`/api/pharmacy/dispenses${buildQuery({ patientId: filters.patientId, page: 0, size: 30 })}`))
}

async function createDispense() {
  await runAction(async () => {
    await api('/api/pharmacy/dispenses', {
      method: 'POST',
      body: JSON.stringify({
        recordId: dispenseForm.recordId ? Number(dispenseForm.recordId) : undefined,
        patientId: Number(dispenseForm.patientId),
        orderId: Number(dispenseForm.orderId),
        drugId: Number(dispenseForm.drugId),
        quantity: Number(dispenseForm.quantity),
        pharmacistName: dispenseForm.pharmacistName
      })
    })
    await loadDispenses()
  }, '发药记录已生成')
}

function resetDrugForm() {
  Object.assign(drugForm, {
    id: null,
    drugCode: '',
    drugName: '',
    specification: '0.3g*20粒',
    unit: '盒',
    manufacturer: 'DMIS 制药',
    salePrice: 18.5,
    stockWarningLine: 20,
    status: 'ENABLED'
  })
}

async function loadBills() {
  state.bills = pageRecords(await api(`/api/bills${buildQuery({ patientId: filters.patientId, page: 0, size: 30 })}`))
}

function addBillItem() {
  billForm.items.push({ itemType: 'DRUG', itemName: '药品费用', unitPrice: 18.5, quantity: 1 })
}

function removeBillItem(index) {
  if (billForm.items.length > 1) billForm.items.splice(index, 1)
}

async function createBill() {
  await runAction(async () => {
    await api('/api/bills', {
      method: 'POST',
      body: JSON.stringify({
        patientId: Number(billForm.patientId),
        appointmentId: billForm.appointmentId ? Number(billForm.appointmentId) : undefined,
        recordId: billForm.recordId ? Number(billForm.recordId) : undefined,
        items: billForm.items.map((item) => ({
          itemType: item.itemType,
          itemName: item.itemName,
          unitPrice: Number(item.unitPrice),
          quantity: Number(item.quantity)
        }))
      })
    })
    await loadBills()
  }, '账单已创建')
}

async function loadBillDetail(id = filters.selectedBillId || paymentForm.billId) {
  if (!id) return
  filters.selectedBillId = id
  paymentForm.billId = id
  state.billItems = await api(`/api/bills/${id}/items`)
  state.payments = await api(`/api/bills/${id}/payments`)
}

async function payBill() {
  await runAction(async () => {
    await api(`/api/bills/${paymentForm.billId}/payments`, {
      method: 'POST',
      body: JSON.stringify({
        paymentMethod: paymentForm.paymentMethod,
        paymentAmount: Number(paymentForm.paymentAmount)
      })
    })
    await Promise.all([loadBills(), loadBillDetail(paymentForm.billId)])
  }, '支付已完成')
}

async function cancelBill(id) {
  await runAction(async () => {
    await api(`/api/bills/${id}/cancel`, { method: 'PUT' })
    await loadBills()
  }, '账单已取消')
}

async function loadLogs() {
  state.logs = pageRecords(await api('/api/admin/operation-logs?page=0&size=30'))
}

async function runGlobalSearch() {
  await runAction(async () => {
    state.search.patients = pageRecords(await api(`/api/patients${buildQuery({ keyword: filters.searchKeyword, page: 0, size: 12 })}`))
    state.search.drugs = pageRecords(await api(`/api/pharmacy/drugs${buildQuery({ keyword: filters.searchKeyword, page: 0, size: 12 })}`))

    if (filters.searchPatientId) {
      const patientQuery = { patientId: filters.searchPatientId, page: 0, size: 12 }
      state.search.appointments = pageRecords(await api(`/api/appointments${buildQuery(patientQuery)}`))
      state.search.records = pageRecords(await api(`/api/records${buildQuery(patientQuery)}`))
      state.search.orders = pageRecords(await api(`/api/orders${buildQuery(patientQuery)}`))
      state.search.bills = pageRecords(await api(`/api/bills${buildQuery(patientQuery)}`))
      state.search.dispenses = pageRecords(await api(`/api/pharmacy/dispenses${buildQuery(patientQuery)}`))
    } else {
      state.search.appointments = []
      state.search.records = []
      state.search.orders = []
      state.search.bills = []
      state.search.dispenses = []
    }

    state.search.logs = filters.searchModuleName
      ? pageRecords(await api(`/api/admin/operation-logs${buildQuery({ moduleName: filters.searchModuleName, page: 0, size: 12 })}`))
      : pageRecords(await api('/api/admin/operation-logs?page=0&size=12'))
  }, '检索完成')
}

async function runDemoChecks() {
  await runAction(async () => {
    const patientId = state.patients[0]?.id
    const checks = []
    checks.push({ name: '模块元数据', data: await api('/api/patients/modules') })
    checks.push({ name: '配置中心', data: await api('/api/config/patient/demo/config') })
    if (patientId) {
      checks.push({ name: 'Redis 缓存', data: await api(`/api/patients/${patientId}/cache-demo`) })
      checks.push({ name: 'Feign 远程调用', data: await api(`/api/appointments/demo/remote-patient/${patientId}`) })
    } else {
      checks.push({ name: 'Redis/Feign 前置条件', data: { message: '请先创建患者，再运行完整验证' } })
    }
    state.demoResults = checks
  }, '联调验证完成')
}

onMounted(() => {
  if (token.value) loadBootstrap()
})
</script>

<template>
  <div v-if="!isAuthed" class="login-home">
    <section class="login-hero">
      <div class="brand large">
        <div class="brand-mark">
          <Stethoscope :size="30" />
        </div>
        <div>
          <strong>DMIS</strong>
          <span>分布式医疗信息系统</span>
        </div>
      </div>
      <p class="eyebrow">Medical Information Workspace</p>
      <h1>以网关为入口的医疗业务工作台</h1>
      <p class="subtitle">
        登录后进入患者管理、预约排班、电子病历、药房库存、电子账单和检索中心。所有交互都通过 API 网关访问真实微服务。
      </p>
      <div class="login-metrics">
        <span><ShieldCheck :size="18" /> JWT 登录认证</span>
        <span><DatabaseZap :size="18" /> Redis 患者缓存</span>
        <span><Network :size="18" /> Nacos + Feign</span>
      </div>
    </section>

    <form class="login-panel" @submit.prevent="login">
      <p class="eyebrow">Secure Login</p>
      <h2>进入工作台</h2>
      <label>
        用户名
        <input v-model="loginForm.username" autocomplete="username" />
      </label>
      <label>
        密码
        <input v-model="loginForm.password" type="password" autocomplete="current-password" />
      </label>
      <button class="primary-button" :disabled="loading" type="submit">
        <ShieldCheck :size="18" />
        {{ loading ? '登录中...' : '登录进入系统' }}
      </button>
      <small>演示账号：admin / admin123</small>
    </form>
    <div v-if="toast.message" :class="['toast', toast.type]">{{ toast.message }}</div>
  </div>

  <div v-else class="app-shell">
    <aside class="sidebar">
      <div class="brand">
        <div class="brand-mark">
          <Stethoscope :size="25" />
        </div>
        <div>
          <strong>DMIS</strong>
          <span>业务工作台</span>
        </div>
      </div>

      <nav class="module-nav">
        <button
          v-for="item in modules"
          :key="item.key"
          class="nav-button"
          :class="{ active: activeModule === item.key }"
          type="button"
          @click="activeModule = item.key"
        >
          <component :is="item.icon" :size="18" />
          <span>{{ item.label }}</span>
          <small>{{ item.hint }}</small>
        </button>
      </nav>
    </aside>

    <main class="main">
      <header class="topbar">
        <div>
          <p class="eyebrow">Gateway Driven Clinical Workspace</p>
          <h1>真实业务交互工作台</h1>
          <span class="subtitle">当前模块：{{ currentModule.label }}。业务请求统一走 `/api/**` 网关路由。</span>
        </div>
        <div class="session-card">
          <span class="status-dot online"></span>
          <strong>已登录</strong>
          <button class="ghost-button" type="button" @click="logout">
            <LogOut :size="16" />
            退出
          </button>
        </div>
      </header>

      <section class="stats-grid">
        <article v-for="stat in summaryStats" :key="stat.label" class="stat-card">
          <span>{{ stat.label }}</span>
          <strong>{{ stat.value }}</strong>
        </article>
      </section>

      <section class="workspace-card">
        <div class="section-head">
          <div>
            <p class="eyebrow">Current Module</p>
            <h2>{{ currentModule.label }}</h2>
            <span>{{ currentModule.hint }}</span>
          </div>
          <button class="ghost-button" :disabled="loading" type="button" @click="refreshActive">
            <RefreshCw :size="16" />
            刷新当前模块
          </button>
        </div>

        <div v-if="activeModule === 'patients'" class="patient-workspace">
          <section class="patient-command-card">
            <div>
              <p class="eyebrow">Patient Command Center</p>
              <h3>患者档案中心</h3>
              <span>支持患者检索、档案编辑、Redis 缓存演示，以及预约/病历/账单关联概览。</span>
            </div>
            <div class="patient-command-actions">
              <input v-model="filters.patientKeyword" placeholder="输入患者姓名搜索" @keyup.enter="loadPatients" />
              <button class="ghost-button" type="button" @click="loadPatients">
                <Search :size="16" />
                查询
              </button>
              <button class="ghost-button" type="button" @click="fillDemoPatient">填充演示患者</button>
            </div>
          </section>

          <section class="patient-list-card">
            <div class="patient-card-head">
              <div>
                <p class="eyebrow">Patient Queue</p>
                <h3>患者列表</h3>
              </div>
              <span>{{ state.patients.length }} 条</span>
            </div>
            <article v-if="!state.patients.length" class="empty">暂无患者，先创建一条演示记录。</article>
            <article v-for="patient in state.patients" :key="patient.id" class="patient-row" :class="{ selected: patient.id === patientForm.id }">
              <button type="button" @click="editPatient(patient)">
                <span class="patient-mini-avatar">{{ patient.name?.slice(0, 1) || '患' }}</span>
                <span class="patient-row-main">
                  <strong>{{ patient.name }}</strong>
                  <small>{{ patient.patientNo }} · {{ patient.gender }} · {{ patient.status }}</small>
                </span>
              </button>
              <button class="danger small-action" type="button" @click="deletePatient(patient.id)">
                <Trash2 :size="14" />
              </button>
            </article>
          </section>

          <form class="patient-editor-card" @submit.prevent="savePatient">
            <div class="editor-title">
              <div>
                <p class="eyebrow">Patient Editor</p>
                <h3>{{ patientForm.id ? '编辑患者档案' : '新增患者档案' }}</h3>
              </div>
              <button class="ghost-button" type="button" @click="resetPatientForm">新建</button>
            </div>
            <div class="form-section-title">基础身份</div>
            <div class="form-grid two compact-form">
              <label>姓名<input v-model="patientForm.name" required placeholder="请输入患者姓名" /></label>
              <label>性别<select v-model="patientForm.gender"><option>MALE</option><option>FEMALE</option></select></label>
              <label>生日<input v-model="patientForm.birthday" type="date" /></label>
              <label>证件号<input v-model="patientForm.idCard" :disabled="Boolean(patientForm.id)" placeholder="新增后不可修改" /></label>
            </div>
            <div class="form-section-title">联系方式</div>
            <div class="form-grid two compact-form">
              <label>电话<input v-model="patientForm.phone" placeholder="13800000000" /></label>
              <label>血型<input v-model="patientForm.bloodType" placeholder="O" /></label>
              <label class="wide">地址<input v-model="patientForm.address" placeholder="家庭住址或科室位置" /></label>
              <label v-if="patientForm.id">状态<select v-model="patientForm.status"><option>ACTIVE</option><option>INACTIVE</option></select></label>
            </div>
            <div class="form-section-title">医疗备注</div>
            <div class="form-grid compact-form">
              <label>过敏史<textarea v-model="patientForm.allergyHistory" placeholder="如：青霉素过敏 / 无"></textarea></label>
              <label>病史<textarea v-model="patientForm.medicalHistory" placeholder="既往史、家族史或演示备注"></textarea></label>
            </div>
            <button class="primary-button" :disabled="loading" type="submit">保存患者</button>
          </form>

          <section class="patient-profile-card">
            <div class="profile-topline">
              <div class="profile-avatar">
                <UsersRound :size="30" />
              </div>
              <div>
                <p class="eyebrow">Current Profile</p>
                <h3>{{ selectedPatient?.name || '暂无患者档案' }}</h3>
                <span v-if="selectedPatient">#{{ selectedPatient.id }} · {{ selectedPatient.patientNo }}</span>
                <span v-else>创建或选择患者后展示档案摘要。</span>
              </div>
            </div>

            <div v-if="selectedPatient" class="patient-info-grid">
              <div><span>年龄</span><strong>{{ selectedPatientAge }}</strong></div>
              <div><span>性别</span><strong>{{ selectedPatient.gender }}</strong></div>
              <div><span>电话</span><strong>{{ selectedPatient.phone || '未填写' }}</strong></div>
              <div><span>血型</span><strong>{{ selectedPatient.bloodType || '未知' }}</strong></div>
            </div>

            <div v-if="selectedPatient" class="patient-linked-grid">
              <div>
                <CalendarClock :size="16" />
                <span>预约</span>
                <strong>{{ selectedPatientSummary.appointments }}</strong>
              </div>
              <div>
                <FileText :size="16" />
                <span>病历</span>
                <strong>{{ selectedPatientSummary.records }}</strong>
              </div>
              <div>
                <ReceiptText :size="16" />
                <span>账单</span>
                <strong>{{ selectedPatientSummary.bills }}</strong>
              </div>
            </div>

            <div v-if="selectedPatient" class="clinical-notes">
              <strong>过敏史</strong>
              <p>{{ selectedPatient.allergyHistory || '暂无记录' }}</p>
              <strong>既往病史</strong>
              <p>{{ selectedPatient.medicalHistory || '暂无记录' }}</p>
            </div>

            <button v-if="selectedPatient" class="profile-cache-button" type="button" @click="showPatientCache(selectedPatient.id)">
              <DatabaseZap :size="16" />
              查看 Redis 缓存策略
            </button>
          </section>
        </div>

        <div v-if="activeModule === 'appointments'" class="module-stack">
          <div class="triple-grid">
            <form class="form-panel" @submit.prevent="createDepartment">
              <h3>创建科室</h3>
              <label>科室编码<input v-model="departmentForm.deptCode" required /></label>
              <label>科室名称<input v-model="departmentForm.deptName" required /></label>
              <button class="primary-button" type="submit">保存科室</button>
            </form>
            <form class="form-panel" @submit.prevent="createDoctor">
              <h3>创建医生</h3>
              <label>医生工号<input v-model="doctorForm.doctorNo" required /></label>
              <label>医生姓名<input v-model="doctorForm.doctorName" required /></label>
              <label>科室 ID<input v-model="doctorForm.deptId" required type="number" /></label>
              <button class="primary-button" type="submit">保存医生</button>
            </form>
            <form class="form-panel" @submit.prevent="createSchedule">
              <h3>创建排班</h3>
              <label>医生 ID<input v-model="scheduleForm.doctorId" required type="number" /></label>
              <label>科室 ID<input v-model="scheduleForm.deptId" required type="number" /></label>
              <label>日期<input v-model="scheduleForm.scheduleDate" type="date" /></label>
              <label>时段<input v-model="scheduleForm.timeSlot" /></label>
              <label>号源数<input v-model="scheduleForm.maxNumber" type="number" min="1" /></label>
              <button class="primary-button" type="submit">保存排班</button>
            </form>
          </div>

          <form class="form-panel inline-form" @submit.prevent="createAppointment">
            <h3>创建预约</h3>
            <label>患者 ID<input v-model="appointmentForm.patientId" required type="number" /></label>
            <label>医生 ID<input v-model="appointmentForm.doctorId" required type="number" /></label>
            <label>科室 ID<input v-model="appointmentForm.deptId" required type="number" /></label>
            <label>排班 ID<input v-model="appointmentForm.scheduleId" required type="number" /></label>
            <label>日期<input v-model="appointmentForm.appointmentDate" type="date" /></label>
            <label>时段<input v-model="appointmentForm.timeSlot" /></label>
            <button class="primary-button" type="submit">提交预约</button>
          </form>

          <div class="quad-grid">
            <DataList title="科室" :items="state.departments" main-key="deptName" sub-key="deptCode" />
            <DataList title="医生" :items="state.doctors" main-key="doctorName" sub-key="doctorNo" />
            <DataList title="排班" :items="state.schedules" main-key="scheduleDate" sub-key="timeSlot" />
            <div class="list-panel">
              <h3>预约记录</h3>
              <article v-for="item in state.appointments" :key="item.id" class="data-card compact">
                <strong>#{{ item.id }} {{ item.appointmentNo }}</strong>
                <span>患者 {{ item.patientId }} / 医生 {{ item.doctorId }} / {{ item.status }}</span>
                <button v-if="item.status !== 'CANCELLED'" @click="cancelAppointment(item.id)">取消预约</button>
              </article>
            </div>
          </div>
        </div>

        <div v-if="activeModule === 'records'" class="module-grid">
          <form class="form-panel" @submit.prevent="saveRecord">
            <h3>{{ recordForm.id ? '编辑病历' : '创建病历' }}</h3>
            <div class="form-grid two">
              <label>患者 ID<input v-model="recordForm.patientId" required type="number" /></label>
              <label>医生 ID<input v-model="recordForm.doctorId" required type="number" /></label>
              <label>预约 ID<input v-model="recordForm.appointmentId" type="number" /></label>
              <label v-if="recordForm.id">状态<select v-model="recordForm.recordStatus"><option>DRAFT</option><option>FINALIZED</option></select></label>
              <label class="wide">主诉<textarea v-model="recordForm.chiefComplaint" required></textarea></label>
              <label class="wide">诊断<textarea v-model="recordForm.diagnosis" required></textarea></label>
              <label class="wide">医嘱建议<textarea v-model="recordForm.advice"></textarea></label>
            </div>
            <button class="primary-button" type="submit">保存病历</button>
          </form>
          <div class="list-panel">
            <form class="mini-form" @submit.prevent="createOrder">
              <h3>创建医嘱</h3>
              <input v-model="orderForm.recordId" placeholder="病历 ID" required type="number" />
              <input v-model="orderForm.orderType" placeholder="医嘱类型" />
              <input v-model="orderForm.orderContent" placeholder="医嘱内容" />
              <input v-model="orderForm.dosage" placeholder="剂量" />
              <input v-model="orderForm.frequency" placeholder="频次" />
              <input v-model="orderForm.durationDays" placeholder="天数" type="number" />
              <button class="primary-button" type="submit">提交医嘱</button>
            </form>
            <h3>病历列表</h3>
            <article v-for="record in state.records" :key="record.id" class="data-card">
              <div>
                <strong>#{{ record.id }} {{ record.recordNo }}</strong>
                <span>患者 {{ record.patientId }} / 医生 {{ record.doctorId }} / {{ record.recordStatus }}</span>
              </div>
              <div class="row-actions">
                <button @click="editRecord(record)">编辑</button>
                <button class="danger" @click="deleteRecord(record.id)">删除</button>
              </div>
            </article>
            <h3>医嘱列表</h3>
            <article v-for="order in state.orders" :key="order.id" class="data-card compact">
              <strong>#{{ order.id }} {{ order.orderNo }} · {{ order.status }}</strong>
              <span>{{ order.orderType }} / {{ order.orderContent }} / 患者 {{ order.patientId }}</span>
              <button v-if="order.status === 'ACTIVE'" @click="stopOrder(order.id)">停止医嘱</button>
            </article>
          </div>
        </div>

        <div v-if="activeModule === 'pharmacy'" class="module-grid">
          <form class="form-panel" @submit.prevent="saveDrug">
            <h3>{{ drugForm.id ? '编辑药品' : '新增药品' }}</h3>
            <div class="form-grid two">
              <label>药品编码<input v-model="drugForm.drugCode" required /></label>
              <label>药品名称<input v-model="drugForm.drugName" required /></label>
              <label>规格<input v-model="drugForm.specification" /></label>
              <label>单位<input v-model="drugForm.unit" /></label>
              <label>厂家<input v-model="drugForm.manufacturer" /></label>
              <label>售价<input v-model="drugForm.salePrice" type="number" step="0.01" /></label>
              <label>预警线<input v-model="drugForm.stockWarningLine" type="number" /></label>
            </div>
            <button class="primary-button" type="submit">保存药品</button>
          </form>
          <div class="list-panel">
            <form class="mini-form" @submit.prevent="inboundInventory">
              <h3>库存入库</h3>
              <input v-model="inventoryForm.drugId" required placeholder="药品 ID" type="number" />
              <input v-model="inventoryForm.batchNo" required placeholder="批号" />
              <input v-model="inventoryForm.quantity" required placeholder="数量" type="number" />
              <input v-model="inventoryForm.productionDate" type="date" />
              <input v-model="inventoryForm.expireDate" type="date" />
              <button class="primary-button" type="submit">入库</button>
            </form>
            <form class="mini-form" @submit.prevent="createDispense">
              <h3>发药</h3>
              <input v-model="dispenseForm.patientId" required placeholder="患者 ID" type="number" />
              <input v-model="dispenseForm.recordId" placeholder="病历 ID" type="number" />
              <input v-model="dispenseForm.orderId" required placeholder="医嘱 ID" type="number" />
              <input v-model="dispenseForm.drugId" required placeholder="药品 ID" type="number" />
              <input v-model="dispenseForm.quantity" required placeholder="数量" type="number" />
              <button class="primary-button" type="submit">确认发药</button>
            </form>
            <h3>药品列表</h3>
            <article v-for="drug in state.drugs" :key="drug.id" class="data-card">
              <div>
                <strong>#{{ drug.id }} {{ drug.drugName }}</strong>
                <span>{{ drug.drugCode }} / {{ drug.specification }} / ￥{{ drug.salePrice }}</span>
              </div>
              <div class="row-actions">
                <button @click="editDrug(drug)">编辑</button>
                <button @click="filters.selectedDrugId = drug.id; loadInventory()">库存</button>
                <button class="danger" @click="deleteDrug(drug.id)">删除</button>
              </div>
            </article>
            <h3>库存批次</h3>
            <article v-for="item in state.inventory" :key="item.id" class="data-card compact">
              <strong>#{{ item.id }} 批号 {{ item.batchNo }}</strong>
              <span>数量 {{ item.quantity }} / 锁定 {{ item.lockedQuantity }} / 库位 {{ item.warehouseLocation }}</span>
            </article>
          </div>
        </div>

        <div v-if="activeModule === 'billing'" class="module-grid">
          <form class="form-panel" @submit.prevent="createBill">
            <h3>创建账单</h3>
            <label>患者 ID<input v-model="billForm.patientId" required type="number" /></label>
            <label>预约 ID<input v-model="billForm.appointmentId" type="number" /></label>
            <label>病历 ID<input v-model="billForm.recordId" type="number" /></label>
            <div class="bill-items">
              <div v-for="(item, index) in billForm.items" :key="index" class="bill-line">
                <input v-model="item.itemType" placeholder="项目类型" />
                <input v-model="item.itemName" placeholder="项目名称" />
                <input v-model="item.unitPrice" type="number" step="0.01" />
                <input v-model="item.quantity" type="number" min="1" />
                <button type="button" @click="removeBillItem(index)">移除</button>
              </div>
            </div>
            <div class="button-row">
              <button class="ghost-button" type="button" @click="addBillItem">新增明细</button>
              <button class="primary-button" type="submit">创建账单</button>
            </div>
          </form>
          <div class="list-panel">
            <form class="mini-form" @submit.prevent="payBill">
              <h3>支付账单</h3>
              <input v-model="paymentForm.billId" required placeholder="账单 ID" type="number" />
              <select v-model="paymentForm.paymentMethod"><option>CASH</option><option>WECHAT</option><option>ALIPAY</option><option>BANK_CARD</option></select>
              <input v-model="paymentForm.paymentAmount" required placeholder="支付金额" type="number" step="0.01" />
              <button class="primary-button" type="submit">支付</button>
            </form>
            <h3>账单列表</h3>
            <article v-for="bill in state.bills" :key="bill.id" class="data-card">
              <div>
                <strong>#{{ bill.id }} {{ bill.billNo }}</strong>
                <span>患者 {{ bill.patientId }} / 总额 {{ bill.totalAmount }} / 已付 {{ bill.paidAmount }}</span>
                <small>{{ bill.billStatus }} · {{ bill.paymentStatus }}</small>
              </div>
              <div class="row-actions">
                <button @click="loadBillDetail(bill.id)">明细</button>
                <button v-if="bill.billStatus !== 'CANCELLED'" @click="cancelBill(bill.id)">取消</button>
              </div>
            </article>
            <h3>账单明细 / 支付记录</h3>
            <article v-for="item in state.billItems" :key="item.id" class="data-card compact">
              <strong>{{ item.itemName }} · {{ item.amount }}</strong>
              <span>{{ item.itemType }} / {{ item.unitPrice }} x {{ item.quantity }}</span>
            </article>
            <article v-for="pay in state.payments" :key="pay.id" class="data-card compact">
              <strong>{{ pay.paymentNo }} · {{ pay.paymentAmount }}</strong>
              <span>{{ pay.paymentMethod }} / {{ pay.paymentStatus }}</span>
            </article>
          </div>
        </div>

        <div v-if="activeModule === 'search'" class="search-workspace">
          <section class="search-command">
            <div>
              <p class="eyebrow">Search Center</p>
              <h3>替代“字典 + 日志”的检索中心</h3>
              <span>输入患者/药品关键词、患者 ID 或日志模块名，前端会聚合调用多个现有查询接口。</span>
            </div>
            <div class="search-fields">
              <input v-model="filters.searchKeyword" placeholder="患者姓名或药品关键词" />
              <input v-model="filters.searchPatientId" placeholder="患者 ID：查询预约/病历/账单" type="number" />
              <input v-model="filters.searchModuleName" placeholder="日志模块名，如 frontend" />
              <button class="primary-button" type="button" @click="runGlobalSearch">
                <Search :size="16" />
                开始检索
              </button>
            </div>
          </section>
          <div class="search-grid">
            <SearchList title="患者结果" :items="state.search.patients" main-key="name" sub-key="patientNo" />
            <SearchList title="药品结果" :items="state.search.drugs" main-key="drugName" sub-key="drugCode" />
            <SearchList title="预约结果" :items="state.search.appointments" main-key="appointmentNo" sub-key="status" />
            <SearchList title="病历结果" :items="state.search.records" main-key="recordNo" sub-key="diagnosis" />
            <SearchList title="医嘱结果" :items="state.search.orders" main-key="orderNo" sub-key="orderContent" />
            <SearchList title="账单结果" :items="state.search.bills" main-key="billNo" sub-key="paymentStatus" />
            <SearchList title="发药结果" :items="state.search.dispenses" main-key="dispenseNo" sub-key="dispenseStatus" />
            <SearchList title="日志结果" :items="state.search.logs" main-key="moduleName" sub-key="operationType" />
          </div>
        </div>

        <div v-if="activeModule === 'demo'" class="demo-panel">
          <div class="demo-intro">
            <Activity :size="42" />
            <div>
              <h3>一键联调验证</h3>
              <p>通过 API 网关依次验证模块元数据、Nacos 配置中心、Redis 缓存和 Feign 远程调用。</p>
            </div>
            <button class="primary-button" type="button" @click="runDemoChecks">运行验证</button>
          </div>
          <article v-for="result in state.demoResults" :key="result.name" class="result-card">
            <strong>{{ result.name }}</strong>
            <pre>{{ JSON.stringify(result.data, null, 2) }}</pre>
          </article>
        </div>
      </section>

      <div v-if="toast.message" :class="['toast', toast.type]">{{ toast.message }}</div>
    </main>
  </div>
</template>
