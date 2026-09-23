<template>
  <div class="card shadow-sm">
    <div class="card-header bg-white d-flex justify-content-between align-items-center py-3">
      <h5 class="mb-0">Коллекция HumanBeing (Всего: {{ totalCount }})</h5>
      <div>
        <button class="btn btn-outline-secondary btn-sm me-2" @click="$emit('reset-filters')">
          <i class="bi bi-arrow-counterclockwise me-1"></i>Сброс
        </button>
        <button class="btn btn-success btn-sm" @click="$emit('create')">
          <i class="bi bi-plus-circle me-1"></i>Создать героя
        </button>
      </div>
    </div>

    <!-- Фильтры -->
    <div class="card-body bg-light border-bottom">
      <div class="row g-2">
        <div class="col-md-3">
          <input type="text" class="form-control form-control-sm" placeholder="Фильтр: Имя" v-model="filters.name" @input="$emit('filter-change')">
        </div>
        <div class="col-md-3">
          <select class="form-select form-select-sm" v-model="filters.mood" @change="$emit('filter-change')">
            <option value="">Все настроения</option>
            <option value="LONGING">LONGING</option>
            <option value="GLOOM">GLOOM</option>
            <option value="APATHY">APATHY</option>
            <option value="FRENZY">FRENZY</option>
          </select>
        </div>
        <div class="col-md-3">
          <select class="form-select form-select-sm" v-model="filters.weaponType" @change="$emit('filter-change')">
            <option value="">Любое оружие</option>
            <option value="AXE">AXE</option>
            <option value="PISTOL">PISTOL</option>
            <option value="KNIFE">KNIFE</option>
            <option value="BAT">BAT</option>
          </select>
        </div>
        <div class="col-md-3">
          <input type="text" class="form-control form-control-sm" placeholder="Фильтр: Машина" v-model="filters['car.name']" @input="$emit('filter-change')">
        </div>
      </div>
    </div>

    <!-- Таблица -->
    <div class="table-responsive">
      <table class="table table-hover align-middle mb-0">
        <thead class="table-light">
          <tr>
            <th class="cursor-pointer" @click="$emit('sort', 'id')">ID <i :class="sortIcon('id')"></i></th>
            <th class="cursor-pointer" @click="$emit('sort', 'name')">Имя <i :class="sortIcon('name')"></i></th>
            <th>Координаты</th>
            <th class="cursor-pointer" @click="$emit('sort', 'impactSpeed')">Скорость <i :class="sortIcon('impactSpeed')"></i></th>
            <th>Свойства</th>
            <th class="cursor-pointer" @click="$emit('sort', 'weaponType')">Оружие <i :class="sortIcon('weaponType')"></i></th>
            <th class="cursor-pointer" @click="$emit('sort', 'mood')">Настроение <i :class="sortIcon('mood')"></i></th>
            <th>Автомобиль</th>
            <th class="text-end">Действия</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="loading">
            <td colspan="9" class="text-center py-4 text-muted">
              <div class="spinner-border spinner-border-sm me-2"></div>Загрузка данных...
            </td>
          </tr>
          <tr v-else-if="heroes.length === 0">
            <td colspan="9" class="text-center py-4 text-muted">Ничего не найдено</td>
          </tr>
          <tr v-for="h in heroes" :key="h.id">
            <td><strong>#{{ h.id }}</strong></td>
            <td>{{ h.name }}</td>
            <td><small class="text-muted">({{ h.coordinates?.x }}, {{ h.coordinates?.y }})</small></td>
            <td>{{ h.impactSpeed }}</td>
            <td>
              <span v-if="h.realHero" class="badge bg-warning text-dark me-1">Герой</span>
              <span v-if="h.hasToothpick" class="badge bg-info text-dark">Зубочистка</span>
            </td>
            <td>{{ h.weaponType || '—' }}</td>
            <td><span class="badge bg-secondary">{{ h.mood }}</span></td>
            <td>
              {{ h.car?.name || '—' }}
              <i v-if="h.car?.cool" class="bi bi-star-fill text-warning ms-1" title="Крутая!"></i>
            </td>
            <td class="text-end">
              <button class="btn btn-outline-primary btn-sm me-1" @click="$emit('edit', h)">
                <i class="bi bi-pencil"></i>
              </button>
              <button class="btn btn-outline-danger btn-sm" @click="$emit('delete', h.id)">
                <i class="bi bi-trash"></i>
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Пагинация -->
    <div class="card-footer bg-white d-flex justify-content-between align-items-center py-3">
      <div class="d-flex align-items-center">
        <span class="small text-muted me-2">На странице:</span>
        <select class="form-select form-select-sm" style="width: auto;" v-model.number="pageSizeModel">
          <option :value="5">5</option>
          <option :value="10">10</option>
          <option :value="20">20</option>
        </select>
      </div>
      <div>
        <span class="small text-muted me-3">Страница {{ page }} из {{ totalPages || 1 }}</span>
        <div class="btn-group btn-group-sm">
          <button class="btn btn-outline-secondary" :disabled="page <= 1" @click="$emit('change-page', page - 1)">Назад</button>
          <button class="btn btn-outline-secondary" :disabled="page >= totalPages" @click="$emit('change-page', page + 1)">Вперёд</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    heroes: Array,
    totalCount: Number,
    page: Number,
    pageSize: Number,
    sortBy: String,
    sortDir: String,
    loading: Boolean,
    filters: Object
  },
  emits: ['sort', 'edit', 'delete', 'create', 'reset-filters', 'filter-change', 'change-page', 'update:pageSize'],
  computed: {
    totalPages() {
      return Math.ceil(this.totalCount / this.pageSize);
    },
    pageSizeModel: {
      get() { return this.pageSize; },
      set(v) { this.$emit('update:pageSize', v); }
    }
  },
  methods: {
    sortIcon(field) {
      if (this.sortBy !== field) return 'bi bi-arrow-down-up text-muted';
      return this.sortDir === 'asc' ? 'bi bi-sort-down text-primary' : 'bi bi-sort-up text-primary';
    }
  }
};
</script>

<style scoped>
.cursor-pointer { cursor: pointer; user-select: none; }
</style>