<template>
  <div class="card border-primary mb-4 shadow-sm">
    <div class="card-header bg-primary text-white d-flex justify-content-between align-items-center">
      <div><i class="bi bi-shield-shaded me-2"></i><strong>Сервис 2: Команды</strong></div>
      <span class="badge bg-light text-primary">Team #{{ teamId }}</span>
    </div>
    <div class="card-body">
      <!-- Выбор команды -->
      <div class="mb-3">
        <label class="form-label small">Номер команды (Team ID):</label>
        <div class="input-group input-group-sm">
          <input type="number" class="form-control" v-model.number="teamId" min="1">
          <button class="btn btn-outline-primary" @click="loadMembers">Обновить состав</button>
        </div>
      </div>

      <!-- Состав текущей команды -->
      <div class="mb-3">
        <label class="form-label small d-flex justify-content-between">
          <span>Герои в команде #{{ teamId }}:</span>
          <span class="text-muted">{{ members.length }} участников</span>
        </label>
        <div class="border rounded p-2 bg-light" style="max-height: 120px; overflow-y: auto;">
          <div v-if="members.length === 0" class="text-muted small text-center py-2">
            В команде пока нет героев
          </div>
          <div v-for="m in members" :key="m.heroId" class="d-flex justify-content-between align-items-center mb-1 bg-white p-1 rounded border-sm">
            <span class="small">
              <strong>Герой #{{ m.heroId }}</strong>
              <span v-if="!m.hasCar" class="badge bg-danger ms-1">Без машины</span>
              <span v-else class="badge bg-success ms-1">С машиной</span>
            </span>
            <button class="btn btn-outline-danger btn-sm py-0 px-1" title="Исключить" @click="remove(m.heroId)">
              <i class="bi bi-x"></i>
            </button>
          </div>
        </div>
      </div>

      <!-- Добавить героя в команду -->
      <div class="mb-3">
        <label class="form-label small">Добавить героя в команду #{{ teamId }}:</label>
        <div class="input-group input-group-sm">
          <input type="number" class="form-control" placeholder="ID героя" v-model.number="newHeroId" min="1">
          <button class="btn btn-success" @click="addHero" :disabled="!newHeroId">
            <i class="bi bi-person-plus-fill me-1"></i>Вступить
          </button>
        </div>
      </div>

      <!-- Главная кнопка оркестрации -->
      <div class="d-grid">
        <button class="btn btn-danger btn-sm" @click="$emit('add-cars', teamId)" :disabled="!teamId || members.length === 0">
          <i class="bi bi-car-front me-1"></i>Пересадить команду #{{ teamId }} на Lada Kalina
        </button>
      </div>
    </div>
  </div>
</template>

<script>
  export default {
    props: { s2Url: String },
    emits: ['add-cars', 'notify-success', 'notify-error'],
    data() {
      return {
        teamId: 7,
        newHeroId: null,
        members: []
      };
    },
    mounted() {
      this.loadMembers();
    },
    methods: {
      async loadMembers() {
        try {
          const res = await fetch(`${this.s2Url}/heroes/team/${this.teamId}?t=${Date.now()}`, {
            cache: 'no-store'
          });
          if (res.ok) {
            this.members = await res.json();
          } else {
            this.members = [];
          }
        } catch (e) {
          this.members = [];
        }
      },
      async addHero() {
        if (!this.newHeroId || !this.teamId) return;

        const heroIdToAdd = this.newHeroId;
        console.log(`Отправка запроса: добавление героя #${heroIdToAdd} в команду #${this.teamId}`);

        try {
          const res = await fetch(`${this.s2Url}/heroes/team/${this.teamId}/add/${heroIdToAdd}`, {
            method: 'POST',
            headers: { 'Accept': 'application/json' }
          });

          const data = await res.json().catch(() => ({}));

          if (!res.ok) {
            console.error("Ошибка от сервера:", data);
            this.$emit('notify-error', data.message ? data : { message: `Ошибка при добавлении: статус ${res.status}` });
            return;
          }

          // Если сервер вернул обновлённый массив участников — сразу обновляем список!
          if (Array.isArray(data)) {
            this.members = data;
          } else {
            // Иначе принудительно запрашиваем свежий список участников
            await this.loadMembers();
          }

          this.$emit('notify-success', `Герой #${heroIdToAdd} успешно добавлен в команду #${this.teamId}`);
          this.newHeroId = null; // Очищаем поле ввода только после успеха
        } catch (e) {
          console.error("Ошибка сети:", e);
          this.$emit('notify-error', { message: 'Не удалось связаться со Вторым сервисом (проверьте порт 29081)' });
        }
      },

      async remove(heroId) {
        try {
          const res = await fetch(`${this.s2Url}/heroes/team/${this.teamId}/remove/${heroId}`, { method: 'DELETE' });
          if (res.status === 204) {
            this.members = this.members.filter(m => m.heroId !== heroId);
            this.$emit('notify-success', `Герой #${heroId} исключён из команды #${this.teamId}`);
          } else {
            const err = await res.json();
            this.$emit('notify-error', err);
          }
        } catch (e) {
          this.$emit('notify-error', { message: 'Ошибка сети при удалении героя' });
        }
      }
    }
  };
</script>