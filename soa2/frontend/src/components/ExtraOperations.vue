<template>
  <div class="card mb-4 shadow-sm">
    <div class="card-header bg-secondary text-white">
      <i class="bi bi-search me-2"></i><strong>Спец-операции Сервиса 1</strong>
    </div>
    <div class="card-body">
      <!-- 1. Mood Less Than -->
      <div class="mb-3 pb-3 border-bottom">
        <label class="form-label small">Количество с настроением меньше:</label>
        <div class="input-group input-group-sm">
          <select class="form-select" v-model="mood">
            <option value="GLOOM">GLOOM</option>
            <option value="APATHY">APATHY</option>
            <option value="FRENZY">FRENZY</option>
          </select>
          <button class="btn btn-primary" @click="$emit('count-mood', mood)">Считать</button>
        </div>
        <div v-if="moodCount !== null" class="mt-2 text-success small font-weight-bold">
          Результат: <strong>{{ moodCount }}</strong> объектов
        </div>
      </div>

      <!-- 2. Name Contains -->
      <div class="mb-3 pb-3 border-bottom">
        <label class="form-label small">Поиск по вхождению подстроки:</label>
        <div class="input-group input-group-sm">
          <input type="text" class="form-control" v-model="substring" placeholder="Подстрока...">
          <button class="btn btn-outline-secondary" @click="$emit('search-contains', substring)" :disabled="!substring">
            Найти
          </button>
        </div>
      </div>

      <!-- 3. Name Prefix -->
      <div>
        <label class="form-label small">Поиск по префиксу имени:</label>
        <div class="input-group input-group-sm">
          <input type="text" class="form-control" v-model="prefix" placeholder="Префикс...">
          <button class="btn btn-outline-secondary" @click="$emit('search-prefix', prefix)" :disabled="!prefix">
            Найти
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  props: { moodCount: Number },
  emits: ['count-mood', 'search-contains', 'search-prefix'],
  data() {
    return {
      mood: 'APATHY',
      substring: '',
      prefix: ''
    };
  }
};
</script>