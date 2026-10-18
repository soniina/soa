<template>
  <div class="card border-primary mb-4 shadow-sm">
    <div class="card-header bg-primary text-white d-flex justify-content-between align-items-center">
      <div><i class="bi bi-shield-shaded me-2"></i><strong>Сервис команд</strong></div>
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
              <span class="text-muted ms-2">
                {{ m.hero ? (m.hero.car?.name?.trim() || 'Без машины') : 'Данные о машине недоступны' }}
              </span>
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
  import { CollectionApi } from '../api/collectionApi';

  export default {
    props: {
      heroesApiUrl: {
        type: String,
        default: 'http://localhost:8085'
      },
      humanBeingApiUrl: {
        type: String,
        default: 'https://helios.cs.ifmo.ru:28082'
      }
    },
    emits: ['add-cars', 'notify-success', 'notify-error'],
    data() {
      return {
        teamId: 7,
        newHeroId: null,
        members: []
      };
    },
    created() {
      this.collectionApi = new CollectionApi(() => this.humanBeingApiUrl);
    },
    mounted() {
      this.loadMembers();
    },
    watch: {
      heroesApiUrl() { this.loadMembers(); },
      humanBeingApiUrl() { this.loadMembers(); }
    },
    methods: {
      async loadMembers() {
        try {
          const res = await fetch(`${this.heroesApiUrl}/heroes/team/${this.teamId}?t=${Date.now()}`, {
            cache: 'no-store'
          });
          if (!res.ok) {
            throw await res.json().catch(() => ({ message: `Ошибка загрузки команды (${res.status})` }));
          }

          const members = await res.json();
          const heroes = await Promise.allSettled(members.map(member => this.collectionApi.getHero(member.heroId)));
          this.members = members.map((member, index) => ({
            ...member,
            hero: heroes[index].status === 'fulfilled' ? heroes[index].value : null
          }));

          if (heroes.some(hero => hero.status === 'rejected')) {
            this.$emit('notify-error', { message: 'Не удалось загрузить данные о машинах некоторых участников команды' });
          }
        } catch (e) {
          this.members = [];
          this.$emit('notify-error', { message: e.message || 'Не удалось загрузить состав команды' });
        }
      },
      async addHero() {
        if (!this.newHeroId || !this.teamId) return;

        const heroIdToAdd = this.newHeroId;
        console.log(`Отправка запроса: добавление героя #${heroIdToAdd} в команду #${this.teamId}`);

        try {
          const res = await fetch(`${this.heroesApiUrl}/heroes/team/${this.teamId}/add/${heroIdToAdd}`, {
            method: 'POST',
            headers: { 'Accept': 'application/json' }
          });

          const data = await res.json().catch(() => ({}));

          if (!res.ok) {
            console.error("Ошибка от сервера:", data);
            this.$emit('notify-error', data.message ? data : { message: `Ошибка при добавлении: статус ${res.status}` });
            return;
          }

          await this.loadMembers();

          this.$emit('notify-success', `Герой #${heroIdToAdd} успешно добавлен в команду #${this.teamId}`);
          this.newHeroId = null; // Очищаем поле ввода только после успеха
        } catch (e) {
          console.error("Ошибка сети:", e);
          this.$emit('notify-error', { message: 'Не удалось связаться с сервисом команд' });
        }
      },

      async remove(heroId) {
        try {
          const res = await fetch(`${this.heroesApiUrl}/heroes/team/${this.teamId}/remove/${heroId}`, { method: 'DELETE' });
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
