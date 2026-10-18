<template>
  <div class="container-fluid py-4 px-md-5">
    <!-- Шапка конфигурации адресов -->
    <div class="row align-items-center mb-4">
      <div class="col-md-6">
        <h2 class="mb-0 text-primary"><i class="bi bi-people-fill me-2"></i>Human Being & Heroes</h2>
        <small class="text-muted">Единый клиент управления персонажами и командами</small>
      </div>
      <div class="col-md-6">
        <div class="card p-2 bg-light mb-0 shadow-sm">
          <div class="row g-2">
            <div class="col-6">
              <label class="form-label small mb-1">URL сервиса персонажей:</label>
              <input type="text" class="form-control form-control-sm" v-model="humanBeingApiUrl" @change="loadHeroes">
            </div>
            <div class="col-6">
              <label class="form-label small mb-1">URL сервиса команд:</label>
              <input type="text" class="form-control form-control-sm" v-model="heroesApiUrl">
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Уведомления об ошибках и успехе -->
    <AlertNotification
      :error="error"
      :success="successMessage"
      @clear-error="error = null"
      @clear-success="successMessage = ''"
    />

    <div class="row">
      <!-- Левая колонка -->
      <div class="col-lg-4">
        <TeamManager
          ref="teamManager"
          :heroes-api-url="heroesApiUrl"
          :human-being-api-url="humanBeingApiUrl"
          @add-cars="onAddCars"
          @notify-success="msg => successMessage = msg"
          @notify-error="err => error = err"
        />
        <ExtraOperations
          :mood-count="moodCount"
          @count-mood="onCountMood"
          @search-contains="onSearchContains"
          @search-prefix="onSearchPrefix"
        />
      </div>

      <!-- Правая колонка -->
      <div class="col-lg-8">
        <HeroTable
          :heroes="heroes"
          :total-count="totalCount"
          :page="page"
          v-model:page-size="pageSize"
          :sort-by="sortBy"
          :sort-dir="sortDir"
          :loading="loading"
          :filters="filters"
          @sort="onSort"
          @edit="onEdit"
          @delete="onDelete"
          @create="onCreate"
          @reset-filters="onResetFilters"
          @filter-change="debouncedLoad"
          @change-page="p => { page = p; loadHeroes(); }"
        />
      </div>
    </div>

    <!-- Модалка -->
    <HeroModal
      ref="heroModal"
      :hero="currentHero"
      :is-edit="isEdit"
      @save="onSaveHero"
    />
  </div>
</template>

<script>
import { CollectionApi } from './api/collectionApi';
import { HeroesApi } from './api/heroesApi';
import AlertNotification from './components/AlertNotification.vue';
import TeamManager from './components/TeamManager.vue';
import ExtraOperations from './components/ExtraOperations.vue';
import HeroTable from './components/HeroTable.vue';
import HeroModal from './components/HeroModal.vue';

export default {
  components: { AlertNotification, TeamManager, ExtraOperations, HeroTable, HeroModal },
  data() {
    return {
      humanBeingApiUrl: 'https://helios.cs.ifmo.ru:28082',
      heroesApiUrl: 'http://localhost:8085',

      heroes: [],
      totalCount: 0,
      page: 1,
      pageSize: 10,
      sortBy: 'id',
      sortDir: 'asc',
      loading: false,

      filters: { name: '', mood: '', weaponType: '', 'car.name': '' },
      error: null,
      successMessage: '',
      moodCount: null,

      isEdit: false,
      currentHero: this.createEmptyHero()
    };
  },
  created() {
    this.colApi = new CollectionApi(() => this.humanBeingApiUrl);
    this.heroesApi = new HeroesApi(() => this.heroesApiUrl);
  },
  mounted() {
    this.loadHeroes();
  },
  methods: {
    createEmptyHero() {
      return {
        name: '',
        coordinates: { x: 0, y: 0 },
        realHero: false,
        hasToothpick: false,
        impactSpeed: 0,
        weaponType: null,
        mood: 'APATHY',
        car: null
      };
    },
    async loadHeroes() {
      this.loading = true;
      try {
        const { items, totalCount } = await this.colApi.getHeroes({
          page: this.page,
          size: this.pageSize,
          sortBy: this.sortBy,
          sortDir: this.sortDir,
          filters: this.filters
        });
        this.heroes = items;
        this.totalCount = totalCount;
      } catch (e) {
        this.error = e;
      } finally {
        this.loading = false;
      }
    },
    debouncedLoad() {
      clearTimeout(this._timer);
      this._timer = setTimeout(() => { this.page = 1; this.loadHeroes(); }, 300);
    },
    onSort(field) {
      if (this.sortBy === field) {
        this.sortDir = this.sortDir === 'asc' ? 'desc' : 'asc';
      } else {
        this.sortBy = field;
        this.sortDir = 'asc';
      }
      this.loadHeroes();
    },
    onResetFilters() {
      this.filters = { name: '', mood: '', weaponType: '', 'car.name': '' };
      this.page = 1;
      this.loadHeroes();
    },
    onCreate() {
      this.isEdit = false;
      this.currentHero = this.createEmptyHero();
      this.$refs.heroModal.show();
    },
    onEdit(hero) {
      this.isEdit = true;
      this.currentHero = JSON.parse(JSON.stringify(hero));
      this.$refs.heroModal.show();
    },
    async onSaveHero(hero) {
      try {
        if (this.isEdit) {
          const updated = await this.colApi.updateHero(hero.id, hero);
          this.successMessage = `Герой #${updated.id} обновлён`;
        } else {
          const created = await this.colApi.createHero(hero);
          this.successMessage = `Герой #${created.id} успешно создан`;
        }
        this.$refs.heroModal.hide();
        this.loadHeroes();
        this.$refs.teamManager.loadMembers();
      } catch (e) {
        this.error = e;
      }
    },
    async onDelete(id) {
      if (!confirm(`Удалить героя #${id}?`)) return;
      try {
        await this.colApi.deleteHero(id);
        this.successMessage = `Герой #${id} удалён`;
        this.loadHeroes();
        this.$refs.teamManager.loadMembers();
      } catch (e) {
        this.error = e;
      }
    },
    async onCountMood(mood) {
      try {
        const { count } = await this.colApi.countMoodLessThan(mood);
        this.moodCount = count;
        this.error = null;
      } catch (e) {
        this.error = e;
      }
    },
    async onSearchContains(sub) {
      try {
        this.heroes = await this.colApi.searchContains(sub);
        this.totalCount = this.heroes.length;
        this.successMessage = `Найдено ${this.heroes.length} записей`;
      } catch (e) {
        this.error = e;
      }
    },
    async onSearchPrefix(prefix) {
      try {
        this.heroes = await this.colApi.searchPrefix(prefix);
        this.totalCount = this.heroes.length;
        this.successMessage = `Найдено ${this.heroes.length} записей`;
      } catch (e) {
        this.error = e;
      }
    },
    async onRemoveHero({ teamId, heroId }) {
      try {
        await this.heroesApi.removeHeroFromTeam(teamId, heroId);
        this.successMessage = `Герой #${heroId} исключён из команды #${teamId}`;
      } catch (e) {
        this.error = e;
      }
    },
    async onAddCars(teamId) {
      try {
        const updated = await this.heroesApi.addCarToTeam(teamId);
        this.successMessage = `Машины выданы героям (изменено: ${updated.length})`;
        this.loadHeroes();
        this.$refs.teamManager.loadMembers();
      } catch (e) {
        this.error = e;
      }
    }
  }
};
</script>
